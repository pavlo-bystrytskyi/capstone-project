package org.example.backend.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.backend.dto.IdResponse;
import org.example.backend.dto.ItemRequest;
import org.example.backend.dto.ItemResponse;
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

    @GetMapping("/public/{id}")
    public ItemResponse getByPublicId(@PathVariable @NonNull String id) {
        Item item = itemService.getByPublicId(id);

        return ItemResponse.of(item);
    }
}
