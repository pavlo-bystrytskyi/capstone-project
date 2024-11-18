package org.example.backend.dto.request.wishlist;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.backend.model.wishlist.ItemIdStorage;

public record ItemIdsRequest(
        @NotBlank String privateId,
        @NotBlank String publicId
) {

    public ItemIdStorage toItemIdStorage() {
        return ItemIdStorage.builder()
                .privateId(privateId)
                .publicId(publicId)
                .build();
    }
}
