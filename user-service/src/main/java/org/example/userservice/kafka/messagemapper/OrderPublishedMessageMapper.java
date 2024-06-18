package org.example.userservice.kafka.messagemapper;

import org.example.userservice.dto.cart.CartContentResponse;
import org.example.userservice.dto.order.OrderRequest;
import org.example.userservice.kafka.message.OrderPublishedMessage;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface OrderPublishedMessageMapper {

    OrderPublishedMessage mapToMessage(OrderRequest orderRequest,
                                       CartContentResponse cartContent,
                                       UUID userId);
}