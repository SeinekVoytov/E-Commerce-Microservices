package org.example.userservice.service.impl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.userservice.communication.ProductServiceCommunicator;
import org.example.userservice.dto.cart.CartContentResponse;
import org.example.userservice.dto.cart.CartItemRequest;
import org.example.userservice.dto.cart.UpdateQuantityRequest;
import org.example.userservice.dto.product.ProductDetailsDto;
import org.example.userservice.exception.CartNotFoundException;
import org.example.userservice.exception.InvalidCartIdCookieException;
import org.example.userservice.mapper.CartContentMapper;
import org.example.userservice.model.Cart;
import org.example.userservice.model.CartItem;
import org.example.userservice.repository.CartRepository;
import org.example.userservice.service.CartService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private static final String CART_ID_COOKIE_NAME = "cartId";

    private final CartRepository cartRepository;
    private final ProductServiceCommunicator communicator;

    private final CartContentMapper cartContentMapper;

    @Override
    public CartContentResponse getCartItems(Jwt jwt,
                                            UUID cartIdFromCookie,
                                            HttpServletResponse response) {

        Cart cart;
        if (jwt != null) {

            UUID userId = retrieveUserIdFromJwt(jwt);
            verifyCartIdCookie(cartIdFromCookie, userId, response);
            cart = cartRepository.findByUserId(userId)
                    .orElse(null);
        } else {

            if (cartIdFromCookie == null) {
                return emptyCartContentResponse();
            }

            cart = cartRepository.findById(cartIdFromCookie).stream()
                    .filter(c -> c.getUserId() == null)
                    .findAny()
                    .orElseThrow(
                            CartNotFoundException::new
                    );
        }

        return (cart == null)
                ? emptyCartContentResponse()
                : cartContentMapper.toResponse(cart);
    }

    @Override
    public CartContentResponse addItemToCart(Jwt jwt,
                                          CartItemRequest request,
                                          UUID cartIdFromCookie,
                                          HttpServletResponse response) {

        if (jwt != null) {

            UUID userId = retrieveUserIdFromJwt(jwt);
            verifyCartIdCookie(cartIdFromCookie, userId, response);

            Cart cart = cartRepository.findByUserId(userId)
                    .orElseGet(() -> new Cart(userId));

            CartItem newCartItem = buildNewCartItem(request.productId(), request.quantity());
            cart.addItemIdempotently(newCartItem);
            cartRepository.save(cart);

            return cartContentMapper.toResponse(cart);
        }

        return addItemToAnonymousCart(request, cartIdFromCookie, response);
    }

    private CartContentResponse addItemToAnonymousCart(CartItemRequest request,
                                                       UUID cartIdFromCookie,
                                                       HttpServletResponse response) {

        CartItem newCartItem = buildNewCartItem(request.productId(), request.quantity());
        Cart cart;

        if (cartIdFromCookie == null) {
            cart = new Cart(newCartItem);
        } else {

            cart = cartRepository.findById(cartIdFromCookie)
                    .orElseThrow(() -> new InvalidCartIdCookieException(cartIdFromCookie));

            cart.addItemIdempotently(newCartItem);
        }

        cart = cartRepository.save(cart);
        addCartIdCookie(response, cart.getId());

        return cartContentMapper.toResponse(cart);
    }

    @Override
    public CartContentResponse updateItemQuantity(Jwt jwt,
                                                  UUID itemId,
                                                  UpdateQuantityRequest request,
                                                  UUID cartIdFromCookie,
                                                  HttpServletResponse response) {

        Cart cart = retrieveCart(jwt, cartIdFromCookie, response);
        CartItem cartItemToBeUpdated = cart.getItemById(itemId);

        cartItemToBeUpdated.setQuantity(request.quantity());
        cartRepository.save(cart);

        return cartContentMapper.toResponse(cart);
    }


    @Override
    public CartContentResponse deleteItemFromCart(Jwt jwt,
                                                  UUID itemId,
                                                  UUID cartIdFromCookie,
                                                  HttpServletResponse response) {

        Cart cart = retrieveCart(jwt, cartIdFromCookie, response);
        cart.removeItemById(itemId);
        cartRepository.save(cart);
        return cartContentMapper.toResponse(cart);
    }

    @Override
    public CartContentResponse clearCart(Jwt jwt,
                                         UUID cartIdFromCookie,
                                         HttpServletResponse response) {

        Cart cart = retrieveCart(jwt, cartIdFromCookie, response);
        clearCart(cart);
        return cartContentMapper.toResponse(cart);
    }

    private Cart retrieveCart(Jwt jwt,
                              UUID cartIdFromCookie,
                              HttpServletResponse response) {

        Cart cart;
        if (jwt != null) {

            UUID userId = retrieveUserIdFromJwt(jwt);
            verifyCartIdCookie(cartIdFromCookie, userId, response);
            cart = cartRepository.findByUserId(userId)
                    .orElseThrow(CartNotFoundException::new);
        } else {

            if (cartIdFromCookie == null) {
                throw new InvalidCartIdCookieException(null);
            }

            cart = cartRepository.findById(cartIdFromCookie).stream()
                    .filter(c -> c.getUserId() == null)
                    .findAny()
                    .orElseThrow(
                            () -> new InvalidCartIdCookieException(cartIdFromCookie)
                    );
        }

        return cart;
    }

    private void clearCart(Cart cart) {
        cart.clear();
        cartRepository.save(cart);
    }

    private CartItem buildNewCartItem(int productId, int quantity) {
        ProductDetailsDto productToBeAdded =
                getProductByIdOrThrowException(productId);

        return CartItem.builder()
                .product(productToBeAdded)
                .quantity(quantity)
                .build();
    }

    private ProductDetailsDto getProductByIdOrThrowException(int id) {
        return communicator.getProductById(id);
    }

    private void assignCartToUser(UUID userId, UUID cartId) {

        Cart cartToBeAssigned = cartRepository.findById(cartId)
                .filter(cart -> cart.getUserId() == null)
                .orElseThrow(() -> new InvalidCartIdCookieException(userId));

        cartToBeAssigned.setUserId(userId);
        cartRepository.save(cartToBeAssigned);
    }

    private void verifyCartIdCookie(UUID cookieVal, UUID userId, HttpServletResponse response) {
        if (cookieVal != null) {
            assignCartToUser(userId, cookieVal);
            deleteCartIdCookie(response);
        }
    }

    private void addCartIdCookie(HttpServletResponse response, UUID cartId) {
        Cookie cookie = new Cookie(CART_ID_COOKIE_NAME, cartId.toString());
        cookie.setMaxAge(3600);
        response.addCookie(cookie);
    }

    private void deleteCartIdCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(CART_ID_COOKIE_NAME, "");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private CartContentResponse emptyCartContentResponse() {
        return new CartContentResponse(
                Collections.emptyList(),
                Collections.emptyMap(),
                0
        );
    }
}