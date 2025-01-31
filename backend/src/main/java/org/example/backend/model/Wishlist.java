package org.example.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
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

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items;

    private String title;

    private String description;

    private Boolean active = true;

    private ZonedDateTime deactivationDate;
}
