package org.example.orderservice.model.order.delivery;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "fee")
public class Fee {

    @Id
    @SequenceGenerator(
            name = "fee_seq",
            sequenceName = "fee_seq",
            allocationSize = 20
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fee_seq")
    private int id;

    private Float amount;
    private String currency;
}
