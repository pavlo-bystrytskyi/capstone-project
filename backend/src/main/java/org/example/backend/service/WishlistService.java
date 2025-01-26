package org.example.backend.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.backend.model.Item;
import org.example.backend.model.User;
import org.example.backend.model.Wishlist;
import org.example.backend.repository.ItemRepository;
import org.example.backend.repository.WishlistRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class WishlistService {

    private final ItemRepository itemRepository;

    private final WishlistRepository wishlistRepository;

    private final IdService idService;

    public Wishlist create(@NonNull Wishlist wishlist) {
        return create(wishlist, null);
    }

    public Wishlist create(@NonNull Wishlist wishlist, @Nullable User user) {
        String privateId = idService.generateId();
        String publicId = idService.generateId();
        List<String> privateIds = wishlist.getItems().stream().map(Item::getPrivateId).toList();
        List<Item> items = itemRepository.findAllByPrivateIds(privateIds);

        return wishlistRepository.save(
                wishlist
                        .withPrivateId(privateId)
                        .withPublicId(publicId)
                        .withOwner(user)
                        .withItems(items)
        );
    }

    public Wishlist updateById(@NonNull String id, @NonNull Wishlist wishlist) {
        return updateById(id, wishlist, null);
    }

    public Wishlist updateById(@NonNull String id, @NonNull Wishlist wishlist, @Nullable User user) {
        Wishlist existingWishlist = getById(id, user);
        List<String> privateIds = wishlist.getItems().stream().map(Item::getPrivateId).toList();
        List<Item> items = itemRepository.findAllByPrivateIds(privateIds);

        Wishlist updatedWishlist = wishlist
                .withId(existingWishlist.getId())
                .withPrivateId(existingWishlist.getPrivateId())
                .withPublicId(existingWishlist.getPublicId())
                .withOwner(existingWishlist.getOwner())
                .withItems(items);

        return wishlistRepository.save(updatedWishlist);
    }

    public Wishlist getById(@NonNull String id, @Nullable User user) {
        return wishlistRepository.findByPrivateIdAndOwner(id, user).orElseThrow();
    }

    public Wishlist getById(@NonNull String id) {
        return getById(id, null);
    }

    public Wishlist getActiveByPublicId(@NonNull String publicId) {
        return wishlistRepository.findByPublicIdAndActiveIsTrue(publicId).orElseThrow();
    }

    public void deleteById(@NonNull String id) {
        deleteById(id, null);
    }

    public void deleteById(@NonNull String id, @Nullable User user) {
        wishlistRepository.deleteByPrivateIdAndOwner(id, user);
    }

    public List<Wishlist> getByUser(@NonNull User user) {
        return wishlistRepository.findAllByOwner(user);
    }
}
