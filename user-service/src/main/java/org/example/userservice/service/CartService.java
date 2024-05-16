package org.example.userservice.service;

import org.example.userservice.dto.CartItemRequest;
import org.example.userservice.dto.CartItemResponse;
import org.springframework.security.core.Authentication;

public interface CartService {

    CartItemResponse addItemToCart(Authentication auth, CartItemRequest request);
    void updateItemQuantity(Authentication auth, CartItemRequest request);
    CartItemResponse deleteItemFromCart(Authentication auth, CartItemRequest request);
    void order(Authentication auth);
}
