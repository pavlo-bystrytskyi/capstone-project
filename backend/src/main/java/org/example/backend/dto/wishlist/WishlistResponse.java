package org.example.backend.dto.wishlist;

import lombok.Builder;
import lombok.NonNull;
import org.example.backend.model.Wishlist;

import java.util.List;

@Builder
public record WishlistResponse(@NonNull List<String> itemIds, @NonNull String title, @NonNull String description) {

    public static WishlistResponse of(Wishlist wishlist) {
        return WishlistResponse.builder()
                .title(wishlist.getTitle())
                .description(wishlist.getDescription())
                .itemIds(wishlist.getItemIds())
                .build();
    }
}
