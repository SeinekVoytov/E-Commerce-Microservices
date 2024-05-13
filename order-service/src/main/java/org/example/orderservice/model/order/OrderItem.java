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
@Table(name = "order_item")
public class OrderItem {

    @Id
    @SequenceGenerator(
            name = "order_item_seq",
            sequenceName = "order_item_seq"
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_item_seq")
    private int id;

    @OneToOne
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private ProductLong item;

    private int quantity;
}
