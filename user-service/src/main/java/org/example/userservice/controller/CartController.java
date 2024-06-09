package org.example.userservice.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.userservice.dto.cart.CartItemRequest;
import org.example.userservice.dto.cart.CartItemResponse;
import org.example.userservice.dto.cart.UpdateQuantityRequest;
import org.example.userservice.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<CartItemResponse> addItemToCart(Principal principal,
                                                          @RequestBody CartItemRequest request,
                                                          @CookieValue(name = "cartId", required = false) UUID cartId,
                                                          HttpServletResponse response) {
        CartItemResponse result = cartService.addItemToCart(principal, request, cartId, response);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PatchMapping("/update/{itemId}")
    public ResponseEntity<CartItemResponse> updateItemQuantity(Authentication auth,
                                                               @PathVariable long itemId,
                                                               @RequestBody UpdateQuantityRequest request,
                                                               @CookieValue(name = "cartId", required = false) UUID cartId,
                                                               HttpServletResponse response) {

        CartItemResponse result = cartService.updateItemQuantity(auth, itemId, request, cartId, response);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{itemId}")
    public ResponseEntity<CartItemResponse> deleteCartItem(Authentication auth,
                                                           @PathVariable long itemId,
                                                           @CookieValue(name = "cartId", required = false) UUID cartId,
                                                           HttpServletResponse response) {

        CartItemResponse result = cartService.deleteItemFromCart(auth, itemId, cartId, response);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}