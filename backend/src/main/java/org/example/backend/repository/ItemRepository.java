package org.example.backend.repository;

import lombok.NonNull;
import org.example.backend.model.Item;
import org.example.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findByPublicId(@NonNull String publicId);

    Optional<Item> findByPrivateId(@NonNull String privateId);

    @Query("SELECT i FROM Item i WHERE i.privateId IN :privateIds")
    List<Item> findAllByPrivateIds(@NonNull Iterable<String> privateIds);

    Optional<Item> findByPrivateIdAndOwner(@NonNull String id, @Nullable User owner);
}
