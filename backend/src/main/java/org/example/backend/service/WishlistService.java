package org.example.backend.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.backend.model.Wishlist;
import org.example.backend.repository.WishlistRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;

    private final IdService idService;

    private final UserService userService;

    public Wishlist create(@NonNull Wishlist wishlist) {
        return create(wishlist, null);
    }

    public Wishlist create(@NonNull Wishlist wishlist, @Nullable String userId) {
        String id = idService.generateId();
        String publicId = idService.generateId();

        return wishlistRepository.save(
                wishlist
                        .withId(id)
                        .withPublicId(publicId)
                        .withOwnerId(userId)
        );
    }

    public Wishlist updateById(@NonNull String id, @NonNull Wishlist wishlist) {
        return updateById(id, wishlist, null);
    }

    public Wishlist updateById(@NonNull String id, @NonNull Wishlist wishlist, @Nullable String userId) {
        Wishlist existingWishlist = getById(id, userId);
        Wishlist updatedWishlist = wishlist
                .withId(existingWishlist.getId())
                .withPublicId(existingWishlist.getPublicId())
                .withOwnerId(existingWishlist.getOwnerId());

        return wishlistRepository.save(updatedWishlist);
    }

    public Wishlist getById(@NonNull String id, @Nullable String userId) {
        return wishlistRepository.findByIdAndOwnerId(id, userId).orElseThrow();
    }

    public Wishlist getById(@NonNull String id) {
        return getById(id, null);
    }

    public Wishlist getByPublicId(@NonNull String id) {
        return wishlistRepository.findByPublicId(id).orElseThrow();
    }

    public void deleteById(@NonNull String id) {
        deleteById(id, null);
    }

    public void deleteById(@NonNull String id, @Nullable String userId) {
        wishlistRepository.deleteByIdAndOwnerId(id, userId);
    }
}
