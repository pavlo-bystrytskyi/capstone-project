package org.example.backend.dto.item;

import lombok.NonNull;
import org.example.backend.model.item.ItemStatus;

public record ItemStatusRequest(@NonNull ItemStatus status) {

}
