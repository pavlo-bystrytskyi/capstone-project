package org.example.backend.model;

import lombok.Builder;
import lombok.Data;
import lombok.With;
import org.springframework.data.annotation.Id;

@Builder
@Data
@With
public class User {
    @Id
    private String id;

    private String externalId;

    private String firstName;

    private String lastName;

    private String email;

    private String picture;
}
