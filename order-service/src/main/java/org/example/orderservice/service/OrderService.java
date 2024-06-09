package org.example.orderservice.service;

import org.example.orderservice.dto.order.OrderDetailsDto;
import org.example.orderservice.dto.order.OrderDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

public interface OrderService {

    Page<OrderDto> getUserOrders(Jwt jwt, Pageable pageable);

    OrderDetailsDto getUserOrderDetailsById(Jwt jwt, int orderId);

    OrderDetailsDto deleteUserOrderById(Jwt jwt, int orderId);

    default UUID retrieveUserIdFromJwt(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
