package org.example.backend.dto.wishlist;

import lombok.Builder;
import lombok.NonNull;
import org.example.backend.model.Wishlist;

import java.util.List;

@Builder
public record PrivateWishlistResponse(
        @NonNull List<String> privateItemIds,
        @NonNull String title,
        @NonNull String description) {

    public static PrivateWishlistResponse of(Wishlist wishlist) {
        return PrivateWishlistResponse.builder()
                .title(wishlist.getTitle())
                .description(wishlist.getDescription())
                .privateItemIds(wishlist.getPrivateItemIds())
                .build();
    }
}
