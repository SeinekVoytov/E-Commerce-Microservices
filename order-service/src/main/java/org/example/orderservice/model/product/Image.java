package org.example.orderservice.model.product;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "image")
public class Image {

    @Id
    @SequenceGenerator(
            name = "image_seq",
            sequenceName = "image_seq",
            allocationSize = 20
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "image_seq")
    private Integer id;

    private String url;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Image image = (Image) o;
        return Objects.equals(id, image.id) && Objects.equals(url, image.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, url);
    }
}