package org.example.orderservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.orderservice.dto.order.OrderDetailsDto;
import org.example.orderservice.dto.order.OrderDto;
import org.example.orderservice.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<Page<OrderDto>> getOrdersByUserId(@AuthenticationPrincipal Jwt jwt,
                                                            Pageable pageable) {

        return ResponseEntity.ok(orderService.getUserOrders(jwt, pageable));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailsDto> getUsersOrderById(@PathVariable Integer orderId,
                                                             @AuthenticationPrincipal Jwt jwt) {

        OrderDetailsDto requestedOrder = orderService.getUserOrderDetailsById(jwt, orderId);
        return ResponseEntity.ok(requestedOrder);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<OrderDetailsDto> deleteUsersOrderById(@PathVariable Integer orderId,
                                                                @AuthenticationPrincipal Jwt jwt) {

        OrderDetailsDto deleteResult = orderService.deleteUserOrderById(jwt, orderId);
        return ResponseEntity.ok(deleteResult);
    }
}