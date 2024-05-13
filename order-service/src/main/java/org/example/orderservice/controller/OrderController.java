package org.example.orderservice.controller;

import org.example.orderservice.dto.order.OrderDetailsDto;
import org.example.orderservice.dto.order.OrderDto;
import org.example.orderservice.repository.OrderDetailsRepository;
import org.example.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> getOrderShortsByUserId(Authentication authentication) {
        List<OrderDto> foundOrders = orderService.getUserOrdersShort(authentication);
        return ResponseEntity.ok(foundOrders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailsDto> getUsersOrderById(
            @PathVariable int orderId,
            Authentication authentication
    ) {
        OrderDetailsDto requestedOrder = orderService.getUsersOrderLongById(authentication, orderId);
        return ResponseEntity.ok(requestedOrder);
    }
}
