package org.example.orderservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.util.Objects;

@Getter
@Setter
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
    private Integer id;

    @OneToOne
    @JoinColumn(name = "pick_up_point_id", referencedColumnName = "id")
    private PickUpPoint pickUpPoint;

    @Enumerated
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private DeliveryStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Delivery delivery = (Delivery) o;
        return Objects.equals(id, delivery.id) && Objects.equals(pickUpPoint, delivery.pickUpPoint) && status == delivery.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pickUpPoint, status);
    }
}