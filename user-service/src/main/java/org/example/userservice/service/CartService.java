package org.example.userservice.service;

import jakarta.servlet.http.HttpServletResponse;
import org.example.userservice.dto.cart.CartContentResponse;
import org.example.userservice.dto.cart.CartItemRequest;
import org.example.userservice.dto.cart.UpdateQuantityRequest;
import org.example.userservice.dto.order.OrderRequest;
import org.example.userservice.dto.order.OrderResponse;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

public interface CartService {

    CartContentResponse getCartItems(Jwt jwt,
                                     UUID cartIdFromCookie,
                                     HttpServletResponse response);

    CartContentResponse addItemToCart(Jwt jwt,
                                   CartItemRequest request,
                                   UUID cartIdFromCookie,
                                   HttpServletResponse response);

    CartContentResponse updateItemQuantity(Jwt jwt,
                                        int itemId,
                                        UpdateQuantityRequest request,
                                        UUID cartIdFromCookie,
                                        HttpServletResponse response);

    CartContentResponse deleteItemFromCart(Jwt jwt,
                                        int itemId,
                                        UUID cartIdFromCookie,
                                        HttpServletResponse response);

    void deleteExpiredCarts();

    OrderResponse order(Jwt jwt, OrderRequest request);

    default UUID retrieveUserIdFromJwt(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}