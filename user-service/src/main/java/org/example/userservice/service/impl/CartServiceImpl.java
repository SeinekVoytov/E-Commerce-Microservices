package org.example.userservice.service.impl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.example.userservice.dto.CartItemRequest;
import org.example.userservice.dto.CartItemResponse;
import org.example.userservice.exception.InvalidCartIdCookieException;
import org.example.userservice.exception.ProductNotFoundException;
import org.example.userservice.mapper.CartItemMapper;
import org.example.userservice.model.cart.Cart;
import org.example.userservice.model.cart.CartItem;
import org.example.userservice.model.product.ProductLong;
import org.example.userservice.repository.cart.CartRepository;
import org.example.userservice.repository.product.ProductLongRepository;
import org.example.userservice.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductLongRepository productLongRepository;

    private final CartItemMapper cartItemMapper;

    @Autowired
    public CartServiceImpl(CartRepository cartRepository,
                           ProductLongRepository productLongRepository,
                           CartItemMapper cartItemMapper) {

        this.cartRepository = cartRepository;
        this.productLongRepository = productLongRepository;
        this.cartItemMapper = cartItemMapper;
    }

    @Override
    public CartItemResponse addItemToCart(Authentication auth,
                                          CartItemRequest request,
                                          UUID cartIdFromCookie,
                                          HttpServletResponse response) {

        if (auth != null) {

            UUID userId = retrieveUserIdFromAuthentication(auth);
            if (cartIdFromCookie != null) {
                assignCartToUser(userId, cartIdFromCookie);
                deleteCartIdCookie(response);
            }

            CartItem newCartItem = buildNewCartItem(request.productId(), request.quantity());
            Optional<Cart> supposedCart = cartRepository.findByUserId(userId);

            if (supposedCart.isEmpty()) {
                Cart newCart = Cart.builder()
                        .userId(userId)
                        .items(Collections.singletonList(newCartItem))
                        .build();

                cartRepository.save(newCart);
            } else {
                Cart cart = supposedCart.get();
                cart.getItems().add(newCartItem);
                cartRepository.save(cart);
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

            cartId = cartRepository.save(newCart).getId();
        } else {

            Cart anonymousUserCart = cartRepository.findById(cartIdFromCookie)
                    .orElseThrow(() -> new InvalidCartIdCookieException("Invalid cart id cookie parameter"));

            cartId = anonymousUserCart.getId();
            anonymousUserCart.getItems().add(newCartItem);
            cartRepository.save(anonymousUserCart);
        }

        addCartIdCookie(response, cartId);

        return cartItemMapper.toResponse(newCartItem);
    }

    @Override
    public void updateItemQuantity(Authentication auth, CartItemRequest request) {

    }

    @Override
    public CartItemResponse deleteItemFromCart(Authentication auth, CartItemRequest request) {
        return null;
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
        return productLongRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product could not be found"));
    }

    private void assignCartToUser(UUID userId, UUID cartId) {

        Cart cartToBeAssigned = cartRepository.findById(cartId)
                .filter(cart -> cart.getUserId() != null)
                .orElseThrow(() -> new InvalidCartIdCookieException("Invalid cart id cookie parameter"));

        cartToBeAssigned.setUserId(userId);
        cartRepository.save(cartToBeAssigned);
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