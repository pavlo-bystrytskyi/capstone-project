package org.example.backend.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.backend.annotation.CurrentUser;
import org.example.backend.dto.IdResponse;
import org.example.backend.dto.wishlist.PrivateWishlistResponse;
import org.example.backend.dto.wishlist.PublicWishlistResponse;
import org.example.backend.dto.wishlist.WishlistRequest;
import org.example.backend.model.User;
import org.example.backend.model.Wishlist;
import org.example.backend.service.WishlistService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/wishlist")
@RequiredArgsConstructor
public class UserWishlistController {

    private final WishlistService wishlistService;

    @PostMapping
    public IdResponse create(@CurrentUser User user, @RequestBody @NonNull WishlistRequest wishlistRequest) {
        Wishlist wishlistData = wishlistRequest.toWishlist();
        Wishlist wishlist = wishlistService.create(wishlistData, user);

        return IdResponse.of(wishlist.getPublicId(), wishlist.getId());
    }

    @GetMapping("/{id}")
    public PrivateWishlistResponse getById(@CurrentUser User user, @PathVariable @NonNull String id) {
        Wishlist wishlist = wishlistService.getById(id, user);

        return PrivateWishlistResponse.of(wishlist);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@CurrentUser User user, @PathVariable @NonNull String id) {
        wishlistService.deleteById(id, user);
    }

    @PutMapping("/{id}")
    public IdResponse updateById(@CurrentUser User user, @PathVariable @NonNull String id, @RequestBody @NonNull WishlistRequest wishlistRequest) {
        Wishlist wishlist = wishlistRequest.toWishlist();
        Wishlist updatedWishlist = wishlistService.updateById(id, wishlist, user);

        return IdResponse.of(updatedWishlist.getPublicId(), updatedWishlist.getId());
    }
}
