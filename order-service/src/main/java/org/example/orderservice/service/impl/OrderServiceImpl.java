package org.example.orderservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.orderservice.dto.order.OrderDetailsDto;
import org.example.orderservice.dto.order.OrderDto;
import org.example.orderservice.exception.InvalidQueryParameterException;
import org.example.orderservice.exception.OrderNotFoundException;
import org.example.orderservice.mapper.order.OrderDetailsMapper;
import org.example.orderservice.mapper.order.OrderMapper;
import org.example.orderservice.model.order.OrderDetails;
import org.example.orderservice.repository.OrderDetailsRepository;
import org.example.orderservice.repository.OrderRepository;
import org.example.orderservice.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Set<String> AVAILABLE_SORT_PARAMETERS = Set.of("createdAt");

    private final OrderRepository orderRepository;
    private final OrderDetailsRepository orderDetailsRepository;

    private final OrderMapper orderMapper;
    private final OrderDetailsMapper detailsMapper;

    @Override
    public Page<OrderDto> getUserOrders(Authentication auth,
                                        Pageable pageable) {

        validateSortParameters(pageable.getSort());
        UUID userId = retrieveUserIdFromAuthentication(auth);
        return orderRepository.findAllByUserId(userId, pageable).map(orderMapper::toDto);
    }

    @Override
    public OrderDetailsDto getUserOrderDetailsById(Authentication auth, int orderId) {
        UUID userId = retrieveUserIdFromAuthentication(auth);
        OrderDetails requestedOrder = orderDetailsRepository.findOrderLongByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
        return detailsMapper.toDto(requestedOrder);
    }

    @Override
    public OrderDetailsDto deleteUserOrderById(Authentication auth, int orderId) {
        UUID userId = retrieveUserIdFromAuthentication(auth);
        OrderDetails orderToBeDeleted = orderDetailsRepository.findOrderLongByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException("Order could not be deleted"));
        orderDetailsRepository.delete(orderToBeDeleted);
        return detailsMapper.toDto(orderToBeDeleted);
    }

    private void validateSortParameters(Sort sort) {
        for (Sort.Order order : sort) {
            String property = order.getProperty();
            if (!AVAILABLE_SORT_PARAMETERS.contains(property)) {
                throw new InvalidQueryParameterException("sort", property);
            }
        }
    }

    private UUID retrieveUserIdFromAuthentication(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return UUID.fromString(jwt.getClaim("sub"));
    }
}