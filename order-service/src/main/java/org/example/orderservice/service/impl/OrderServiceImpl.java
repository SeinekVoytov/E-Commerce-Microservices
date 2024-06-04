package org.example.orderservice.service.impl;

import lombok.AllArgsConstructor;
import org.example.orderservice.dto.order.OrderDetailsDto;
import org.example.orderservice.dto.order.OrderDto;
import org.example.orderservice.exception.OrderNotFoundException;
import org.example.orderservice.mapper.OrderDetailsMapper;
import org.example.orderservice.mapper.OrderMapper;
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
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepo;
    private final OrderDetailsRepository orderDetailsRepo;

    private final OrderMapper orderMapper;
    private final OrderDetailsMapper detailsMapper;

    @Override
    public List<OrderDto> getUserOrdersShort(Authentication auth) {
        UUID userId = retrieveUserIdFromAuthentication(auth);
        List<Order> foundOrders = orderRepo.findOrderShortsByUserId(userId);
        return orderMapper.listToDtos(foundOrders);
    }

    @Override
    public OrderDetailsDto getUsersOrderLongById(Authentication auth, int orderId) {
        UUID userId = retrieveUserIdFromAuthentication(auth);
        OrderDetails requestedOrder = orderDetailsRepo.findOrderLongByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
        return detailsMapper.toDto(requestedOrder);
    }

    @Override
    public OrderDetailsDto deleteUsersOrderById(Authentication auth, int orderId) {
        UUID userId = retrieveUserIdFromAuthentication(auth);
        OrderDetails orderToBeDeleted = orderDetailsRepo.findOrderLongByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException("Order could not be deleted"));
        orderDetailsRepo.delete(orderToBeDeleted);
        return detailsMapper.toDto(orderToBeDeleted);
    }

    private UUID retrieveUserIdFromAuthentication(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return UUID.fromString(jwt.getClaim("sub"));
    }
}
