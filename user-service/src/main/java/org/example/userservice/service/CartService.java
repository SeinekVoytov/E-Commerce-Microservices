package org.example.userservice.service;

import jakarta.servlet.http.HttpServletResponse;
import org.example.userservice.dto.cart.CartContentResponse;
import org.example.userservice.dto.cart.CartItemRequest;
import org.example.userservice.dto.cart.UpdateQuantityRequest;
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
                                        UUID itemId,
                                        UpdateQuantityRequest request,
                                        UUID cartIdFromCookie,
                                        HttpServletResponse response);

    CartContentResponse deleteItemFromCart(Jwt jwt,
                                        UUID itemId,
                                        UUID cartIdFromCookie,
                                        HttpServletResponse response);

    CartContentResponse clearCart(Jwt jwt,
                                  UUID cartIdFromCookie,
                                  HttpServletResponse response);

    default UUID retrieveUserIdFromJwt(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}