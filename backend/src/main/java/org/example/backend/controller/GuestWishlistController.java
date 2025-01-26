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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/guest/wishlist")
@RequiredArgsConstructor
@Validated
public class GuestWishlistController {

    private final WishlistService wishlistService;

    @PostMapping
    public IdResponse create(@RequestBody @Valid WishlistRequest wishlistRequest) {
        Wishlist wishlistData = wishlistRequest.toWishlist();
        Wishlist wishlist = wishlistService.create(wishlistData);

        return IdResponse.of(wishlist.getPublicId(), wishlist.getPrivateId());
    }

    @GetMapping("/{privateId}")
    public PrivateWishlistResponse getByPrivateId(@PathVariable @NotNull String privateId) {
        Wishlist wishlist = wishlistService.getById(privateId);

        return PrivateWishlistResponse.of(wishlist);
    }

    @GetMapping("/public/{publicId}")
    public PublicWishlistResponse getByPublicId(@PathVariable @NotNull String publicId) {
        Wishlist wishlist = wishlistService.getActiveByPublicId(publicId);

        return PublicWishlistResponse.of(wishlist);
    }

    @DeleteMapping("/{privateId}")
    public void deleteById(@PathVariable @NotNull String privateId) {
        wishlistService.deleteById(privateId);
    }

    @PutMapping("/{privateId}")
    public IdResponse updateByPrivateId(@PathVariable @NonNull String privateId, @RequestBody @Valid WishlistRequest wishlistRequest) {
        Wishlist wishlist = wishlistRequest.toWishlist();
        Wishlist updatedWishlist = wishlistService.updateById(privateId, wishlist);

        return IdResponse.of(updatedWishlist.getPublicId(), updatedWishlist.getPrivateId());
    }
}
