package org.example.productservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "price")
@Data
public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(mappedBy = "price", cascade = CascadeType.PERSIST)
    @JsonIgnore
    @ToString.Exclude
    private ProductShort product;

    private float amount;
    private String currency;
}
