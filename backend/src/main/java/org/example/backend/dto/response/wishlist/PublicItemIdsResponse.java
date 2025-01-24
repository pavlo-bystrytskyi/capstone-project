package org.example.backend.dto.response.wishlist;

import lombok.NonNull;
import org.example.backend.model.Item;

public record PublicItemIdsResponse(@NonNull String privateId, @NonNull String publicId) {
    public static PublicItemIdsResponse of(@NonNull Item item) {
        return new PublicItemIdsResponse("", item.getPublicId());
    }
}
