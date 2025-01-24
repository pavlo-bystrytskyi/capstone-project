package org.example.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Data
@With
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private String externalId;

    private String firstName;

    private String lastName;

    private String email;

    private String picture;
}
