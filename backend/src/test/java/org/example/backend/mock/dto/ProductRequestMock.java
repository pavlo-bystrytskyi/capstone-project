package org.example.backend.mock.dto;

import lombok.With;

@With
public record ProductRequestMock(String title, String description, String link) {
}
