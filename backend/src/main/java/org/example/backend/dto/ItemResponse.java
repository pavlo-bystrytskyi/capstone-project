package org.example.backend.dto;

import lombok.Builder;
import lombok.NonNull;
import org.example.backend.model.Item;

@Builder
public record ItemResponse(@NonNull Double quantity, @NonNull ProductResponse product) {

    public static ItemResponse of(Item item) {
        return ItemResponse.builder()
                .product(ProductResponse.of(item.getProduct()))
                .quantity(item.getQuantity())
                .build();
    }
}
