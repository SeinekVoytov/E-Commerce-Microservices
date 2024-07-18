package org.example.orderservice.service;

import org.example.orderservice.dto.order.OrderDetailsResponse;
import org.example.orderservice.dto.order.OrderRequest;
import org.example.orderservice.dto.order.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

public interface OrderService {

    Page<OrderResponse> getUserOrders(Jwt jwt, Pageable pageable);

    OrderDetailsResponse getUserOrderDetailsById(Jwt jwt, int orderId);

    OrderDetailsResponse deleteUserOrderById(Jwt jwt, int orderId);

    OrderDetailsResponse createOrder(Jwt jwt, OrderRequest request);

    default UUID retrieveUserIdFromJwt(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
