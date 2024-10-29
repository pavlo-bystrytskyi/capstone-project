package org.example.backend.mock.dto;

import lombok.With;

@With
public record ItemRequestMock(Double quantity, ProductRequestMock product) {
}
