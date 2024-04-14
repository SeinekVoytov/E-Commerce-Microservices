package org.example.productservice.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "product_short")
@Data
public class ProductShort {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(mappedBy = "productShort", cascade = CascadeType.PERSIST)
    private ProductLong productLong;

    private String name;

    @Column(name = "img_uri")
    private String imgUri;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "price_id", referencedColumnName = "id")
    private Price price;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "product_short_category",
            joinColumns = @JoinColumn(name = "product_short_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;
}
