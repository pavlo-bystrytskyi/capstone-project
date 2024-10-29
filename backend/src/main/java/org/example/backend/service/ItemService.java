package org.example.backend.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.backend.model.Item;
import org.example.backend.repository.ItemRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    private final IdService idService;

    public Item create(@NonNull Item item) {
        return itemRepository.save(
                item
                        .withId(idService.generateId())
                        .withPublicId(idService.generateId())
        );
    }

    public Item getById(@NonNull String id) {
        return itemRepository.findById(id).orElseThrow();
    }

    public Item getByPublicId(@NonNull String publicId) {
        return itemRepository.findByPublicId(publicId).orElseThrow();
    }
}