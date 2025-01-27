package org.example.backend.dto.response.wishlist;

import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.NonNull;
import org.example.backend.model.Wishlist;

import java.time.ZonedDateTime;
import java.util.List;

@Builder
public record PrivateWishlistResponse(
        @NonNull String privateId,
        @NonNull String publicId,
        @NonNull List<PrivateItemIdsResponse> itemIds,
        @NonNull String title,
        @NonNull String description,
        @NonNull Boolean active,
        @Nullable ZonedDateTime deactivationDate
) {

    public static PrivateWishlistResponse of(Wishlist wishlist) {
        return PrivateWishlistResponse.builder()
                .privateId(wishlist.getPrivateId())
                .publicId(wishlist.getPublicId())
                .title(wishlist.getTitle())
                .description(wishlist.getDescription())
                .active(wishlist.getActive())
                .deactivationDate(wishlist.getDeactivationDate())
                .itemIds(wishlist.getItems().stream().map(PrivateItemIdsResponse::of).toList())
                .build();
    }
}
