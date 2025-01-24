package org.example.backend.util;

import org.example.backend.mock.dto.ItemIdsRequestMock;
import org.example.backend.mock.dto.WishlistRequestMock;
import org.example.backend.model.Item;
import org.example.backend.model.User;
import org.example.backend.model.Wishlist;
import org.example.backend.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestComponent
public class TestResultVerifier {

    @Autowired
    private WishlistRepository wishlistRepository;

    public void assertWishlistTableSize(int expectedSize) {
        List<Wishlist> wishlists = wishlistRepository.findAll();
        assertEquals(expectedSize, wishlists.size());
    }

    public void assertWishlistRequestSaved(User user, WishlistRequestMock expected, String wishlistPrivateId) {
        Optional<Wishlist> optional = wishlistRepository.findByPrivateIdAndOwner(wishlistPrivateId, user);
        assertTrue(optional.isPresent());
        Wishlist actual = optional.get();
        assertEquals(expected.title(), actual.getTitle());
        assertEquals(expected.description(), actual.getDescription());
        assertRequestItemEquality(expected.itemIds(), actual.getItems());
    }

    private void assertRequestItemEquality(List<ItemIdsRequestMock> expected, List<Item> actual) {
        assertEquals(expected.size(), actual.size());
        assertTrue(
                actual.stream().map(Item::getPrivateId).toList().containsAll(
                        expected.stream().map(ItemIdsRequestMock::privateId).toList()
                )
        );
        assertTrue(
                actual.stream().map(Item::getPublicId).toList().containsAll(
                        expected.stream().map(ItemIdsRequestMock::publicId).toList()
                )
        );
    }

    public void assertWishlistInTable(User owner, Wishlist expected) {
        Optional<Wishlist> optional = wishlistRepository.findByPrivateIdAndOwner(expected.getPrivateId(), owner);
        assertTrue(optional.isPresent());
        Wishlist actual = optional.get();
        assertEquals(expected.getPublicId(), actual.getPublicId());
        assertEquals(expected.getOwner().getId(), actual.getOwner().getId());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertWishlistItemEquality(expected.getItems(), actual.getItems());
    }

    private void assertWishlistItemEquality(List<Item> expected, List<Item> actual) {
        assertEquals(expected.size(), actual.size());
        assertTrue(
                actual.stream().map(Item::getPrivateId).toList().containsAll(
                        expected.stream().map(Item::getPrivateId).toList()
                )
        );
        assertTrue(
                actual.stream().map(Item::getPublicId).toList().containsAll(
                        expected.stream().map(Item::getPublicId).toList()
                )
        );
    }

}
