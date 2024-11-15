package org.example.backend.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.backend.annotation.CurrentUserId;
import org.example.backend.dto.IdResponse;
import org.example.backend.dto.wishlist.PrivateWishlistResponse;
import org.example.backend.dto.wishlist.WishlistRequest;
import org.example.backend.model.Wishlist;
import org.example.backend.service.WishlistService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/wishlist")
@RequiredArgsConstructor
public class UserWishlistController {

    private final WishlistService wishlistService;

    @PostMapping
    public IdResponse create(@CurrentUserId String userId, @RequestBody @NonNull WishlistRequest wishlistRequest) {
        Wishlist wishlistData = wishlistRequest.toWishlist();
        Wishlist wishlist = wishlistService.create(wishlistData, userId);

        return IdResponse.of(wishlist.getPublicId(), wishlist.getId());
    }

    @GetMapping
    public List<PrivateWishlistResponse> getAll(@CurrentUserId String userId) {
        List<Wishlist> wishlistsList = wishlistService.getByUserId(userId);

        return wishlistsList.stream().map(PrivateWishlistResponse::of).toList();
    }

    @GetMapping("/{id}")
    public PrivateWishlistResponse getById(@CurrentUserId String userId, @PathVariable @NonNull String id) {
        Wishlist wishlist = wishlistService.getById(id, userId);

        return PrivateWishlistResponse.of(wishlist);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@CurrentUserId String userId, @PathVariable @NonNull String id) {
        wishlistService.deleteById(id, userId);
    }

    @PutMapping("/{id}")
    public IdResponse updateById(@CurrentUserId String userId, @PathVariable @NonNull String id, @RequestBody @NonNull WishlistRequest wishlistRequest) {
        Wishlist wishlist = wishlistRequest.toWishlist();
        Wishlist updatedWishlist = wishlistService.updateById(id, wishlist, userId);

        return IdResponse.of(updatedWishlist.getPublicId(), updatedWishlist.getId());
    }
}
