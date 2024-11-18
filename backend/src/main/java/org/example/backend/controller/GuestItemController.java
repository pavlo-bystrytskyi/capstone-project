package org.example.backend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.backend.dto.response.IdResponse;
import org.example.backend.dto.request.item.ItemRequest;
import org.example.backend.dto.response.item.PrivateItemResponse;
import org.example.backend.dto.response.item.PublicItemResponse;
import org.example.backend.dto.request.item.ItemStatusRequest;
import org.example.backend.model.Item;
import org.example.backend.service.ItemService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/guest/item")
@RequiredArgsConstructor
public class GuestItemController {

    private final ItemService itemService;

    @PostMapping
    public IdResponse create(@RequestBody @Valid ItemRequest itemRequest) {
        Item itemData = itemRequest.toItem();
        Item item = itemService.create(itemData);

        return IdResponse.of(item.getPublicId(), item.getId());
    }

    @GetMapping("/{id}")
    public PublicItemResponse getById(@PathVariable @NotNull String id) {
        Item item = itemService.getById(id);

        return PublicItemResponse.of(item);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable @NotNull String id) {
        itemService.deleteById(id);
    }

    @GetMapping("/public/{id}")
    public PublicItemResponse getByPublicId(@PathVariable @NotNull String id) {
        Item item = itemService.getByPublicId(id);

        return PublicItemResponse.of(item);
    }

    @PutMapping("/public/{id}")
    public PublicItemResponse updateStatusByPublicId(@PathVariable @NotNull String id, @RequestBody @Valid ItemStatusRequest itemStatusRequest) {
        Item item = itemService.updateStatusByPublicId(id, itemStatusRequest.status());

        return PublicItemResponse.of(item);
    }

    @PutMapping("/{id}")
    public PrivateItemResponse updateById(@PathVariable @NotNull String id, @RequestBody @Valid ItemRequest itemRequest) {
        Item item = itemRequest.toItem();
        Item updatedItem = itemService.updateById(id, item);

        return PrivateItemResponse.of(updatedItem);
    }
}
