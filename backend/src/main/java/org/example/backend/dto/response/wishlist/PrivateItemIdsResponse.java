package org.example.backend.dto.response.wishlist;

import lombok.NonNull;
import org.example.backend.model.wishlist.ItemIdStorage;

public record PrivateItemIdsResponse(@NonNull String privateId, @NonNull String publicId) {
    public static PrivateItemIdsResponse of(@NonNull ItemIdStorage itemIdStorage) {
        return new PrivateItemIdsResponse(itemIdStorage.getPrivateId(), itemIdStorage.getPublicId());
    }
}
