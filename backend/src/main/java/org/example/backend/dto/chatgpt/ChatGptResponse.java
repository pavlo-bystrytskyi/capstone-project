package org.example.backend.dto.chatgpt;

import java.util.List;

public record ChatGptResponse(List<ChatGptResponseChoice> choices) {
}
