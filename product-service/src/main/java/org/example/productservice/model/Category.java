package org.example.productservice.model;

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

    @ManyToOne
    @JoinColumn(name = "parent_category_id")
    @ToString.Exclude
    private Category parentCategory;

    @OneToMany(
            mappedBy = "parentCategory",
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
            orphanRemoval = true
    )
    private Set<Category> childCategories;

    @ManyToMany(mappedBy = "categories", cascade = CascadeType.PERSIST)
    @ToString.Exclude
    private Set<Product> products;

    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id) && Objects.equals(parentCategory, category.parentCategory) && Objects.equals(products, category.products) && Objects.equals(name, category.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, childCategories, products, name);
    }
}