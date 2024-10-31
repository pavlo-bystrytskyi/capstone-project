package org.example.backend.dto.item;

import lombok.Builder;
import lombok.NonNull;
import org.example.backend.model.Item;
import org.example.backend.model.item.ItemStatus;

@Builder
public record ItemResponse(@NonNull Double quantity, @NonNull ProductResponse product, @NonNull ItemStatus status) {

    public static ItemResponse of(Item item) {
        return ItemResponse.builder()
                .product(ProductResponse.of(item.getProduct()))
                .quantity(item.getQuantity())
                .status(item.getStatus())
                .build();
    }
}
