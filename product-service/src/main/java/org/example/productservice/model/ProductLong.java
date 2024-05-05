package org.example.productservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "product_long")
public class ProductLong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "product_short_id", referencedColumnName = "id")
    private ProductShort productShort;

    @Column(name = "length_m")
    private Float lengthInM;

    @Column(name = "width_m")
    private Float widthInM;

    @Column(name = "height_m")
    private Float heightInM;

    @Column(name = "net_weight_kg")
    private Float netWeightInKg;

    @Column(name = "gross_weight_kg")
    private Float grossWeightInKg;
}
