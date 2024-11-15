package org.example.backend.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.backend.model.Item;
import org.example.backend.model.item.ItemStatus;
import org.example.backend.repository.ItemRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    private final IdService idService;

    public Item create(@NonNull Item item) {
        return create(item, null);
    }

    public Item create(@NonNull Item item, @Nullable String userId) {
        return itemRepository.save(
                item
                        .withId(idService.generateId())
                        .withPublicId(idService.generateId())
                        .withOwnerId(userId)
        );
    }

    public Item updateById(@NonNull String id, @NonNull Item item) {
        return updateById(id, item, null);
    }

    public Item updateById(@NonNull String id, @NonNull Item item, @Nullable String userId) {
        Item existingItem = getById(id, userId);
        Item updatedItem = item
                .withId(existingItem.getId())
                .withPublicId(existingItem.getPublicId());

        return itemRepository.save(updatedItem);
    }

    public Item getById(@NonNull String id, @Nullable String userId) {
        return itemRepository.findByIdAndOwnerId(id, userId).orElseThrow();
    }

    public Item getById(@NonNull String id) {
        return getById(id, null);
    }

    public Item updateStatusByPublicId(@NonNull String publicId, @NonNull ItemStatus status) {
        Item item = getByPublicId(publicId);

        return itemRepository.save(item.withStatus(status));
    }

    public Item getByPublicId(@NonNull String publicId) {
        return itemRepository.findByPublicId(publicId).orElseThrow();
    }

    public void deleteById(@NonNull String id) {
        deleteById(id, null);
    }

    public void deleteById(@NonNull String id, @Nullable String userId) {
        itemRepository.deleteByIdAndOwnerId(id, userId);
    }
}
