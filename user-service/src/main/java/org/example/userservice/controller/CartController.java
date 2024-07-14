package org.example.userservice.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.userservice.dto.cart.CartContentResponse;
import org.example.userservice.dto.cart.CartItemRequest;
import org.example.userservice.dto.cart.UpdateQuantityRequest;
import org.example.userservice.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/items")
    public ResponseEntity<CartContentResponse> getAllCartItems(@AuthenticationPrincipal Jwt jwt,
                                                               @CookieValue(name = "cartId", required = false) UUID cartId,
                                                               HttpServletResponse response) {

        CartContentResponse cartContent = cartService.getCartItems(jwt, cartId, response);
        return ResponseEntity.ok(cartContent);
    }

    @PostMapping("/items")
    public ResponseEntity<CartContentResponse> addItemToCart(@AuthenticationPrincipal Jwt jwt,
                                                             @RequestBody @Valid CartItemRequest request,
                                                             @CookieValue(name = "cartId", required = false) UUID cartId,
                                                             HttpServletResponse response) {
        CartContentResponse result = cartService.addItemToCart(jwt, request, cartId, response);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PatchMapping("/items/{itemId}")
    public ResponseEntity<CartContentResponse> updateItemQuantity(@AuthenticationPrincipal Jwt jwt,
                                                                  @PathVariable UUID itemId,
                                                                  @RequestBody @Valid UpdateQuantityRequest request,
                                                                  @CookieValue(name = "cartId", required = false) UUID cartId,
                                                                  HttpServletResponse response) {

        CartContentResponse result = cartService.updateItemQuantity(jwt, itemId, request, cartId, response);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartContentResponse> deleteCartItem(@AuthenticationPrincipal Jwt jwt,
                                                              @PathVariable UUID itemId,
                                                              @CookieValue(name = "cartId", required = false) UUID cartId,
                                                              HttpServletResponse response) {

        CartContentResponse result = cartService.deleteItemFromCart(jwt, itemId, cartId, response);
        return ResponseEntity.ok(result);
    }
}