package org.example.backend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.backend.dto.response.IdResponse;
import org.example.backend.dto.response.wishlist.PrivateWishlistResponse;
import org.example.backend.dto.request.wishlist.WishlistRequest;
import org.example.backend.dto.response.wishlist.PublicWishlistResponse;
import org.example.backend.model.Wishlist;
import org.example.backend.service.WishlistService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/guest/wishlist")
@RequiredArgsConstructor
public class GuestWishlistController {

    private final WishlistService wishlistService;

    @PostMapping
    public IdResponse create(@RequestBody @Valid WishlistRequest wishlistRequest) {
        Wishlist wishlistData = wishlistRequest.toWishlist();
        Wishlist wishlist = wishlistService.create(wishlistData);

        return IdResponse.of(wishlist.getPublicId(), wishlist.getId());
    }

    @GetMapping("/{id}")
    public PrivateWishlistResponse getById(@PathVariable @NotNull String id) {
        Wishlist wishlist = wishlistService.getById(id);

        return PrivateWishlistResponse.of(wishlist);
    }

    @GetMapping("/public/{id}")
    public PublicWishlistResponse getByPublicId(@PathVariable @NotNull String id) {
        Wishlist wishlist = wishlistService.getByPublicId(id);

        return PublicWishlistResponse.of(wishlist);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable @NotNull String id) {
        wishlistService.deleteById(id);
    }

    @PutMapping("/{id}")
    public IdResponse updateById(@PathVariable @NonNull String id, @RequestBody @Valid WishlistRequest wishlistRequest) {
        Wishlist wishlist = wishlistRequest.toWishlist();
        Wishlist updatedWishlist = wishlistService.updateById(id, wishlist);

        return IdResponse.of(updatedWishlist.getPublicId(), updatedWishlist.getId());
    }
}
