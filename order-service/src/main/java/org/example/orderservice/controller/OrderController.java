package org.example.orderservice.controller;

import lombok.AllArgsConstructor;
import org.example.orderservice.dto.order.OrderDetailsDto;
import org.example.orderservice.dto.order.OrderDto;
import org.example.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@AllArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderDto>> getOrderShortsByUserId(Authentication authentication) {
        List<OrderDto> foundOrders = orderService.getUserOrdersShort(authentication);
        return ResponseEntity.ok(foundOrders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailsDto> getUsersOrderById(@PathVariable int orderId,
                                                             Authentication authentication) {
        OrderDetailsDto requestedOrder = orderService.getUsersOrderLongById(authentication, orderId);
        return ResponseEntity.ok(requestedOrder);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<OrderDetailsDto> deleteUsersOrderById(@PathVariable int orderId,
                                                                Authentication authentication) {
        OrderDetailsDto deleteResult = orderService.deleteUsersOrderById(authentication, orderId);
        return ResponseEntity.ok(deleteResult);
    }
}