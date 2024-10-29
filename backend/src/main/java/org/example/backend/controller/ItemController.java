package org.example.backend.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.backend.dto.IdResponse;
import org.example.backend.dto.ItemRequest;
import org.example.backend.model.Item;
import org.example.backend.service.ItemService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
