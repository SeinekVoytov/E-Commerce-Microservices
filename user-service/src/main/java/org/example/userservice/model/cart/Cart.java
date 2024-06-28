package org.example.userservice.model.cart;

import jakarta.persistence.*;
import lombok.*;
import org.example.userservice.exception.CartItemNotFoundException;
import org.hibernate.annotations.DynamicInsert;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@ToString
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

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn(name = "cart_id", referencedColumnName = "id")
    private Set<CartItem> items;

    @Column(
            name = "updated_at",
            columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP"
    )
    private Instant updatedAt;

    @Column(
            name = "created_at",
            columnDefinition = "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP"
    )
    private Instant createdAt;

    public void addItem(CartItem item) {
        items.add(item);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void clear() {
        items.clear();
    }

    public CartItem getItemById(int itemId) {
        return items.stream()
                .filter(item -> item.getId() == itemId)
                .findAny()
                .orElseThrow(CartItemNotFoundException::new);
    }

    public void removeItemById(int itemId) {
        items.removeIf(item -> item.getId() == itemId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cart cart = (Cart) o;
        return Objects.equals(id, cart.id) && Objects.equals(userId, cart.userId) && Objects.equals(items, cart.items) && Objects.equals(updatedAt, cart.updatedAt) && Objects.equals(createdAt, cart.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, items, updatedAt, createdAt);
    }
}