package org.example.orderservice.model.product;

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
@Table(name = "price")
public class Price {

    @Id
    @SequenceGenerator(
            name = "price_seq",
            sequenceName = "price_seq",
            allocationSize = 20
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "price_seq")
    private Integer id;

    private BigDecimal amount;

    @Convert(converter = CurrencyConverter.class)
    private Currency currency;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Price price = (Price) o;
        return Objects.equals(id, price.id) && Objects.equals(amount, price.amount) && Objects.equals(currency, price.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount, currency);
    }
}