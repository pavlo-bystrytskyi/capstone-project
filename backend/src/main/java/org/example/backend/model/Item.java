package org.example.backend.model;

import lombok.Builder;
import lombok.Data;
import lombok.With;
import org.example.backend.model.item.ItemStatus;
import org.springframework.data.annotation.Id;

@Builder
@Data
@With
public class Item {

    @Id
    private String id;

    private String publicId;

    private String ownerId;

    private Product product;

    private double quantity;

    private ItemStatus status;
}
