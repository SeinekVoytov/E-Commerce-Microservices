package org.example.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderLongDto {

    private int id;
    private UUID userId;
    private DeliveryDto delivery;
    private AddressDto address;
    private List<OrderItemLongDto> items;
}
