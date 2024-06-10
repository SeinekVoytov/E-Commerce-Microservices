package org.example.userservice.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.userservice.dto.cart.CartContentResponse;
import org.example.userservice.dto.cart.CartItemRequest;
import org.example.userservice.dto.cart.CartItemResponse;
import org.example.userservice.dto.cart.UpdateQuantityRequest;
import org.example.userservice.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartContentResponse> getAllCartItems(@AuthenticationPrincipal Jwt jwt,
                                                                 @CookieValue(name = "cartId", required = false) UUID cartId,
                                                                 HttpServletResponse response) {

        CartContentResponse cartContent = cartService.getCartItems(jwt, cartId, response);
        return ResponseEntity.ok(cartContent);
    }

    @PostMapping("/add")
    public ResponseEntity<CartItemResponse> addItemToCart(@AuthenticationPrincipal Jwt jwt,
                                                          @RequestBody CartItemRequest request,
                                                          @CookieValue(name = "cartId", required = false) UUID cartId,
                                                          HttpServletResponse response) {
        CartItemResponse result = cartService.addItemToCart(jwt, request, cartId, response);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PatchMapping("/update/{itemId}")
    public ResponseEntity<CartItemResponse> updateItemQuantity(@AuthenticationPrincipal Jwt jwt,
                                                               @PathVariable int itemId,
                                                               @RequestBody UpdateQuantityRequest request,
                                                               @CookieValue(name = "cartId", required = false) UUID cartId,
                                                               HttpServletResponse response) {

        CartItemResponse result = cartService.updateItemQuantity(jwt, itemId, request, cartId, response);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{itemId}")
    public ResponseEntity<CartItemResponse> deleteCartItem(@AuthenticationPrincipal Jwt jwt,
                                                           @PathVariable int itemId,
                                                           @CookieValue(name = "cartId", required = false) UUID cartId,
                                                           HttpServletResponse response) {

        CartItemResponse result = cartService.deleteItemFromCart(jwt, itemId, cartId, response);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}