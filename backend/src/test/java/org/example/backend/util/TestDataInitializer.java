package org.example.backend.util;

import jakarta.annotation.Nullable;
import org.example.backend.model.Item;
import org.example.backend.model.Product;
import org.example.backend.model.User;
import org.example.backend.model.Wishlist;
import org.example.backend.model.item.ItemStatus;
import org.example.backend.repository.UserRepository;
import org.example.backend.service.ItemService;
import org.example.backend.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import java.util.List;

@TestComponent
public class TestDataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WishlistService wishlistService;

    @Autowired
    private ItemService itemService;

    public User createUser(String externalId) {
        User user = User.builder().externalId(externalId).build();
        userRepository.save(user);

        return userRepository.findByExternalId(externalId).orElse(null);
    }

    public Product createProduct(String title, String description, String link) {
        return Product.builder().title(title).description(description).link(link).build();
    }

    public Item createItem(Product product, ItemStatus status, Double quantity, @Nullable User owner) {
        Item item = Item.builder()
                .status(status)
                .product(product)
                .quantity(quantity)
                .build();

        return itemService.create(item, owner);
    }

    public Wishlist createWishlist(String title, String description, List<Item> items, @Nullable User owner) {
        return wishlistService.create(
                Wishlist.builder()
                        .title(title)
                        .description(description)
                        .items(items)
                        .build(),
                owner
        );
    }
}
