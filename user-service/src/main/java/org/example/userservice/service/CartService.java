package org.example.userservice.service;

import jakarta.servlet.http.HttpServletResponse;
import org.example.userservice.dto.CartItemRequest;
import org.example.userservice.dto.CartItemResponse;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public interface CartService {

    CartItemResponse addItemToCart(Authentication auth,
                                   CartItemRequest request,
                                   UUID cartIdFromCookie,
                                   HttpServletResponse response);

    void updateItemQuantity(Authentication auth, CartItemRequest request);
    CartItemResponse deleteItemFromCart(Authentication auth, CartItemRequest request);
    void order(Authentication auth);
}