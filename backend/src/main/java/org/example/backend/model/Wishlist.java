package org.example.backend.model;

import lombok.Builder;
import lombok.Data;
import lombok.With;
import org.example.backend.model.wishlist.ItemIdStorage;
import org.springframework.data.annotation.Id;

import java.util.List;

@Builder
@Data
@With
public class Wishlist {

    @Id
    private String id;

    private String publicId;

    private String ownerId;

    private List<ItemIdStorage> itemIds;

    private String title;

    private String description;
}
