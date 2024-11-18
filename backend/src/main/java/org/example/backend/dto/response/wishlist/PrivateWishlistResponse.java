package org.example.backend.dto.response.wishlist;

import lombok.Builder;
import lombok.NonNull;
import org.example.backend.model.Wishlist;

import java.util.List;

@Builder
public record PrivateWishlistResponse(
        @NonNull String privateId,
        @NonNull String publicId,
        @NonNull List<PrivateItemIdsResponse> itemIds,
        @NonNull String title,
        @NonNull String description
) {

    public static PrivateWishlistResponse of(Wishlist wishlist) {
        return PrivateWishlistResponse.builder()
                .privateId(wishlist.getId())
                .publicId(wishlist.getPublicId())
                .title(wishlist.getTitle())
                .description(wishlist.getDescription())
                .itemIds(wishlist.getItemIds().stream().map(PrivateItemIdsResponse::of).toList())
                .build();
    }
}
