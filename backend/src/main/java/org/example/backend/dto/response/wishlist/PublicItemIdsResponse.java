package org.example.backend.dto.response.wishlist;

import lombok.NonNull;
import org.example.backend.model.wishlist.ItemIdStorage;

public record PublicItemIdsResponse(@NonNull String privateId, @NonNull String publicId) {
    public static PublicItemIdsResponse of(@NonNull ItemIdStorage itemIdStorage) {
        return new PublicItemIdsResponse("", itemIdStorage.getPublicId());
    }
}
