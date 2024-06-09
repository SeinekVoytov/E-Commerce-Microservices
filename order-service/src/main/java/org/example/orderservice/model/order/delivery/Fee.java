package org.example.orderservice.model.order.delivery;

import jakarta.persistence.*;
import lombok.*;
import org.example.orderservice.jpaconverter.CurrencyConverter;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

@Getter
@Setter
@Builder
@ToString
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
    private Integer id;

    private BigDecimal amount;

    @Convert(converter = CurrencyConverter.class)
    private Currency currency;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fee fee = (Fee) o;
        return Objects.equals(id, fee.id) && Objects.equals(amount, fee.amount) && Objects.equals(currency, fee.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount, currency);
    }
}