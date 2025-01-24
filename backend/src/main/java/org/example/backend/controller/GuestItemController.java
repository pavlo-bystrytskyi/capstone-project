package org.example.backend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.example.backend.dto.request.item.ItemRequest;
import org.example.backend.dto.request.item.ItemStatusRequest;
import org.example.backend.dto.response.IdResponse;
import org.example.backend.dto.response.item.PrivateItemResponse;
import org.example.backend.dto.response.item.PublicItemResponse;
import org.example.backend.model.Item;
import org.example.backend.service.ItemService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/guest/item")
@RequiredArgsConstructor
@Validated
public class GuestItemController {

    private final ItemService itemService;

    @PostMapping
    public IdResponse create(@RequestBody @Valid ItemRequest itemRequest) {
        Item itemData = itemRequest.toItem();
        Item item = itemService.create(itemData);

        return IdResponse.of(item.getPublicId(), item.getPrivateId());
    }

    @GetMapping("/{privateId}")
    public PublicItemResponse getByPrivateId(@PathVariable @NotNull String privateId) {
        Item item = itemService.getByPrivateId(privateId);

        return PublicItemResponse.of(item);
    }

    @DeleteMapping("/{privateId}")
    public void deleteByPrivateId(@PathVariable @NotNull String privateId) {
        itemService.deleteByPrivateId(privateId);
    }

    @GetMapping("/public/{publicId}")
    public PublicItemResponse getByPublicId(@PathVariable @NotNull String publicId) {
        Item item = itemService.getByPublicId(publicId);

        return PublicItemResponse.of(item);
    }

    @PutMapping("/public/{publicId}")
    public PublicItemResponse updateStatusByPublicId(@PathVariable @NotNull String publicId, @RequestBody @Valid ItemStatusRequest itemStatusRequest) {
        Item item = itemService.updateStatusByPublicId(publicId, itemStatusRequest.status());

        return PublicItemResponse.of(item);
    }

    @PutMapping("/{privateId}")
    public PrivateItemResponse updateById(@PathVariable @NotNull String privateId, @RequestBody @Valid ItemRequest itemRequest) {
        Item item = itemRequest.toItem();
        Item updatedItem = itemService.updateByPrivateId(privateId, item);

        return PrivateItemResponse.of(updatedItem);
    }
}
