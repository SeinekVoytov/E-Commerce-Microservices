package org.example.orderservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.orderservice.dto.order.OrderDetailsDto;
import org.example.orderservice.dto.order.OrderDto;
import org.example.orderservice.exception.OrderNotFoundException;
import org.example.orderservice.mapper.order.OrderDetailsMapper;
import org.example.orderservice.mapper.order.OrderMapper;
import org.example.orderservice.model.order.Order;
import org.example.orderservice.model.order.OrderDetails;
import org.example.orderservice.repository.OrderDetailsRepository;
import org.example.orderservice.repository.OrderRepository;
import org.example.orderservice.service.OrderService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailsRepository orderDetailsRepository;

    private final OrderMapper orderMapper;
    private final OrderDetailsMapper detailsMapper;

    @Override
    public List<OrderDto> getUserOrdersShort(Authentication auth) {
        UUID userId = retrieveUserIdFromAuthentication(auth);
        List<Order> foundOrders = orderRepository.findOrderShortsByUserId(userId);
        return orderMapper.setToDtos(foundOrders);
    }

    @Override
    public OrderDetailsDto getUsersOrderLongById(Authentication auth, int orderId) {
        UUID userId = retrieveUserIdFromAuthentication(auth);
        OrderDetails requestedOrder = orderDetailsRepository.findOrderLongByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
        return detailsMapper.toDto(requestedOrder);
    }

    @Override
    public OrderDetailsDto deleteUsersOrderById(Authentication auth, int orderId) {
        UUID userId = retrieveUserIdFromAuthentication(auth);
        OrderDetails orderToBeDeleted = orderDetailsRepository.findOrderLongByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException("Order could not be deleted"));
        orderDetailsRepository.delete(orderToBeDeleted);
        return detailsMapper.toDto(orderToBeDeleted);
    }

    private UUID retrieveUserIdFromAuthentication(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return UUID.fromString(jwt.getClaim("sub"));
    }
}