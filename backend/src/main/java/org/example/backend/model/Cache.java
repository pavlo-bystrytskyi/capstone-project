package org.example.backend.model;

import lombok.Builder;
import lombok.Data;
import org.example.backend.dto.chatgpt.ParsedProductData;
import org.springframework.data.annotation.Id;

@Builder
@Data
public class Cache {

    @Id
    private String key;

    private ParsedProductData value;
}
