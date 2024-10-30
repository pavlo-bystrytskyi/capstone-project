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

    public Wishlist getById(@NonNull String id) {
        return wishlistRepository.findById(id).orElseThrow();
    }
}
