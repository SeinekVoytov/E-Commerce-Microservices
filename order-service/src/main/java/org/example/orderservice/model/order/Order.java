package org.example.orderservice.model.order;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.orderservice.model.order.delivery.Delivery;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "\"order\"")
public class Order {

    @Id
    @SequenceGenerator(
            name = "order_seq",
            sequenceName = "order_seq",
            allocationSize = 20
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq")
    private int id;

    @Column(name = "user_id")
    private UUID userId;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "delivery_id", referencedColumnName = "id")
    private Delivery delivery;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private List<OrderItem> items;
}
