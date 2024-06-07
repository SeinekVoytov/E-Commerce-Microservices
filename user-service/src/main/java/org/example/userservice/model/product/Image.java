package org.example.userservice.model.product;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "image")
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
