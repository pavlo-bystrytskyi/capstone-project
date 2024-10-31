package org.example.backend.mock.dto;

import lombok.NonNull;
import org.example.backend.model.item.ItemStatus;

public record ItemStatusRequestMock(@NonNull ItemStatus status) {

}
