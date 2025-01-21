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

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items;

//    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = true)
//    private User owner;
//
//
//    @OneToMany(mappedBy = "wishlist", fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH, CascadeType.REMOVE}, orphanRemoval = true)
//    private List<Item> items;

    private String title;

    private String description;
}
