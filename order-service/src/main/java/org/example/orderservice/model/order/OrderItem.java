package org.example.orderservice.model.order;

import jakarta.persistence.*;
import lombok.*;
import org.example.orderservice.model.product.ProductDetails;

import java.util.Objects;

@Getter
@Setter
@Builder
@ToString
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
    private Integer id;

    @OneToOne
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private ProductDetails item;

    private Integer quantity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(id, orderItem.id) && Objects.equals(item, orderItem.item) && Objects.equals(quantity, orderItem.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, item, quantity);
    }
}