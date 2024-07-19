package org.example.userservice.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.example.userservice.exception.CartItemNotFoundException;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@RedisHash(value = "cart", timeToLive = 3600L)
public class Cart {

    @Id
    private UUID id;

    @Indexed
    private UUID userId;

    private List<CartItem> items = new ArrayList<>();

    public Cart(UUID userId) {
        this.userId = userId;
    }

    public Cart(CartItem initialItem) {
        addItemIdempotently(initialItem);
    }

    public Cart(UUID userId, CartItem initialItem) {
        this(initialItem);
        this.userId = userId;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void clear() {
        items.clear();
    }

    public void addItemIdempotently(CartItem item) {
        Integer toBeAddedProductId = item.getProduct().getId();
        items.stream()
                .filter(cartItem -> cartItem.getProduct().getId().equals(toBeAddedProductId))
                .findAny()
                .ifPresentOrElse(
                        alreadySavedItem -> alreadySavedItem.setQuantity(
                                alreadySavedItem.getQuantity() + item.getQuantity()
                        ),
                        () -> addItemAndSetId(item));
    }

    public CartItem getItemById(UUID itemId) {
        return items.stream()
                .filter(item -> item.getId().equals(itemId))
                .findAny()
                .orElseThrow(CartItemNotFoundException::new);
    }

    public void removeItemById(UUID itemId) {
        if (!items.removeIf(item -> item.getId().equals(itemId))) {
            throw new CartItemNotFoundException();
        }
    }

    private void addItemAndSetId(CartItem item) {
        item.setId(UUID.randomUUID());
        items.add(item);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cart cart = (Cart) o;
        return Objects.equals(id, cart.id) && Objects.equals(userId, cart.userId) && Objects.equals(items, cart.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, items);
    }
}