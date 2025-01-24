package org.example.backend.repository;

import lombok.NonNull;
import org.example.backend.model.Item;
import org.example.backend.model.User;
import org.example.backend.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    @Query("SELECT w FROM Wishlist w LEFT JOIN FETCH w.items WHERE w.publicId = :publicId")
    Optional<Wishlist> findByPublicId(@NonNull String publicId);

    @Query("SELECT w FROM Wishlist w LEFT JOIN FETCH w.items WHERE w.privateId = :privateId AND (:owner IS NULL OR w.owner = :owner)")
    Optional<Wishlist> findByPrivateIdAndOwner(@NonNull String privateId, @Nullable User owner);

    void deleteByPrivateIdAndOwner(@NonNull String privateId, @Nullable User owner);

    @Query("SELECT w FROM Wishlist w LEFT JOIN FETCH w.items WHERE w.owner = :owner")
    List<Wishlist> findAllByOwner(User owner);

    List<Wishlist> findAllByItemsContaining(Item item);
}
