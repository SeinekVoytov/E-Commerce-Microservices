package org.example.orderservice.model.order.delivery;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.util.Objects;

@Getter
@Setter
@Builder
@ToString
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
    private Integer id;

    @Enumerated
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private DeliveryType type;

    @Enumerated
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private DeliveryStatus status;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "fee_id", referencedColumnName = "id")
    private Fee fee;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Delivery delivery = (Delivery) o;
        return Objects.equals(id, delivery.id) && type == delivery.type && status == delivery.status && Objects.equals(fee, delivery.fee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, status, fee);
    }
}