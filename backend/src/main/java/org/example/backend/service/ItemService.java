package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.model.Item;
import org.example.backend.repository.ItemRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    private final IdService idService;

    public Item create(Item item) {
        String id = idService.generateId();
        return itemRepository.save(item.withPublicId(id));
    }
}
