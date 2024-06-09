package org.example.userservice.model.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "category")
public class Category {

    @Id
    @SequenceGenerator(
            name = "category_seq",
            sequenceName = "category_seq",
            allocationSize = 20
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_seq")
    private Integer id;

    @ManyToMany(mappedBy = "categories", cascade = CascadeType.PERSIST)
    @JsonIgnore
    @ToString.Exclude
    private Set<Product> products;

    private String name;
    private Integer count;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id) && Objects.equals(products, category.products) && Objects.equals(name, category.name) && Objects.equals(count, category.count);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, products, name, count);
    }
}