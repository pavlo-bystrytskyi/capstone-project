package org.example.backend.dto.response;

import lombok.Builder;
import lombok.NonNull;
import org.example.backend.dto.chatgpt.ParsedProductData;

@Builder
public record DataExtractionResponse(
        @NonNull String title,
        @NonNull String description
) {

    public static DataExtractionResponse of(ParsedProductData parsedResponse) {
        return DataExtractionResponse
                .builder()
                .title(parsedResponse.title())
                .description(parsedResponse.description())
                .build();
    }
}
