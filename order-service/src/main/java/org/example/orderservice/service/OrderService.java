package org.example.orderservice.service;

import org.example.orderservice.dto.order.OrderShortDto;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface OrderService {

    List<OrderShortDto> getUserOrdersShort(Authentication authentication);
}
