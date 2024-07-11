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
@Table(name = "brand")
public class Brand {

    @Id
    @SequenceGenerator(
            name = "brand_seq",
            sequenceName = "brand_seq",
            allocationSize = 20
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "brand_seq")
    private Integer id;

    private String name;

    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "brand_id", referencedColumnName = "id")
    private Set<Product> products;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Brand brand = (Brand) o;
        return Objects.equals(id, brand.id) && Objects.equals(name, brand.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
