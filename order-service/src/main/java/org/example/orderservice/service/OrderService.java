package org.example.orderservice.service;

import org.example.orderservice.dto.order.OrderDetailsDto;
import org.example.orderservice.dto.order.OrderDto;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface OrderService {

    List<OrderDto> getUserOrdersShort(Authentication auth);

    OrderDetailsDto getUsersOrderLongById(Authentication auth, int orderId);

    OrderDetailsDto deleteUsersOrderById(Authentication auth, int orderId);
}
