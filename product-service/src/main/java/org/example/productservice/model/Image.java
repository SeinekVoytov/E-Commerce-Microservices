package org.example.productservice.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "image")
@Data
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne()
    @JoinColumn(name = "owner_id",  referencedColumnName = "id")
    @ToString.Exclude
    private ProductShort owner;

    private String url;
}
