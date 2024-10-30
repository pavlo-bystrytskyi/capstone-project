package org.example.backend.repository;

import org.example.backend.model.Item;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ItemRepository extends MongoRepository<Item, String> {
    Optional<Item> findByPublicId(String publicId);
}
