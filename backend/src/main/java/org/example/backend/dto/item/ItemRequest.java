package org.example.backend.dto.item;

import lombok.NonNull;
import org.example.backend.model.Item;

public record ItemRequest(@NonNull Double quantity, @NonNull ProductRequest product) {
    public Item toItem() {
        return Item.builder()
                .product(product.toProduct())
                .quantity(quantity)
                .build();
    }
}
