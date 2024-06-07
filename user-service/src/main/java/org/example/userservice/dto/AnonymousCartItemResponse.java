package org.example.userservice.dto;

import java.util.UUID;

public record AnonymousCartItemResponse (
        UUID cartId,
        ProductLongDto product,
        int quantity
) {
}
