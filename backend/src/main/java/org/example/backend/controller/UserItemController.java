package org.example.backend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.example.backend.annotation.CurrentUser;
import org.example.backend.dto.response.IdResponse;
import org.example.backend.dto.request.item.ItemRequest;
import org.example.backend.dto.response.item.PrivateItemResponse;
import org.example.backend.dto.response.item.PublicItemResponse;
import org.example.backend.model.Item;
import org.example.backend.model.User;
import org.example.backend.service.ItemService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/item")
@RequiredArgsConstructor
@Validated
public class UserItemController {

    private final ItemService itemService;

    @PostMapping
    public IdResponse create(@CurrentUser @NotNull User user, @RequestBody @Valid ItemRequest itemRequest) {
        Item itemData = itemRequest.toItem();
        Item item = itemService.create(itemData, user);

        return IdResponse.of(item.getPublicId(), item.getPrivateId());
    }

    @GetMapping("/{privateId}")
    public PublicItemResponse getByPrivateId(@CurrentUser @NotNull User user, @PathVariable @NotNull String privateId) {
        Item item = itemService.getByPrivateId(privateId, user);

        return PublicItemResponse.of(item);
    }

    @DeleteMapping("/{privateId}")
    public void deleteByPrivateId(@CurrentUser @NotNull User user, @PathVariable @NotNull String privateId) {
        itemService.deleteByPrivateId(privateId, user);
    }

    @PutMapping("/{privateId}")
    public PrivateItemResponse updateByPrivateId(@CurrentUser @NotNull User user, @PathVariable @NotNull String privateId, @RequestBody @Valid ItemRequest itemRequest) {
        Item item = itemRequest.toItem();
        Item updatedItem = itemService.updateByPrivateId(privateId, item, user);

        return PrivateItemResponse.of(updatedItem);
    }
}
