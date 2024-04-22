package org.example.orderservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.orderservice.model.product.ProductShort;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "order_item_short")
public class OrderItemShort {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private ProductShort item;

    @OneToOne
    @JoinColumn(name = "quantity_id", referencedColumnName = "id")
    private OrderItemQuantity quantity;
}
