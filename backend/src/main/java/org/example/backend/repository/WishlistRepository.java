package org.example.backend.repository;

import lombok.NonNull;
import org.example.backend.model.Wishlist;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.lang.Nullable;

import java.util.Optional;

public interface WishlistRepository extends MongoRepository<Wishlist, String> {

    Optional<Wishlist> findByPublicId(@NonNull String publicId);

    Optional<Wishlist> findByIdAndOwnerId(@NonNull String id, @Nullable String ownerId);

    void deleteByIdAndOwnerId(@NonNull String id, @Nullable String ownerId);
}
