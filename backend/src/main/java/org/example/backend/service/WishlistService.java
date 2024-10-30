package org.example.backend.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.backend.model.Wishlist;
import org.example.backend.repository.WishlistRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;

    private final IdService idService;

    public Wishlist create(Wishlist wishlist) {
        String id = idService.generateId();

        return wishlistRepository.save(wishlist.withPublicId(id));
    }

    public Wishlist updateById(@NonNull String id, @NonNull Wishlist wishlist) {
        Wishlist existingWishlist = getById(id);
        Wishlist updatedWishlist = wishlist
                .withId(existingWishlist.getId())
                .withPublicId(existingWishlist.getPublicId());

        return wishlistRepository.save(updatedWishlist);
    }

    public Wishlist getById(@NonNull String id) {
        return wishlistRepository.findById(id).orElseThrow();
    }

    public Wishlist getByPublicId(@NonNull String id) {
        return wishlistRepository.findByPublicId(id).orElseThrow();
    }

    public void deleteById(@NonNull String id) {
        wishlistRepository.deleteById(id);
    }
}
