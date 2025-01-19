package org.example.backend.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.backend.model.Item;
import org.example.backend.model.item.ItemStatus;
import org.example.backend.repository.ItemRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    private final IdService idService;

    public Item create(@NonNull Item item) {
        return create(item, null);
    }

    public Item create(@NonNull Item item, @Nullable Long userId) {
        return itemRepository.save(
                item
                        .withPrivateId(idService.generateId())
                        .withPublicId(idService.generateId())
                        .withOwnerId(userId)
        );
    }

    public Item updateByPrivateId(@NonNull String id, @NonNull Item item) {
        return updateByPrivateId(id, item, null);
    }

    public Item updateByPrivateId(@NonNull String privateId, @NonNull Item item, @Nullable Long userId) {
        Item existingItem = getByPrivateId(privateId, userId);
        Item updatedItem = item
                .withId(existingItem.getId())
                .withPrivateId(existingItem.getPrivateId())
                .withPublicId(existingItem.getPublicId())
                .withOwnerId(userId);

        return itemRepository.save(updatedItem);
    }

    public Item getByPrivateId(@NonNull String id, @Nullable Long userId) {
        return itemRepository.findByPrivateIdAndOwnerId(id, userId).orElseThrow();
    }

    public Item getByPrivateId(@NonNull String id) {
        return getByPrivateId(id, null);
    }

    public Item updateStatusByPublicId(@NonNull String publicId, @NonNull ItemStatus status) {
        Item item = getByPublicId(publicId);

        return itemRepository.save(item.withStatus(status));
    }

    public Item getByPublicId(@NonNull String publicId) {
        return itemRepository.findByPublicId(publicId).orElseThrow();
    }

    public void deleteByPrivateId(@NonNull String privateId) {
        deleteByPrivateId(privateId, null);
    }

    public void deleteByPrivateId(@NonNull String privateId, @Nullable Long userId) {
        itemRepository.deleteByPrivateIdAndOwnerId(privateId, userId);
    }
}
