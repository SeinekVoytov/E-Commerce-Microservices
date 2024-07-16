package org.example.orderservice.model.delivery;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.orderservice.jpaconverter.CurrencyConverter;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

@Getter
@Setter
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