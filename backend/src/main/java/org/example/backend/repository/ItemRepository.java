package org.example.backend.repository;

import lombok.NonNull;
import org.example.backend.model.Item;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.lang.Nullable;

import java.util.Optional;

public interface ItemRepository extends MongoRepository<Item, String> {

    Optional<Item> findByPublicId(@NonNull String publicId);

    Optional<Item> findByIdAndOwnerId(@NonNull String id, @Nullable String ownerId);

    void deleteByIdAndOwnerId(@NonNull String id, @Nullable String userId);
}
