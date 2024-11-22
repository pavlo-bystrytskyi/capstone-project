package org.example.backend.repository;

import org.example.backend.model.Cache;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CacheRepository extends MongoRepository<Cache, String> {

}
