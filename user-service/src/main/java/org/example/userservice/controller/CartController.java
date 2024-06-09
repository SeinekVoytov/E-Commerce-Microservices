package org.example.userservice.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.example.userservice.dto.cart.CartItemRequest;
import org.example.userservice.dto.cart.CartItemResponse;
import org.example.userservice.dto.cart.UpdateQuantityRequest;
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

    @PutMapping("/update/{itemId}")
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