package org.example.backend.dto.request.wishlist;

import jakarta.validation.constraints.NotBlank;
import org.example.backend.model.Item;

public record ItemIdsRequest(
        @NotBlank String privateId,
        @NotBlank String publicId
) {

    public Item toItem() {
        return Item.builder()
                .privateId(privateId)
                .publicId(publicId)
                .build();
    }
}
