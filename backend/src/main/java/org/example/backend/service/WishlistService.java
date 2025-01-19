package org.example.backend.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.backend.model.Wishlist;
import org.example.backend.repository.WishlistRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;

    private final IdService idService;

    public Wishlist create(@NonNull Wishlist wishlist) {
        return create(wishlist, null);
    }

    public Wishlist create(@NonNull Wishlist wishlist, @Nullable Long userId) {
        String privateId = idService.generateId();
        String publicId = idService.generateId();

        return wishlistRepository.save(
                wishlist
                        .withPrivateId(privateId)
                        .withPublicId(publicId)
                        .withOwnerId(userId)
        );
    }

    public Wishlist updateById(@NonNull String id, @NonNull Wishlist wishlist) {
        return updateById(id, wishlist, null);
    }

    public Wishlist updateById(@NonNull String id, @NonNull Wishlist wishlist, @Nullable Long userId) {
        Wishlist existingWishlist = getById(id, userId);
        Wishlist updatedWishlist = wishlist
                .withId(existingWishlist.getId())
                .withPrivateId(existingWishlist.getPrivateId())
                .withPublicId(existingWishlist.getPublicId())
                .withOwnerId(existingWishlist.getOwnerId());

        return wishlistRepository.save(updatedWishlist);
    }

    public Wishlist getById(@NonNull String id, @Nullable Long userId) {
        return wishlistRepository.findByPrivateIdAndOwnerId(id, userId).orElseThrow();
    }

    public Wishlist getById(@NonNull String id) {
        return getById(id, null);
    }

    public Wishlist getByPublicId(@NonNull String publicId) {
        return wishlistRepository.findByPublicId(publicId).orElseThrow();
    }

    public void deleteById(@NonNull String id) {
        deleteById(id, null);
    }

    public void deleteById(@NonNull String id, @Nullable Long userId) {
        wishlistRepository.deleteByPrivateIdAndOwnerId(id, userId);
    }

    public List<Wishlist> getByUserId(@NonNull Long userId) {
        return wishlistRepository.findAllByOwnerId(userId);
    }
}
