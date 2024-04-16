package org.example.productservice.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "image")
@Data
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne()
    @JoinColumn(name = "owner_id",  referencedColumnName = "id")
    private ProductShort owner;

    private String url;
}
