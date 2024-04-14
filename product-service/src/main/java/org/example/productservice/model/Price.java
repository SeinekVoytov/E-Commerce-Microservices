package org.example.productservice.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "price")
@Data
public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(mappedBy = "price", cascade = CascadeType.PERSIST)
    private ProductShort product;

    private float amount;
    private String currency;
}
