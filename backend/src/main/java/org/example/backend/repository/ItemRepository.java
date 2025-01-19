package org.example.backend.repository;

import lombok.NonNull;
import org.example.backend.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.Nullable;

import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findByPublicId(@NonNull String publicId);

    Optional<Item> findByPrivateId(@NonNull String publicId);

    Optional<Item> findByPrivateIdAndOwnerId(@NonNull String id, @Nullable Long ownerId);

    void deleteByPrivateIdAndOwnerId(@NonNull String id, @Nullable Long ownerId);
}
