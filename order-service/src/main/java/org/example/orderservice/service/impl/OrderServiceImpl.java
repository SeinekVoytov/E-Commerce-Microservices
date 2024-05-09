package org.example.orderservice.service.impl;

import org.example.orderservice.dto.order.OrderShortDto;
import org.example.orderservice.mapper.OrderShortMapper;
import org.example.orderservice.model.order.OrderShort;
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
    private final OrderShortMapper orderShortMapper;

    @Autowired
    public OrderServiceImpl(OrderShortRepository orderShortRepository, OrderShortMapper orderShortMapper) {
        this.orderShortRepository = orderShortRepository;
        this.orderShortMapper = orderShortMapper;
    }

    @Override
    public List<OrderShortDto> getUserOrdersShort(Authentication authentication) {
        UUID userId = retrieveUserIdFromAuthentication(authentication);
        List<OrderShort> foundOrders = orderShortRepository.findOrderShortsByUserId(userId);
        return orderShortMapper.listToDtos(foundOrders);
    }

    private UUID retrieveUserIdFromAuthentication(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return UUID.fromString(jwt.getClaim("sub"));
    }
}
