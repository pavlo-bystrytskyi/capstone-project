package org.example.backend.dto.wishlist;

import lombok.Builder;
import lombok.NonNull;
import org.example.backend.model.Wishlist;

import java.util.List;

@Builder
public record PublicWishlistResponse(
        @NonNull List<PublicItemIdsResponse> itemIds,
        @NonNull String title,
        @NonNull String description
) {

    public static PublicWishlistResponse of(Wishlist wishlist) {
        return PublicWishlistResponse.builder()
                .title(wishlist.getTitle())
                .description(wishlist.getDescription())
                .itemIds(wishlist.getItemIds().stream().map(PublicItemIdsResponse::of).toList())
                .build();
    }
}
