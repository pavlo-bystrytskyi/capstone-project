package org.example.backend.mock.dto;

import lombok.With;
import org.example.backend.model.item.ItemStatus;

@With
public record ItemRequestMock(Double quantity, ItemStatus status, ProductRequestMock product) {

}
