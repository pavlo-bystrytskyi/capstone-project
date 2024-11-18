package org.example.backend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.backend.annotation.CurrentUserId;
import org.example.backend.dto.response.IdResponse;
import org.example.backend.dto.request.item.ItemRequest;
import org.example.backend.dto.response.item.PrivateItemResponse;
import org.example.backend.dto.response.item.PublicItemResponse;
import org.example.backend.model.Item;
import org.example.backend.service.ItemService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/item")
@RequiredArgsConstructor
public class UserItemController {

    private final ItemService itemService;

    @PostMapping
    public IdResponse create(@CurrentUserId String userId, @RequestBody @Valid ItemRequest itemRequest) {
        Item itemData = itemRequest.toItem();
        Item item = itemService.create(itemData, userId);

        return IdResponse.of(item.getPublicId(), item.getId());
    }

    @GetMapping("/{id}")
    public PublicItemResponse getById(@CurrentUserId String userId, @PathVariable @NotNull String id) {
        Item item = itemService.getById(id, userId);

        return PublicItemResponse.of(item);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@CurrentUserId String userId, @PathVariable @NotNull String id) {
        itemService.deleteById(id, userId);
    }

    @PutMapping("/{id}")
    public PrivateItemResponse updateById(@CurrentUserId String userId, @PathVariable @NotNull String id, @RequestBody @Valid ItemRequest itemRequest) {
        Item item = itemRequest.toItem();
        Item updatedItem = itemService.updateById(id, item, userId);

        return PrivateItemResponse.of(updatedItem);
    }
}
