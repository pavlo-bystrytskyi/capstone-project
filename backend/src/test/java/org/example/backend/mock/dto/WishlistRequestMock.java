package org.example.backend.mock.dto;

import lombok.With;

import java.time.ZonedDateTime;
import java.util.List;

@With
public record WishlistRequestMock(String title, String description, List<ItemIdsRequestMock> itemIds, Boolean active, ZonedDateTime deactivationDate) {

}
