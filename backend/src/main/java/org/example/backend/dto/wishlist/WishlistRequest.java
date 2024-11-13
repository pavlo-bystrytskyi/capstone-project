package org.example.backend.dto.wishlist;

import lombok.NonNull;
import org.example.backend.model.Wishlist;

import java.util.List;

public record WishlistRequest(
        @NonNull List<ItemIdsRequest> itemIds,
        @NonNull String title,
        @NonNull String description
) {

    public Wishlist toWishlist() {
        return Wishlist.builder()
                .itemIds(itemIds.stream().map(ItemIdsRequest::toItemIdStorage).toList())
                .title(title)
                .description(description)
                .build();
    }
}
