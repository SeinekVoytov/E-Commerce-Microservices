package org.example.userservice.service;

import jakarta.servlet.http.HttpServletResponse;
import org.example.userservice.dto.cart.CartItemRequest;
import org.example.userservice.dto.cart.CartItemResponse;
import org.example.userservice.dto.cart.UpdateQuantityRequest;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

public interface CartService {

    CartItemResponse addItemToCart(Jwt jwt,
                                   CartItemRequest request,
                                   UUID cartIdFromCookie,
                                   HttpServletResponse response);

    CartItemResponse updateItemQuantity(Jwt jwt,
                                        long itemId,
                                        UpdateQuantityRequest request,
                                        UUID cartIdFromCookie,
                                        HttpServletResponse response);

    CartItemResponse deleteItemFromCart(Jwt jwt,
                                        long itemId,
                                        UUID cartIdFromCookie,
                                        HttpServletResponse response);

    void deleteExpiredCarts();

    void order(Jwt jwt);

    default UUID retrieveUserIdFromJwt(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}