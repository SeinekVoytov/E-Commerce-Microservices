package org.example.orderservice.model.order;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.orderservice.model.product.ProductLong;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "order_item_long")
public class OrderItemLong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private ProductLong item;

    @OneToOne
    @JoinColumn(name = "quantity_id", referencedColumnName = "id")
    private OrderItemQuantity quantity;
}
