package org.example.backend.dto.item;

import lombok.Builder;
import lombok.NonNull;
import org.example.backend.model.Product;

@Builder
public record ProductResponse(@NonNull String title, @NonNull String description, @NonNull String link) {

    public static ProductResponse of(Product product) {
        return ProductResponse.builder()
                .title(product.getTitle())
                .description(product.getDescription())
                .link(product.getLink())
                .build();
    }
}
