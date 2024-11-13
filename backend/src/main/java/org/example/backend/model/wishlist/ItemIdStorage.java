package org.example.backend.model.wishlist;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ItemIdStorage {
    private String publicId;
    private String privateId;
}
