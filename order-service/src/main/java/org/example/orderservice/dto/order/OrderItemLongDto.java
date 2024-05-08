package org.example.orderservice.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.orderservice.dto.product.ProductLongDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemLongDto {

    private ProductLongDto product;
    private int quantity;
}
