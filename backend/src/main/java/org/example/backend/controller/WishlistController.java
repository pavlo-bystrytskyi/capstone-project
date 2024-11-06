package org.example.backend.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.backend.dto.IdResponse;
import org.example.backend.dto.wishlist.PrivateWishlistResponse;
import org.example.backend.dto.wishlist.WishlistRequest;
import org.example.backend.dto.wishlist.PublicWishlistResponse;
import org.example.backend.model.Wishlist;
import org.example.backend.service.WishlistService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping
    public IdResponse create(@RequestBody @NonNull WishlistRequest wishlistRequest) {
        Wishlist wishlistData = wishlistRequest.toWishlist();
        Wishlist wishlist = wishlistService.create(wishlistData);

        return IdResponse.of(wishlist.getPublicId(), wishlist.getId());
    }

    @GetMapping("/{id}")
    public PrivateWishlistResponse getById(@PathVariable @NonNull String id) {
        Wishlist wishlist = wishlistService.getById(id);

        return PrivateWishlistResponse.of(wishlist);
    }

    @GetMapping("/public/{id}")
    public PublicWishlistResponse getByPublicId(@PathVariable @NonNull String id) {
        Wishlist wishlist = wishlistService.getByPublicId(id);

        return PublicWishlistResponse.of(wishlist);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable @NonNull String id) {
        wishlistService.deleteById(id);
    }

    @PutMapping("/{id}")
    public PrivateWishlistResponse updateById(@PathVariable @NonNull String id, @RequestBody @NonNull WishlistRequest wishlistRequest) {
        Wishlist wishlist = wishlistRequest.toWishlist();
        Wishlist updatedWishlist = wishlistService.updateById(id, wishlist);

        return PrivateWishlistResponse.of(updatedWishlist);
    }
}
