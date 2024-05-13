package org.example.orderservice.model.order;

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
@Table(name = "address")
public class Address {

    @Id
    @SequenceGenerator(
            name = "address_seq",
            sequenceName = "address_seq",
            allocationSize = 20
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "address_seq")
    private int id;

    private String city;
    private String country;

    @Column(name = "street_address")
    private String streetAddress;

    private String apartment;
}
