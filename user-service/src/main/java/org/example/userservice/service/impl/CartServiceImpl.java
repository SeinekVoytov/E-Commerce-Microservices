package org.example.userservice.service.impl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.example.userservice.dto.CartItemRequest;
import org.example.userservice.dto.CartItemResponse;
import org.example.userservice.dto.UpdateQuantityRequest;
import org.example.userservice.exception.CartItemNotFoundException;
import org.example.userservice.exception.CartNotFoundException;
import org.example.userservice.exception.InvalidCartIdCookieException;
import org.example.userservice.exception.ProductNotFoundException;
import org.example.userservice.mapper.CartItemMapper;
import org.example.userservice.model.cart.Cart;
import org.example.userservice.model.cart.CartItem;
import org.example.userservice.model.product.ProductLong;
import org.example.userservice.repository.cart.CartItemRepository;
import org.example.userservice.repository.cart.CartRepository;
import org.example.userservice.repository.product.ProductLongRepository;
import org.example.userservice.service.CartService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepo;
    private final CartItemRepository cartItemRepo;
    private final ProductLongRepository productLongRepo;

    private final CartItemMapper cartItemMapper;

    @Override
    public CartItemResponse addItemToCart(Authentication auth,
                                          CartItemRequest request,
                                          UUID cartIdFromCookie,
                                          HttpServletResponse response) {

        if (auth != null) {

            UUID userId = retrieveUserIdFromAuthentication(auth);
            CartItem newCartItem = buildNewCartItem(request.productId(), request.quantity());
            verifyCartIdCookie(cartIdFromCookie, userId, response);

            Optional<Cart> supposedCart = cartRepo.findByUserId(userId).ifPresentOrElse();

            if (supposedCart.isEmpty()) {
                Cart newCart = Cart.builder()
                        .userId(userId)
                        .items(Collections.singletonList(newCartItem))
                        .build();

                cartRepo.save(newCart);
            } else {
                Cart cart = supposedCart.get();
                cart.getItems().add(newCartItem);
                cartRepo.save(cart);
            }

            return cartItemMapper.toResponse(newCartItem);
        }

        return addItemToAnonymousCart(request, cartIdFromCookie, response);
    }

    private CartItemResponse addItemToAnonymousCart(CartItemRequest request,
                                                    UUID cartIdFromCookie,
                                                    HttpServletResponse response) {

        CartItem newCartItem = buildNewCartItem(request.productId(), request.quantity());
        UUID cartId;

        if (cartIdFromCookie == null) {
            Cart newCart = Cart.builder()
                    .items(Collections.singletonList(newCartItem))
                    .build();

            cartId = cartRepo.save(newCart).getId();
        } else {

            Cart anonymousUserCart = cartRepo.findById(cartIdFromCookie)
                    .orElseThrow(() -> new InvalidCartIdCookieException("Invalid cart id cookie parameter"));

            cartId = anonymousUserCart.getId();
            anonymousUserCart.getItems().add(newCartItem);
            cartRepo.save(anonymousUserCart);
        }

        addCartIdCookie(response, cartId);

        return cartItemMapper.toResponse(newCartItem);
    }

    @Override
    public CartItemResponse updateItemQuantity(Authentication auth,
                                               long itemId,
                                               UpdateQuantityRequest request,
                                               UUID cartIdFromCookie,
                                               HttpServletResponse response) {

        CartItem cartItemToBeUpdated = retrieveRequestedCartItem(auth, itemId, cartIdFromCookie, response);
        cartItemToBeUpdated.setQuantity(request.quantity());
        cartItemRepo.save(cartItemToBeUpdated);
        return cartItemMapper.toResponse(cartItemToBeUpdated);
    }

    @Override
    public CartItemResponse deleteItemFromCart(Authentication auth,
                                               long itemId,
                                               UUID cartIdFromCookie,
                                               HttpServletResponse response) {

        CartItem cartItemToBeDeleted = retrieveRequestedCartItem(auth, itemId, cartIdFromCookie, response);
        cartItemRepo.delete(cartItemToBeDeleted);
        return cartItemMapper.toResponse(cartItemToBeDeleted);
    }

    private CartItem retrieveRequestedCartItem(Authentication auth,
                                               long itemId,
                                               UUID cartIdFromCookie,
                                               HttpServletResponse response) {

        Cart cart;
        if (auth != null) {

            UUID userId = retrieveUserIdFromAuthentication(auth);
            verifyCartIdCookie(cartIdFromCookie, userId, response);
            cart = cartRepo.findByUserId(userId)
                    .orElseThrow(
                            () -> new CartNotFoundException(
                                    "No cart associated with this user"
                            )
                    );
        }
        else {

            if (cartIdFromCookie == null) {
                throw new InvalidCartIdCookieException("Invalid cart id cookie parameter");
            }

            cart = cartRepo.findById(cartIdFromCookie).stream()
                    .filter(c -> c.getUserId() == null)
                    .findAny()
                    .orElseThrow(
                            () -> new InvalidCartIdCookieException("Invalid cart id cookie parameter")
                    );
        }

        return cart.getItems().stream()
                .filter(item -> item.getId() == itemId)
                .findAny()
                .orElseThrow(
                        () -> new CartItemNotFoundException("No such item found in user's cart")
                );
    }

    @Override
    @Scheduled(initialDelay = 300_000L, fixedDelay = 300_000L)
    @Transactional
    public void deleteExpiredCarts() {
        LocalDate expiryLocalDate = LocalDate.now().minusDays(1);
        Instant instant = expiryLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Date expiryDate = Date.from(instant);
        cartRepo.deleteByUpdatedAtBefore(expiryDate);
    }

    @Override
    public void order(Authentication auth) {

    }

    private UUID retrieveUserIdFromAuthentication(Authentication auth) {
        Jwt jwt = (Jwt) auth.getPrincipal();
        return UUID.fromString(jwt.getClaimAsString("sub"));
    }

    private CartItem buildNewCartItem(int productId, int quantity) {
        ProductLong productToBeAdded =
                getProductByIdOrThrowException(productId);

        return CartItem.builder()
                .product(productToBeAdded)
                .quantity(quantity)
                .build();
    }

    private ProductLong getProductByIdOrThrowException(int id) {
        return productLongRepo.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product could not be found"));
    }

    private void assignCartToUser(UUID userId, UUID cartId) {

        Cart cartToBeAssigned = cartRepo.findById(cartId)
                .filter(cart -> cart.getUserId() == null)
                .orElseThrow(() -> new InvalidCartIdCookieException("Invalid cart id cookie parameter"));

        cartToBeAssigned.setUserId(userId);
        cartRepo.save(cartToBeAssigned);
    }

    private void verifyCartIdCookie(UUID cookieVal, UUID userId, HttpServletResponse response) {
        if (cookieVal != null) {
            assignCartToUser(userId, cookieVal);
            deleteCartIdCookie(response);
        }
    }

    private void addCartIdCookie(HttpServletResponse response, UUID cartId) {
        Cookie cookie = new Cookie("cartId", cartId.toString());
        cookie.setMaxAge(3600);
        response.addCookie(cookie);
    }

    private void deleteCartIdCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("cartId", "");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}