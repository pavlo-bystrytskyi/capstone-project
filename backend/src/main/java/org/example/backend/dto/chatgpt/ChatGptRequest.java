package org.example.backend.dto.chatgpt;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ChatGptRequest(
        String model,
        List<ChatGptRequestMessage> messages,
        @JsonProperty("response_format") ChatGptResponseFormat responseFormat
) {
}
