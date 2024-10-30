package org.example.backend.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.backend.dto.IdResponse;
import org.example.backend.dto.item.ItemRequest;
import org.example.backend.dto.item.ItemResponse;
import org.example.backend.model.Item;
import org.example.backend.service.ItemService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/item")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public IdResponse create(@RequestBody @NonNull ItemRequest itemRequest) {
        Item item = itemRequest.toItem();

        return IdResponse.of(itemService.create(item).getId());
    }

    @GetMapping("/{id}")
    public ItemResponse getById(@PathVariable @NonNull String id) {
        Item item = itemService.getById(id);

        return ItemResponse.of(item);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable @NonNull String id) {
        itemService.deleteById(id);
    }

    @GetMapping("/public/{id}")
    public ItemResponse getByPublicId(@PathVariable @NonNull String id) {
        Item item = itemService.getByPublicId(id);

        return ItemResponse.of(item);
    }

    @PutMapping("/{id}")
    public ItemResponse updateById(@PathVariable @NonNull String id, @RequestBody @NonNull ItemRequest itemRequest) {
        Item item = itemRequest.toItem();
        Item updatedItem = itemService.updateById(id, item);

        return ItemResponse.of(updatedItem);
    }
}
