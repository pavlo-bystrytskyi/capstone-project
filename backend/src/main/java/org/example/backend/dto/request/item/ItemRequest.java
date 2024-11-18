package org.example.backend.dto.request.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.example.backend.model.Item;
import org.example.backend.model.item.ItemStatus;

public record ItemRequest(
        @NotNull @Positive Double quantity,
        @NotNull ItemStatus status,
        @NotNull @Valid ProductRequest product
) {

    public Item toItem() {
        return Item.builder()
                .product(product.toProduct())
                .quantity(quantity)
                .status(status)
                .build();
    }
}
