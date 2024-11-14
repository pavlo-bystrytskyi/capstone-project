package org.example.backend.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.backend.annotation.CurrentUser;
import org.example.backend.dto.IdResponse;
import org.example.backend.dto.item.ItemRequest;
import org.example.backend.dto.item.PrivateItemResponse;
import org.example.backend.dto.item.PublicItemResponse;
import org.example.backend.model.Item;
import org.example.backend.model.User;
import org.example.backend.service.ItemService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/item")
@RequiredArgsConstructor
public class UserItemController {

    private final ItemService itemService;

    @PostMapping
    public IdResponse create(@CurrentUser User user, @RequestBody @NonNull ItemRequest itemRequest) {
        Item itemData = itemRequest.toItem();
        Item item = itemService.create(itemData, user);

        return IdResponse.of(item.getPublicId(), item.getId());
    }

    @GetMapping("/{id}")
    public PublicItemResponse getById(@CurrentUser User user, @PathVariable @NonNull String id) {
        Item item = itemService.getById(id, user);

        return PublicItemResponse.of(item);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@CurrentUser User user, @PathVariable @NonNull String id) {
        itemService.deleteById(id, user);
    }

    @PutMapping("/{id}")
    public PrivateItemResponse updateById(@CurrentUser User user, @PathVariable @NonNull String id, @RequestBody @NonNull ItemRequest itemRequest) {
        Item item = itemRequest.toItem();
        Item updatedItem = itemService.updateById(id, item, user);

        return PrivateItemResponse.of(updatedItem);
    }
}
