package org.example.backend.dto.response.item;

import lombok.Builder;
import lombok.NonNull;
import org.example.backend.model.Item;
import org.example.backend.model.item.ItemStatus;

@Builder
public record PrivateItemResponse(
        @NonNull String privateId,
        @NonNull String publicId,
        @NonNull Double quantity,
        @NonNull ProductResponse product,
        @NonNull ItemStatus status
) {

    public static PrivateItemResponse of(Item item) {
        return PrivateItemResponse.builder()
                .privateId(item.getId())
                .publicId(item.getPublicId())
                .product(ProductResponse.of(item.getProduct()))
                .quantity(item.getQuantity())
                .status(item.getStatus())
                .build();
    }
}
