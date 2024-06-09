package org.example.userservice.service;

import jakarta.servlet.http.HttpServletResponse;
import org.example.userservice.dto.cart.CartItemRequest;
import org.example.userservice.dto.cart.CartItemResponse;
import org.example.userservice.dto.cart.UpdateQuantityRequest;
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

    void deleteExpiredCarts();

    void order(Authentication auth);
}