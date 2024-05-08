package org.example.orderservice.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.orderservice.dto.product.ProductShortDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemShortDto {

    private ProductShortDto product;
    private int quantity;
}
