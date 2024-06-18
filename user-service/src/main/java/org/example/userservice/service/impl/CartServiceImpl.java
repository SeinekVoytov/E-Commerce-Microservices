package org.example.userservice.service.impl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.userservice.dto.cart.CartContentResponse;
import org.example.userservice.dto.cart.CartItemRequest;
import org.example.userservice.dto.cart.CartItemResponse;
import org.example.userservice.dto.cart.UpdateQuantityRequest;
import org.example.userservice.dto.order.OrderRequest;
import org.example.userservice.dto.order.OrderResponse;
import org.example.userservice.exception.*;
import org.example.userservice.kafka.message.OrderPublishedMessage;
import org.example.userservice.kafka.messagemapper.OrderPublishedMessageMapper;
import org.example.userservice.kafka.producer.OrderPublishedProducer;
import org.example.userservice.mapper.cart.CartContentMapper;
import org.example.userservice.mapper.cart.CartItemMapper;
import org.example.userservice.mapper.order.OrderResponseMapper;
import org.example.userservice.model.cart.Cart;
import org.example.userservice.model.cart.CartItem;
import org.example.userservice.model.product.ProductDetails;
import org.example.userservice.repository.cart.CartItemRepository;
import org.example.userservice.repository.cart.CartRepository;
import org.example.userservice.repository.product.ProductDetailsRepository;
import org.example.userservice.service.CartService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private static final String CART_ID_COOKIE_NAME = "cartId";

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductDetailsRepository productLongRepository;

    private final CartItemMapper cartItemMapper;
    private final CartContentMapper cartContentMapper;
    private final OrderResponseMapper orderResponseMapper;

    private final OrderPublishedProducer orderPublishedProducer;
    private final OrderPublishedMessageMapper orderPublishedMessageMapper;

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
    public CartItemResponse addItemToCart(Jwt jwt,
                                          CartItemRequest request,
                                          UUID cartIdFromCookie,
                                          HttpServletResponse response) {

        if (jwt != null) {

            UUID userId = retrieveUserIdFromJwt(jwt);
            CartItem newCartItem = buildNewCartItem(request.productId(), request.quantity());
            verifyCartIdCookie(cartIdFromCookie, userId, response);

            cartRepository.findByUserId(userId).ifPresentOrElse(cart -> {
                cart.getItems().add(newCartItem);
                cartRepository.save(cart);
            }, () -> {
                Cart newCart = Cart.builder()
                        .userId(userId)
                        .items(Collections.singleton(newCartItem))
                        .build();

                cartRepository.save(newCart);
            });

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
                    .items(Collections.singleton(newCartItem))
                    .build();

            cartId = cartRepository.save(newCart).getId();
        } else {

            Cart anonymousUserCart = cartRepository.findById(cartIdFromCookie)
                    .orElseThrow(() -> new InvalidCartIdCookieException(cartIdFromCookie));

            cartId = anonymousUserCart.getId();
            anonymousUserCart.getItems().add(newCartItem);
            cartRepository.save(anonymousUserCart);
        }

        addCartIdCookie(response, cartId);

        return cartItemMapper.toResponse(newCartItem);
    }

    @Override
    public CartItemResponse updateItemQuantity(Jwt jwt,
                                               int itemId,
                                               UpdateQuantityRequest request,
                                               UUID cartIdFromCookie,
                                               HttpServletResponse response) {

        CartItem cartItemToBeUpdated = retrieveRequestedCartItem(jwt, itemId, cartIdFromCookie, response);
        cartItemToBeUpdated.setQuantity(request.quantity());
        cartItemRepository.save(cartItemToBeUpdated);
        return cartItemMapper.toResponse(cartItemToBeUpdated);
    }

    @Override
    public CartItemResponse deleteItemFromCart(Jwt jwt,
                                               int itemId,
                                               UUID cartIdFromCookie,
                                               HttpServletResponse response) {

        CartItem cartItemToBeDeleted = retrieveRequestedCartItem(jwt, itemId, cartIdFromCookie, response);
        cartItemRepository.delete(cartItemToBeDeleted);
        return cartItemMapper.toResponse(cartItemToBeDeleted);
    }

    private CartItem retrieveRequestedCartItem(Jwt jwt,
                                               int itemId,
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

        return cart.getItems().stream()
                .filter(item -> item.getId() == itemId)
                .findAny()
                .orElseThrow(CartItemNotFoundException::new);
    }

    @Override
    @Scheduled(initialDelay = 300_000L, fixedDelay = 300_000L)
    @Transactional
    public void deleteExpiredCarts() {
        LocalDate expiryLocalDate = LocalDate.now().minusDays(1);
        Instant instant = expiryLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        cartRepository.deleteByUpdatedAtBefore(instant);
    }

    @Override
    public OrderResponse order(Jwt jwt, OrderRequest request) {

        UUID userId = retrieveUserIdFromJwt(jwt);
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(CartNotFoundException::new);

        if (cart.getItems().isEmpty()) {
            throw new CartIsEmptyException();
        }

        CartContentResponse cartContent = cartContentMapper.toResponse(cart);
        clearCart(cart);

        OrderPublishedMessage orderPublishedMessage =
                orderPublishedMessageMapper.mapToMessage(request, cartContent, userId);

        orderPublishedProducer.publishOrder(orderPublishedMessage);

        return orderResponseMapper.mapToOrderResponse(request, cartContent);
    }

    private void clearCart(Cart cart) {
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    private CartItem buildNewCartItem(int productId, int quantity) {
        ProductDetails productToBeAdded =
                getProductByIdOrThrowException(productId);

        return CartItem.builder()
                .product(productToBeAdded)
                .quantity(quantity)
                .build();
    }

    private ProductDetails getProductByIdOrThrowException(int id) {
        return productLongRepository.findById(id)
                .orElseThrow(ProductNotFoundException::new);
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
                Collections.emptySet(),
                Collections.emptyMap()
        );
    }
}