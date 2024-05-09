package org.example.orderservice.service.impl;

import org.example.orderservice.dto.order.OrderLongDto;
import org.example.orderservice.dto.order.OrderShortDto;
import org.example.orderservice.exception.OrderNotFoundException;
import org.example.orderservice.mapper.OrderLongMapper;
import org.example.orderservice.mapper.OrderShortMapper;
import org.example.orderservice.model.order.OrderLong;
import org.example.orderservice.model.order.OrderShort;
import org.example.orderservice.repository.OrderLongRepository;
import org.example.orderservice.repository.OrderShortRepository;
import org.example.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderShortRepository orderShortRepository;
    private final OrderLongRepository orderLongRepository;

    private final OrderShortMapper orderShortMapper;
    private final OrderLongMapper orderLongMapper;

    @Autowired
    public OrderServiceImpl(
            OrderShortRepository orderShortRepository,
            OrderLongRepository orderLongRepository,
            OrderShortMapper orderShortMapper,
            OrderLongMapper orderLongMapper
    ) {
        this.orderShortRepository = orderShortRepository;
        this.orderLongRepository = orderLongRepository;
        this.orderShortMapper = orderShortMapper;
        this.orderLongMapper = orderLongMapper;
    }

    @Override
    public List<OrderShortDto> getUserOrdersShort(Authentication authentication) {
        UUID userId = retrieveUserIdFromAuthentication(authentication);
        List<OrderShort> foundOrders = orderShortRepository.findOrderShortsByUserId(userId);
        return orderShortMapper.listToDtos(foundOrders);
    }

    @Override
    public OrderLongDto getUsersOrderLongById(Authentication authentication, int orderId) {
        UUID userId = retrieveUserIdFromAuthentication(authentication);
        OrderLong requestedOrder = orderLongRepository.findOrderLongByIdAndUserId(orderId, userId).orElseThrow(() -> new OrderNotFoundException("Order could not be found"));
        return orderLongMapper.toDto(requestedOrder);
    }

    private UUID retrieveUserIdFromAuthentication(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return UUID.fromString(jwt.getClaim("sub"));
    }
}
