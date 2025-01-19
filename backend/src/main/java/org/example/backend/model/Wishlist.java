package org.example.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Builder
@Data
@With
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private String privateId;

    private String publicId;

    private Long ownerId;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Item> items;

    private String title;

    private String description;
}
