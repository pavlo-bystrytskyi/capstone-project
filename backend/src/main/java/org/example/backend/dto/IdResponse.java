package org.example.backend.dto;

import lombok.NonNull;

public record IdResponse(@NonNull String id) {
    public static IdResponse of(@NonNull String id) {
        return new IdResponse(id);
    }
}
