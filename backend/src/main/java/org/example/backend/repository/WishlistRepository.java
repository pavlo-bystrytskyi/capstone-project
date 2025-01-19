package org.example.backend.repository;

import lombok.NonNull;
import org.example.backend.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    @Query("SELECT w FROM Wishlist w LEFT JOIN FETCH w.items WHERE w.publicId = :publicId")
    Optional<Wishlist> findByPublicId(@NonNull String publicId);

    @Query("SELECT w FROM Wishlist w LEFT JOIN FETCH w.items WHERE w.privateId = :privateId AND (:ownerId IS NULL OR w.ownerId = :ownerId)")
    Optional<Wishlist> findByPrivateIdAndOwnerId(@NonNull String privateId, @Nullable Long ownerId);

    void deleteByPrivateIdAndOwnerId(@NonNull String privateId, @Nullable Long ownerId);

    @Query("SELECT w FROM Wishlist w LEFT JOIN FETCH w.items WHERE :ownerId IS NULL OR w.ownerId = :ownerId")
    List<Wishlist> findAllByOwnerId(Long ownerId);
}
