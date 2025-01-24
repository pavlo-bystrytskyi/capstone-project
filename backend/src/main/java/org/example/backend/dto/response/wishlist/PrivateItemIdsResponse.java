package org.example.backend.dto.response.wishlist;

import lombok.NonNull;
import org.example.backend.model.Item;

public record PrivateItemIdsResponse(@NonNull String privateId, @NonNull String publicId) {
    public static PrivateItemIdsResponse of(@NonNull Item item) {
        return new PrivateItemIdsResponse(item.getPrivateId(), item.getPublicId());
    }
}
