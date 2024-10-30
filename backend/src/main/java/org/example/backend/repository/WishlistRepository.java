package org.example.backend.repository;

import org.example.backend.model.Wishlist;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface WishlistRepository extends MongoRepository<Wishlist, String> {

    Optional<Wishlist> findByPublicId(String publicId);
}
