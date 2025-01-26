package org.example.backend.util;

import jakarta.annotation.Nullable;
import org.example.backend.dto.response.wishlist.PublicItemIdsResponse;
import org.example.backend.dto.response.wishlist.PublicWishlistResponse;
import org.example.backend.mock.dto.ItemIdsRequestMock;
import org.example.backend.mock.dto.WishlistRequestMock;
import org.example.backend.model.Item;
import org.example.backend.model.User;
import org.example.backend.model.Wishlist;
import org.example.backend.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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
        assertEquals(expected.active(), actual.getActive());
        assertZonedDateTimeEquality(expected.deactivationDate(), actual.getDeactivationDate());
        assertRequestItemEquality(expected.itemIds(), actual.getItems());
    }

    private void assertZonedDateTimeEquality(ZonedDateTime expected, ZonedDateTime actual) {
        if (expected == null && actual == null) {
            return;
        }
        assertNotNull(expected);
        assertNotNull(actual);
        ZonedDateTime normalizedExpected = expected.withZoneSameInstant(ZoneOffset.UTC);
        ZonedDateTime normalizedActual = actual.withZoneSameInstant(ZoneOffset.UTC);
        assertEquals(normalizedExpected, normalizedActual);
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

    public void assertWishlistInTable(@Nullable User owner, Wishlist expected) {
        Optional<Wishlist> optional = wishlistRepository.findByPrivateIdAndOwner(expected.getPrivateId(), owner);
        assertTrue(optional.isPresent());
        Wishlist actual = optional.get();
        assertEquals(expected.getPublicId(), actual.getPublicId());
        assertEquals(expected.getOwner(), actual.getOwner());
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

    public void assertPublicWishlistResponseInTable(PublicWishlistResponse actual, String id) {
        Optional<Wishlist> optional = wishlistRepository.findByPrivateIdAndOwner(id, null);
        assertTrue(optional.isPresent());
        Wishlist expected = optional.get();
        assertEquals(expected.getTitle(), actual.title());
        assertEquals(expected.getDescription(), actual.description());
        assertPublicResponseItemEquality(expected.getItems(), actual.itemIds());
    }

    private void assertPublicResponseItemEquality(List<Item> expected, List<PublicItemIdsResponse> actual) {
        assertEquals(expected.size(), actual.size());
        expected.forEach(
                (Item item) -> assertFalse(
                        actual.stream().map(PublicItemIdsResponse::privateId).toList().contains(item.getPrivateId())
                )
        );
        assertTrue(
                actual.stream().map(PublicItemIdsResponse::publicId).toList().containsAll(
                        expected.stream().map(Item::getPublicId).toList()
                )
        );
    }
}
