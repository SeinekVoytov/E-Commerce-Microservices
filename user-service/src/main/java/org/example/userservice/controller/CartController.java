package org.example.userservice.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.userservice.dto.cart.CartContentResponse;
import org.example.userservice.dto.cart.CartItemRequest;
import org.example.userservice.dto.cart.UpdateQuantityRequest;
import org.example.userservice.dto.order.OrderRequest;
import org.example.userservice.dto.order.OrderResponse;
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
    public ResponseEntity<CartContentResponse> addItemToCart(@AuthenticationPrincipal Jwt jwt,
                                                             @RequestBody CartItemRequest request,
                                                             @CookieValue(name = "cartId", required = false) UUID cartId,
                                                             HttpServletResponse response) {
        CartContentResponse result = cartService.addItemToCart(jwt, request, cartId, response);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PatchMapping("/update/{itemId}")
    public ResponseEntity<CartContentResponse> updateItemQuantity(@AuthenticationPrincipal Jwt jwt,
                                                                  @PathVariable Integer itemId,
                                                                  @RequestBody UpdateQuantityRequest request,
                                                                  @CookieValue(name = "cartId", required = false) UUID cartId,
                                                                  HttpServletResponse response) {

        CartContentResponse result = cartService.updateItemQuantity(jwt, itemId, request, cartId, response);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/delete/{itemId}")
    public ResponseEntity<CartContentResponse> deleteCartItem(@AuthenticationPrincipal Jwt jwt,
                                                              @PathVariable Integer itemId,
                                                              @CookieValue(name = "cartId", required = false) UUID cartId,
                                                              HttpServletResponse response) {

        CartContentResponse result = cartService.deleteItemFromCart(jwt, itemId, cartId, response);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/order")
    public ResponseEntity<OrderResponse> order(@AuthenticationPrincipal Jwt jwt,
                                               @RequestBody OrderRequest request) {

        return new ResponseEntity<>(cartService.order(jwt, request), HttpStatus.CREATED);
    }
}