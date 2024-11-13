package org.example.backend.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.backend.dto.IdResponse;
import org.example.backend.dto.item.ItemRequest;
import org.example.backend.dto.item.PrivateItemResponse;
import org.example.backend.dto.item.PublicItemResponse;
import org.example.backend.dto.item.ItemStatusRequest;
import org.example.backend.model.Item;
import org.example.backend.service.ItemService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/guest/item")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public IdResponse create(@RequestBody @NonNull ItemRequest itemRequest) {
        Item itemData = itemRequest.toItem();
        Item item = itemService.create(itemData);

        return IdResponse.of(item.getPublicId(), item.getId());
    }

    @GetMapping("/{id}")
    public PublicItemResponse getById(@PathVariable @NonNull String id) {
        Item item = itemService.getById(id);

        return PublicItemResponse.of(item);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable @NonNull String id) {
        itemService.deleteById(id);
    }

    @GetMapping("/public/{id}")
    public PublicItemResponse getByPublicId(@PathVariable @NonNull String id) {
        Item item = itemService.getByPublicId(id);

        return PublicItemResponse.of(item);
    }

    @PutMapping("/public/{id}")
    public PublicItemResponse updateStatusByPublicId(@PathVariable @NonNull String id, @RequestBody @NonNull ItemStatusRequest itemStatusRequest) {
        Item item = itemService.updateStatusByPublicId(id, itemStatusRequest.status());

        return PublicItemResponse.of(item);
    }

    @PutMapping("/{id}")
    public PrivateItemResponse updateById(@PathVariable @NonNull String id, @RequestBody @NonNull ItemRequest itemRequest) {
        Item item = itemRequest.toItem();
        Item updatedItem = itemService.updateById(id, item);

        return PrivateItemResponse.of(updatedItem);
    }
}
