package org.example.orderservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.orderservice.dto.order.OrderDetailsDto;
import org.example.orderservice.dto.order.OrderDto;
import org.example.orderservice.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<Page<OrderDto>> getOrderShortsByUserId(Authentication auth,
                                                                 Pageable pageable) {

        return ResponseEntity.ok(orderService.getUserOrders(auth, pageable));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailsDto> getUsersOrderById(@PathVariable int orderId,
                                                             Authentication auth) {

        OrderDetailsDto requestedOrder = orderService.getUserOrderDetailsById(auth, orderId);
        return ResponseEntity.ok(requestedOrder);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<OrderDetailsDto> deleteUsersOrderById(@PathVariable int orderId,
                                                                Authentication auth) {

        OrderDetailsDto deleteResult = orderService.deleteUserOrderById(auth, orderId);
        return ResponseEntity.ok(deleteResult);
    }
}