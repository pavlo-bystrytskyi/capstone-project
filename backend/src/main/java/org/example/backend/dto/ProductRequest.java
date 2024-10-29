package org.example.backend.dto;

import lombok.NonNull;
import org.example.backend.model.Product;

public record ProductRequest(@NonNull String title, @NonNull String description, @NonNull String link) {
    public Product toProduct() {
        return Product.builder()
                .title(title)
                .description(description)
                .link(link)
                .build();
    }
}
