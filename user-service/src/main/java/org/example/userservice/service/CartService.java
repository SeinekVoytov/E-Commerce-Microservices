package org.example.userservice.service;

import jakarta.servlet.http.HttpServletResponse;
import org.example.userservice.dto.cart.CartItemRequest;
import org.example.userservice.dto.cart.CartItemResponse;
import org.example.userservice.dto.cart.UpdateQuantityRequest;
import org.springframework.security.oauth2.jwt.Jwt;

import java.security.Principal;
import java.util.UUID;

public interface CartService {

    CartItemResponse addItemToCart(Principal principal,
                                   CartItemRequest request,
                                   UUID cartIdFromCookie,
                                   HttpServletResponse response);

    CartItemResponse updateItemQuantity(Principal principal,
                                        long itemId,
                                        UpdateQuantityRequest request,
                                        UUID cartIdFromCookie,
                                        HttpServletResponse response);

    CartItemResponse deleteItemFromCart(Principal principal,
                                        long itemId,
                                        UUID cartIdFromCookie,
                                        HttpServletResponse response);

    void deleteExpiredCarts();

    void order(Principal principal);

    default UUID retrieveUserIdFromPrincipal(Principal principal) {
        Jwt jwt = (Jwt) principal;
        return UUID.fromString(jwt.getClaimAsString("sub"));
    }
}