package org.example.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.backend.model.item.ItemStatus;

@Builder
@Data
@With
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    private String privateId;

    private String publicId;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne(cascade = CascadeType.ALL)
    private Product product;

    private double quantity;

    private ItemStatus status;
}
