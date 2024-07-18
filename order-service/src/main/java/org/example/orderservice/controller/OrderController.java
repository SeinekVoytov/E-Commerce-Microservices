package org.example.orderservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.orderservice.dto.order.OrderDetailsResponse;
import org.example.orderservice.dto.order.OrderRequest;
import org.example.orderservice.dto.order.OrderResponse;
import org.example.orderservice.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getOrdersByUserId(@AuthenticationPrincipal Jwt jwt,
                                                                 Pageable pageable) {

        return ResponseEntity.ok(orderService.getUserOrders(jwt, pageable));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailsResponse> getUsersOrderById(@PathVariable Integer orderId,
                                                                  @AuthenticationPrincipal Jwt jwt) {

        OrderDetailsResponse requestedOrder = orderService.getUserOrderDetailsById(jwt, orderId);
        return ResponseEntity.ok(requestedOrder);
    }

    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<OrderDetailsResponse> deleteUsersOrderById(@PathVariable Integer orderId,
                                                                     @AuthenticationPrincipal Jwt jwt) {

        OrderDetailsResponse deleteResult = orderService.deleteUserOrderById(jwt, orderId);
        return ResponseEntity.ok(deleteResult);
    }

    @PostMapping
    public ResponseEntity<OrderDetailsResponse> order(@RequestBody @Valid OrderRequest request,
                                                      @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(orderService.createOrder(jwt, request));
    }
}