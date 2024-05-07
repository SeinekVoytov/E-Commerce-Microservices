package org.example.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.orderservice.model.order.delivery.DeliveryStatus;
import org.example.orderservice.model.order.delivery.DeliveryType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryDto {

    private DeliveryType type;
    private DeliveryStatus status;
    private FeeDto fee;
}
