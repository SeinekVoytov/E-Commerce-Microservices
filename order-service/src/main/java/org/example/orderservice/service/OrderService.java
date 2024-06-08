package org.example.orderservice.service;

import org.example.orderservice.dto.order.OrderDetailsDto;
import org.example.orderservice.dto.order.OrderDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface OrderService {

    Page<OrderDto> getUserOrders(Authentication auth, Pageable pageable);

    OrderDetailsDto getUserOrderDetailsById(Authentication auth, int orderId);

    OrderDetailsDto deleteUserOrderById(Authentication auth, int orderId);
}
