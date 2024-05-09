package org.example.orderservice.controller;

import org.example.orderservice.dto.order.OrderLongDto;
import org.example.orderservice.dto.order.OrderShortDto;
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
    public ResponseEntity<List<OrderShortDto>> getOrderShortsByUserId(Authentication authentication) {
        List<OrderShortDto> foundOrders = orderService.getUserOrdersShort(authentication);
        return ResponseEntity.ok(foundOrders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderLongDto> getUsersOrderById(
            @PathVariable int orderId,
            Authentication authentication
    ) {
        OrderLongDto requestedOrder = orderService.getUsersOrderLongById(authentication, orderId);
        return ResponseEntity.ok(requestedOrder);
    }
}
