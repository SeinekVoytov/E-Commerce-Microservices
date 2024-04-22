package org.example.orderservice.model.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "price")
public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(mappedBy = "price", cascade = CascadeType.PERSIST)
    @JsonIgnore
    @ToString.Exclude
    private ProductShort product;

    private Float amount;
    private String currency;
}
