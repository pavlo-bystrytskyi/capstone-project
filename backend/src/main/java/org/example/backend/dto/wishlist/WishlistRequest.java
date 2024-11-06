package org.example.backend.dto.wishlist;

import lombok.NonNull;
import org.example.backend.model.Wishlist;

import java.util.List;

public record WishlistRequest(@NonNull List<String> privateItemIds, @NonNull List<String> publicItemIds,
                              @NonNull String title, @NonNull String description) {

    public Wishlist toWishlist() {
        return Wishlist.builder()
                .privateItemIds(privateItemIds)
                .publicItemIds(publicItemIds)
                .title(title)
                .description(description)
                .build();
    }
}
