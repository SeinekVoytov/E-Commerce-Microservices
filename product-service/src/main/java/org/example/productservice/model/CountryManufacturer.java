package org.example.productservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "country_manufacturer")
public class CountryManufacturer {

    @Id
    @SequenceGenerator(
            name = "country_seq",
            sequenceName = "country_seq",
            allocationSize = 20
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "country_seq")
    private Integer id;

    @Enumerated(EnumType.STRING)
    private Country name;

    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "country_manufacturer_id", referencedColumnName = "id")
    private Set<ProductDetails> products;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CountryManufacturer country = (CountryManufacturer) o;
        return Objects.equals(id, country.id) && Objects.equals(name, country.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
