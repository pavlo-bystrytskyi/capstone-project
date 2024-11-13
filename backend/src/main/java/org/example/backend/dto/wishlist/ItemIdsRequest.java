package org.example.backend.dto.wishlist;

import lombok.NonNull;
import org.example.backend.model.wishlist.ItemIdStorage;

public record ItemIdsRequest(
        @NonNull String privateId,
        @NonNull String publicId
) {

    public ItemIdStorage toItemIdStorage() {
        return ItemIdStorage.builder()
                .privateId(privateId)
                .publicId(publicId)
                .build();
    }
}
