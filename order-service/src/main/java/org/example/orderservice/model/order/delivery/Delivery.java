package org.example.orderservice.model.order.delivery;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "delivery")
public class Delivery {

    @Id
    @SequenceGenerator(
            name = "delivery_seq",
            sequenceName = "delivery_seq",
            allocationSize = 20
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "delivery_seq")
    private int id;

    @Enumerated
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private DeliveryType type;

    @Enumerated
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private DeliveryStatus status;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "fee_id", referencedColumnName = "id")
    private Fee fee;
}