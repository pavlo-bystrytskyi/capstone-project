package org.example.backend.dto.item;

import lombok.NonNull;
import org.example.backend.model.Item;
import org.example.backend.model.item.ItemStatus;

public record ItemRequest(@NonNull Double quantity, @NonNull ItemStatus status,
                          @NonNull ProductRequest product) {

    public Item toItem() {
        return Item.builder()
                .product(product.toProduct())
                .quantity(quantity)
                .status(status)
                .build();
    }
}
