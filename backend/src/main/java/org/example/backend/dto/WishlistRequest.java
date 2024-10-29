package org.example.backend.dto;

import lombok.NonNull;
import org.example.backend.model.Wishlist;

import java.util.List;

public record WishlistRequest(@NonNull List<String> itemIds, @NonNull String title, @NonNull String description) {

    public Wishlist toWishlist() {
        return Wishlist.builder()
                .itemIds(itemIds)
                .title(title)
                .description(description)
                .build();
    }
}
