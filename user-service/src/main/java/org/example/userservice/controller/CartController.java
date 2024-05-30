package org.example.userservice.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.example.userservice.dto.CartItemRequest;
import org.example.userservice.dto.CartItemResponse;
import org.example.userservice.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public ResponseEntity<CartItemResponse> addItemToCart(Authentication auth,
                                                          @RequestBody CartItemRequest request,
                                                          @CookieValue(name = "cartId", required = false) UUID cartId,
                                                          HttpServletResponse response) {
        CartItemResponse result = cartService.addItemToCart(auth, request, cartId, response);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PutMapping("/quantity")
    public ResponseEntity<CartItemResponse> updateItemQuantity(Authentication auth,
                                                               @RequestBody CartItemRequest request,
                                                               @CookieValue(name = "cartId", required = false) UUID cartId,
                                                               HttpServletResponse response) {

        CartItemResponse result = cartService.updateItemQuantity(auth, request, cartId, response);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }
}