package org.example.userservice.model.product;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "product")
public class Product {

    @Id
    @SequenceGenerator(
            name = "product_seq",
            sequenceName = "product_seq",
            allocationSize = 20
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq")
    private Integer id;

    private String name;

    @Column(name = "net_weight_kg")
    private Double netWeightInKg;

    private String description;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private Set<Image> images;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "price_id", referencedColumnName = "id")
    private Price price;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Brand brand;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private CountryManufacturer countryManufacturer;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "product_category",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;

    @PreRemove
    private void removeMappingWithCategories() {
        categories.forEach(
                category -> category.getProducts().remove(this)
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id) && Objects.equals(name, product.name) && Objects.equals(netWeightInKg, product.netWeightInKg) && Objects.equals(description, product.description) && Objects.equals(images, product.images) && Objects.equals(price, product.price) && Objects.equals(brand, product.brand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, netWeightInKg, description, images, price, brand);
    }
}
