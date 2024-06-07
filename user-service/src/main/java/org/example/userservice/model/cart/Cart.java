package org.example.userservice.model.cart;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import javax.print.attribute.standard.MediaSize;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "cart")
@DynamicInsert
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID userId;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "cart_id", referencedColumnName = "id")
    private List<CartItem> items;

    @Column(
            name = "updated_at",
            columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP"
    )
    private Instant updatedAt;

    @Column(
            name = "created_at",
            columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP"
    )
    private Date createdAt;
}
