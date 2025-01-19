package org.example.backend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.example.backend.annotation.CurrentUser;
import org.example.backend.dto.request.wishlist.WishlistRequest;
import org.example.backend.dto.response.IdResponse;
import org.example.backend.dto.response.wishlist.PrivateWishlistResponse;
import org.example.backend.model.User;
import org.example.backend.model.Wishlist;
import org.example.backend.service.WishlistService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/wishlist")
@RequiredArgsConstructor
@Validated
public class UserWishlistController {

    private final WishlistService wishlistService;

    @PostMapping
    public IdResponse create(@CurrentUser @NotNull User user, @RequestBody @Valid WishlistRequest wishlistRequest) {
        Wishlist wishlistData = wishlistRequest.toWishlist();
        Wishlist wishlist = wishlistService.create(wishlistData, user.getId());

        return IdResponse.of(wishlist.getPublicId(), wishlist.getPrivateId());
    }

    @GetMapping
    public List<PrivateWishlistResponse> getAll(@CurrentUser @NotNull User user) {
        List<Wishlist> wishlistsList = wishlistService.getByUserId(user.getId());

        return wishlistsList.stream().map(PrivateWishlistResponse::of).toList();
    }

    @GetMapping("/{privateId}")
    public PrivateWishlistResponse getByPrivateId(@CurrentUser @NotNull User user, @PathVariable @NotNull String privateId) {
        Wishlist wishlist = wishlistService.getById(privateId, user.getId());

        return PrivateWishlistResponse.of(wishlist);
    }

    @DeleteMapping("/{privateId}")
    public void deleteByPrivateId(@CurrentUser @NotNull User user, @PathVariable @NotNull String privateId) {
        wishlistService.deleteById(privateId, user.getId());
    }

    @PutMapping("/{privateId}")
    public IdResponse updateByPrivateId(@CurrentUser @NotNull User user, @PathVariable @NotNull String privateId, @RequestBody @Valid WishlistRequest wishlistRequest) {
        Wishlist wishlist = wishlistRequest.toWishlist();
        Wishlist updatedWishlist = wishlistService.updateById(privateId, wishlist, user.getId());

        return IdResponse.of(updatedWishlist.getPublicId(), updatedWishlist.getPrivateId());
    }
}
