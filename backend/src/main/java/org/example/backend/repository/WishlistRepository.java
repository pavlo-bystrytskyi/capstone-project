package org.example.backend.repository;

import org.example.backend.model.Wishlist;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WishlistRepository extends MongoRepository<Wishlist, String> {

}
