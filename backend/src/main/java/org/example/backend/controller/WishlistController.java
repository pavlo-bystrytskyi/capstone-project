package org.example.backend.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.backend.dto.IdResponse;
import org.example.backend.dto.WishlistRequest;
import org.example.backend.model.Wishlist;
import org.example.backend.service.WishlistService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping
    public IdResponse create(@RequestBody @NonNull WishlistRequest wishlistRequest) {
        Wishlist wishlist = wishlistRequest.toWishlist();

        return IdResponse.of(wishlistService.create(wishlist).getId());
    }
}
