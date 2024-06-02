package org.example.userservice.service;

import jakarta.servlet.http.HttpServletResponse;
import org.example.userservice.dto.CartItemRequest;
import org.example.userservice.dto.CartItemResponse;
import org.example.userservice.dto.UpdateQuantityRequest;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public interface CartService {

    CartItemResponse addItemToCart(Authentication auth,
                                   CartItemRequest request,
                                   UUID cartIdFromCookie,
                                   HttpServletResponse response);

    CartItemResponse updateItemQuantity(Authentication auth,
                                        long itemId,
                                        UpdateQuantityRequest request,
                                        UUID cartIdFromCookie,
                                        HttpServletResponse response);

    CartItemResponse deleteItemFromCart(Authentication auth,
                                        long itemId,
                                        UUID cartIdFromCookie,
                                        HttpServletResponse response);
    void order(Authentication auth);
}