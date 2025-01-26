package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.model.Wishlist;
import org.example.backend.repository.WishlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistDeactivationService {

    private final WishlistRepository wishlistRepository;

    @Transactional
    public void deactivateExpired() {
        ZonedDateTime now = ZonedDateTime.now();
        List<Wishlist> expiredWishlists = wishlistRepository
                .findByActiveTrueAndDeactivationDateBefore(now)
                .stream()
                .map(
                        wishlist -> wishlist.withActive(false)
                )
                .toList();

        wishlistRepository.saveAll(expiredWishlists);
    }
}
