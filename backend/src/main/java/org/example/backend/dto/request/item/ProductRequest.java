package org.example.backend.dto.request.item;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.example.backend.model.Product;
import org.hibernate.validator.constraints.URL;

public record ProductRequest(
        @NotNull @Size(min = 4, max = 255) String title,
        @NotNull @Size(max = 4095) String description,
        @NotNull @URL String link
) {
    public Product toProduct() {
        return Product.builder()
                .title(title)
                .description(description)
                .link(link)
                .build();
    }
}
