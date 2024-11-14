package org.example.backend.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.backend.model.Item;
import org.example.backend.model.User;
import org.example.backend.model.item.ItemStatus;
import org.example.backend.repository.ItemRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    private final IdService idService;

    private final UserService userService;

    public Item create(@NonNull Item item) {
        return create(item, null);
    }

    public Item create(@NonNull Item item, @Nullable User user) {
        String userId = userService.getUserId(user);

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

    public Item updateById(@NonNull String id, @NonNull Item item, @Nullable User user) {
        Item existingItem = getById(id, user);
        Item updatedItem = item
                .withId(existingItem.getId())
                .withPublicId(existingItem.getPublicId());

        return itemRepository.save(updatedItem);
    }

    public Item getById(@NonNull String id, @Nullable User user) {
        String userId = userService.getUserId(user);

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

    public void deleteById(@NonNull String id, @Nullable User user) {
        String userId = userService.getUserId(user);

        itemRepository.deleteByIdAndOwnerId(id, userId);
    }
}
