package org.example.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;

public record DataExtractionRequest(
        @NotNull @URL String url
) {

}
