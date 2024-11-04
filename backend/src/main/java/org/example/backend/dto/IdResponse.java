package org.example.backend.dto;

import lombok.NonNull;

public record IdResponse(@NonNull String publicId, @NonNull String privateId) {
    public static IdResponse of(@NonNull String publicId, @NonNull String privateId) {
        return new IdResponse(publicId, privateId);
    }
}
