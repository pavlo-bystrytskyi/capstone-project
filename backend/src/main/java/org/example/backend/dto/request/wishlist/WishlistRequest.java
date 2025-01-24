package org.example.backend.dto.request.wishlist;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.NonNull;
import org.example.backend.model.Wishlist;

import java.util.List;

public record WishlistRequest(
        @NotEmpty @Valid List<ItemIdsRequest> itemIds,
        @NotNull @Size(min = 4, max = 255) String title,
        @NotNull @Size(min = 0, max = 4095) String description
) {

    public Wishlist toWishlist() {
        return Wishlist.builder()
                .items(itemIds.stream().map(ItemIdsRequest::toItem).toList())
                .title(title)
                .description(description)
                .build();
    }
}
