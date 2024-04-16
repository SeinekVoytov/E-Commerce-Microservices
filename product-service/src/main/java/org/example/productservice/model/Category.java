package org.example.productservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "category")
@Data
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToMany(mappedBy = "categories", cascade = CascadeType.PERSIST)
    @JsonIgnore
    @ToString.Exclude
    private List<ProductShort> products;

    private String name;
    private int count;
}
