package org.example.productservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "product_short")
public class ProductShort {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(mappedBy = "productShort", cascade = CascadeType.PERSIST)
    @ToString.Exclude
    private ProductLong productLong;

    private String name;

    @OneToMany(mappedBy = "owner", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Image> images;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "price_id", referencedColumnName = "id")
    private Price price;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "product_short_category",
            joinColumns = @JoinColumn(name = "product_short_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;
}
