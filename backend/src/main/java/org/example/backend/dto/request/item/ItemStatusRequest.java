package org.example.backend.dto.request.item;

import jakarta.validation.constraints.NotNull;
import org.example.backend.model.item.ItemStatus;

public record ItemStatusRequest(@NotNull ItemStatus status) {

}
