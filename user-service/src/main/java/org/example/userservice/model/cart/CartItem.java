package org.example.userservice.model.cart;

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
@Table(name = "cart_item")
public class CartItem {

    @Id
    @SequenceGenerator(
            name = "cart_item_seq",
            sequenceName = "cart_item_seq",
            allocationSize = 75
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cart_item_seq")
    private int id;

    private int productId;
    private int quantity;
}
