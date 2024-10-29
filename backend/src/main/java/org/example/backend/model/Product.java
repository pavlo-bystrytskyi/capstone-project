package org.example.backend.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Builder
@Data
public class Product {

    @Id
    private String id;

    private String title;
    private String description;
    private String link;
}
