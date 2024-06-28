package org.example.userservice.model.product;

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
@Table(name = "product_details")
public class ProductDetails {

    @Id
    @SequenceGenerator(
            name = "product_details_seq",
            sequenceName = "product_details_seq",
            allocationSize = 20
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_details_seq")
    private Integer id;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @Column(name = "length_meters")
    private Double lengthInMeters;

    @Column(name = "width_meters")
    private Double widthInMeters;

    @Column(name = "height_meters")
    private Double heightInMeters;

    @Column(name = "gross_weight_kg")
    private Double grossWeightInKg;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductDetails that = (ProductDetails) o;
        return Objects.equals(id, that.id) && Objects.equals(product, that.product) && Objects.equals(lengthInMeters, that.lengthInMeters) && Objects.equals(widthInMeters, that.widthInMeters) && Objects.equals(heightInMeters, that.heightInMeters) && Objects.equals(grossWeightInKg, that.grossWeightInKg);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, product, lengthInMeters, widthInMeters, heightInMeters, grossWeightInKg);
    }
}