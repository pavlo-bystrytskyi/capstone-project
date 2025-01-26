package org.example.backend.dto.request.wishlist;

import org.springframework.lang.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.example.backend.model.Wishlist;

import java.time.ZonedDateTime;
import java.util.List;

public record WishlistRequest(
        @NotEmpty @Valid List<ItemIdsRequest> itemIds,
        @NotNull @Size(min = 4, max = 255) String title,
        @NotNull @Size(max = 4095) String description,
        @NotNull Boolean active,
        @Nullable ZonedDateTime deactivationDate
) {

    public Wishlist toWishlist() {
        return Wishlist.builder()
                .items(itemIds.stream().map(ItemIdsRequest::toItem).toList())
                .title(title)
                .description(description)
                .active(active)
                .deactivationDate(deactivationDate)
                .build();
    }
}
