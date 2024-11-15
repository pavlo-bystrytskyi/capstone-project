package org.example.backend.dto;

import lombok.Builder;
import lombok.NonNull;
import org.example.backend.model.User;

@Builder
public record UserResponse(
        @NonNull String firstName,
        @NonNull String lastName,
        @NonNull String email,
        @NonNull String picture
) {

    public static UserResponse of(@NonNull User user) {
        return UserResponse
                .builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .picture(user.getPicture())
                .build();
    }

    public static UserResponse guest() {
        return UserResponse
                .builder()
                .firstName("")
                .lastName("")
                .email("")
                .picture("")
                .build();
    }
}
