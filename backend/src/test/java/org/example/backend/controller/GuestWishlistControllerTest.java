package org.example.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.backend.dto.response.ErrorResponse;
import org.example.backend.dto.response.IdResponse;
import org.example.backend.dto.response.wishlist.PrivateItemIdsResponse;
import org.example.backend.dto.response.wishlist.PrivateWishlistResponse;
import org.example.backend.dto.response.wishlist.PublicItemIdsResponse;
import org.example.backend.dto.response.wishlist.PublicWishlistResponse;
import org.example.backend.mock.dto.ItemIdsRequestMock;
import org.example.backend.mock.dto.WishlistRequestMock;
import org.example.backend.model.Wishlist;
import org.example.backend.model.wishlist.ItemIdStorage;
import org.example.backend.repository.WishlistRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
class GuestWishlistControllerTest {

    private static final String URL_BASE = "/api/guest/wishlist";
    private static final String URL_WITH_ID = "/api/guest/wishlist/{id}";
    private static final String URL_PUBLIC_WITH_ID = "/api/guest/wishlist/public/{id}";

    private static final String ID_FIRST = "some id 1";
    private static final String ID_SECOND = "some id 2";
    private static final String PUBLIC_ID_FIRST = "some public id 1";
    private static final String PUBLIC_ID_SECOND = "some public id 2";
    private static final String TITLE_FIRST = "some wishlist title 1";
    private static final String TITLE_SECOND = "some wishlist title 2";
    private static final String TITLE_THIRD = "some wishlist title 3";
    private static final String DESCRIPTION_FIRST = "some wishlist description 1";
    private static final String DESCRIPTION_SECOND = "some wishlist description 2";
    private static final String DESCRIPTION_THIRD = "some wishlist description 3";
    private static final String PRIVATE_ITEM_ID_FIRST = "some item id 1";
    private static final String PRIVATE_ITEM_ID_SECOND = "some item id 2";
    private static final String PRIVATE_ITEM_ID_THIRD = "some item id 3";
    private static final String PUBLIC_ITEM_ID_FIRST = "some public item id 1";
    private static final String PUBLIC_ITEM_ID_SECOND = "some public item id 2";
    private static final String PUBLIC_ITEM_ID_THIRD = "some public item id 3";

    private static final String MESSAGE_NOT_FOUND = "No value present";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WishlistRepository wishlistRepository;

    static Stream<Arguments> incorrectParamDataProvider() {
        WishlistRequestMock wishlistRequest = new WishlistRequestMock(
                TITLE_FIRST,
                DESCRIPTION_FIRST,
                List.of(
                        new ItemIdsRequestMock(PRIVATE_ITEM_ID_FIRST, PUBLIC_ITEM_ID_FIRST),
                        new ItemIdsRequestMock(PRIVATE_ITEM_ID_SECOND, PUBLIC_ITEM_ID_SECOND)
                )
        );

        return Stream.of(
                Arguments.of("Empty wishlist title", wishlistRequest.withTitle(null), "title: must not be null"),
                Arguments.of("Short wishlist title", wishlistRequest.withTitle("_".repeat(3)), "title: size must be between 4 and 255"),
                Arguments.of("Long wishlist title", wishlistRequest.withTitle("_".repeat(256)), "title: size must be between 4 and 255"),
                Arguments.of("Empty wishlist description", wishlistRequest.withDescription(null), "description: must not be null"),
                Arguments.of("Long wishlist description", wishlistRequest.withDescription("_".repeat(4096)), "description: size must be between 0 and 4095"),
                Arguments.of("Empty item list", wishlistRequest.withItemIds(null), "itemIds: must not be empty"),
                Arguments.of("Zero length item list", wishlistRequest.withItemIds(List.of()), "itemIds: must not be empty"),
                Arguments.of("No public id in item list", wishlistRequest.withItemIds(List.of(new ItemIdsRequestMock(PRIVATE_ITEM_ID_FIRST, null))), "itemIds[0].publicId: must not be blank"),
                Arguments.of("No private id in item list", wishlistRequest.withItemIds(List.of(new ItemIdsRequestMock(null, PUBLIC_ITEM_ID_FIRST))), "itemIds[0].privateId: must not be blank"),
                Arguments.of("Empty request", null, "Not readable request body")
        );
    }

    @Test
    @DisplayName("Create - successful")
    @DirtiesContext
    void create_successful() throws Exception {
        WishlistRequestMock wishlistRequest = new WishlistRequestMock(
                TITLE_FIRST,
                DESCRIPTION_FIRST,
                List.of(
                        new ItemIdsRequestMock(PRIVATE_ITEM_ID_FIRST, PUBLIC_ITEM_ID_FIRST),
                        new ItemIdsRequestMock(PRIVATE_ITEM_ID_SECOND, PUBLIC_ITEM_ID_SECOND)
                )
        );

        MvcResult mvcResult = mockMvc.perform(
                        post(URL_BASE)
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(wishlistRequest))
                ).andExpect(
                        MockMvcResultMatchers.status().isOk()
                )
                .andReturn();

        IdResponse response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), IdResponse.class);
        assertWishlistRequestSaved(wishlistRequest, response.privateId());
    }

    private void assertWishlistRequestSaved(WishlistRequestMock expected, String id) {
        Optional<Wishlist> optional = wishlistRepository.findById(id);
        assertTrue(optional.isPresent());
        Wishlist actual = optional.get();
        assertEquals(expected.title(), actual.getTitle());
        assertEquals(expected.description(), actual.getDescription());
        assertRequestItemEquality(expected.itemIds(), actual.getItemIds());
    }

    private void assertRequestItemEquality(List<ItemIdsRequestMock> expected, List<ItemIdStorage> actual) {
        assertEquals(expected.size(), actual.size());
        assertTrue(
                actual.stream().map(ItemIdStorage::getPrivateId).toList().containsAll(
                        expected.stream().map(ItemIdsRequestMock::privateId).toList()
                )
        );
        assertTrue(
                actual.stream().map(ItemIdStorage::getPublicId).toList().containsAll(
                        expected.stream().map(ItemIdsRequestMock::publicId).toList()
                )
        );
    }

    @ParameterizedTest(name = "{0}")
    @DirtiesContext
    @DisplayName("Create - incorrect payload")
    @MethodSource("incorrectParamDataProvider")
    void create_incorrectParam(String name, WishlistRequestMock wishlistRequest, String expectedMessage) throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                post(URL_BASE)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wishlistRequest))
        ).andExpect(
                MockMvcResultMatchers.status().is4xxClientError()
        ).andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(response, ErrorResponse.class);
        assertEquals(expectedMessage, errorResponse.message());
        List<Wishlist> wishlistList = wishlistRepository.findAll();
        assertTrue(wishlistList.isEmpty());
    }

    @Test
    @DirtiesContext
    @DisplayName("Get by id - successful")
    void getById_successful() throws Exception {
        Wishlist wishlistFirst = Wishlist.builder()
                .id(ID_FIRST)
                .publicId(PUBLIC_ID_FIRST)
                .title(TITLE_FIRST)
                .description(DESCRIPTION_FIRST)
                .itemIds(
                        List.of(
                                ItemIdStorage.builder().privateId(PRIVATE_ITEM_ID_FIRST).publicId(PUBLIC_ITEM_ID_FIRST).build(),
                                ItemIdStorage.builder().privateId(PRIVATE_ITEM_ID_SECOND).publicId(PUBLIC_ITEM_ID_SECOND).build()
                        )
                )
                .build();
        Wishlist wishlistSecond = Wishlist.builder()
                .id(ID_SECOND)
                .publicId(PUBLIC_ID_SECOND)
                .title(TITLE_SECOND)
                .description(DESCRIPTION_SECOND)
                .itemIds(
                        List.of(
                                ItemIdStorage.builder().privateId(PRIVATE_ITEM_ID_THIRD).publicId(PUBLIC_ITEM_ID_THIRD).build()
                        )
                )
                .build();
        wishlistRepository.saveAll(
                List.of(
                        wishlistFirst,
                        wishlistSecond
                )
        );

        MvcResult mvcResult = mockMvc.perform(
                        get(URL_WITH_ID, wishlistFirst.getId())
                ).andExpect(
                        MockMvcResultMatchers.status().isOk()
                )
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        PrivateWishlistResponse wishlistResponse = objectMapper.readValue(response, PrivateWishlistResponse.class);
        assertPrivateWishlistResponseInTable(wishlistResponse, wishlistFirst.getId());
    }

    private void assertPrivateWishlistResponseInTable(PrivateWishlistResponse actual, String id) {
        Optional<Wishlist> optional = wishlistRepository.findById(id);
        assertTrue(optional.isPresent());
        Wishlist expected = optional.get();
        assertEquals(expected.getId(), actual.privateId());
        assertEquals(expected.getPublicId(), actual.publicId());
        assertEquals(expected.getTitle(), actual.title());
        assertEquals(expected.getDescription(), actual.description());
        assertPrivateResponseItemEquality(expected.getItemIds(), actual.itemIds());
    }

    private void assertPrivateResponseItemEquality(List<ItemIdStorage> expected, List<PrivateItemIdsResponse> actual) {
        assertEquals(expected.size(), actual.size());
        assertTrue(
                actual.stream().map(PrivateItemIdsResponse::privateId).toList().containsAll(
                        expected.stream().map(ItemIdStorage::getPrivateId).toList()
                )
        );
        assertTrue(
                actual.stream().map(PrivateItemIdsResponse::publicId).toList().containsAll(
                        expected.stream().map(ItemIdStorage::getPublicId).toList()
                )
        );
    }

    @Test
    @DirtiesContext
    @DisplayName("Get by id - not found")
    void getById_notFound() throws Exception {
        Wishlist wishlistFirst = Wishlist.builder()
                .id(ID_FIRST)
                .publicId(PUBLIC_ID_FIRST)
                .title(TITLE_FIRST)
                .description(DESCRIPTION_FIRST)
                .itemIds(
                        List.of(
                                ItemIdStorage.builder().privateId(PRIVATE_ITEM_ID_FIRST).publicId(PUBLIC_ITEM_ID_FIRST).build(),
                                ItemIdStorage.builder().privateId(PRIVATE_ITEM_ID_THIRD).publicId(PUBLIC_ITEM_ID_THIRD).build()
                        )
                )
                .build();
        wishlistRepository.saveAll(
                List.of(
                        wishlistFirst
                )
        );

        MvcResult mvcResult = mockMvc.perform(
                        get(URL_WITH_ID, ID_SECOND)
                ).andExpect(
                        MockMvcResultMatchers.status().is4xxClientError()
                )
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(response, ErrorResponse.class);
        assertEquals(MESSAGE_NOT_FOUND, errorResponse.message());
    }

    @Test
    @DirtiesContext
    @DisplayName("Get by public id - successful")
    void getByPublicId_successful() throws Exception {
        Wishlist wishlistFirst = Wishlist.builder()
                .id(ID_FIRST)
                .publicId(PUBLIC_ID_FIRST)
                .title(TITLE_FIRST)
                .description(DESCRIPTION_FIRST)
                .itemIds(
                        List.of(
                                ItemIdStorage.builder().privateId(PRIVATE_ITEM_ID_FIRST).publicId(PUBLIC_ITEM_ID_FIRST).build(),
                                ItemIdStorage.builder().privateId(PRIVATE_ITEM_ID_THIRD).publicId(PUBLIC_ITEM_ID_THIRD).build()
                        )
                )
                .build();
        Wishlist wishlistSecond = Wishlist.builder()
                .id(ID_SECOND)
                .publicId(PUBLIC_ID_SECOND)
                .title(TITLE_SECOND)
                .itemIds(
                        List.of(
                                ItemIdStorage.builder().privateId(PRIVATE_ITEM_ID_SECOND).publicId(PUBLIC_ITEM_ID_SECOND).build()
                        )
                )
                .build();
        wishlistRepository.saveAll(
                List.of(
                        wishlistFirst,
                        wishlistSecond
                )
        );

        MvcResult mvcResult = mockMvc.perform(
                get(URL_PUBLIC_WITH_ID, wishlistFirst.getPublicId())
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertFalse(response.contains(wishlistFirst.getId()));
        assertFalse(response.contains(wishlistFirst.getPublicId()));
        PublicWishlistResponse wishlistResponse = objectMapper.readValue(response, PublicWishlistResponse.class);
        assertPublicWishlistResponseInTable(wishlistResponse, wishlistFirst.getId());
    }

    private void assertPublicWishlistResponseInTable(PublicWishlistResponse actual, String id) {
        Optional<Wishlist> optional = wishlistRepository.findById(id);
        assertTrue(optional.isPresent());
        Wishlist expected = optional.get();
        assertEquals(expected.getTitle(), actual.title());
        assertEquals(expected.getDescription(), actual.description());
        assertPublicResponseItemEquality(expected.getItemIds(), actual.itemIds());
    }

    private void assertPublicResponseItemEquality(List<ItemIdStorage> expected, List<PublicItemIdsResponse> actual) {
        assertEquals(expected.size(), actual.size());
        expected.forEach(
                (ItemIdStorage itemIdStorage) -> assertFalse(
                        actual.stream().map(PublicItemIdsResponse::privateId).toList().contains(itemIdStorage.getPrivateId())
                )
        );
        assertTrue(
                actual.stream().map(PublicItemIdsResponse::publicId).toList().containsAll(
                        expected.stream().map(ItemIdStorage::getPublicId).toList()
                )
        );
    }

    @Test
    @DirtiesContext
    @DisplayName("Get by public id - not found")
    void getByPublicId_notFound() throws Exception {
        Wishlist wishlistFirst = Wishlist.builder()
                .id(ID_FIRST)
                .publicId(PUBLIC_ID_FIRST)
                .title(TITLE_FIRST)
                .description(DESCRIPTION_FIRST)
                .itemIds(
                        List.of(
                                ItemIdStorage.builder().privateId(PRIVATE_ITEM_ID_FIRST).publicId(PUBLIC_ITEM_ID_FIRST).build(),
                                ItemIdStorage.builder().privateId(PRIVATE_ITEM_ID_THIRD).publicId(PUBLIC_ITEM_ID_THIRD).build()
                        )
                )
                .build();
        wishlistRepository.saveAll(
                List.of(
                        wishlistFirst
                )
        );

        MvcResult mvcResult = mockMvc.perform(
                        get(URL_PUBLIC_WITH_ID, PUBLIC_ID_SECOND)
                ).andExpect(
                        MockMvcResultMatchers.status().is4xxClientError()
                )
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(response, ErrorResponse.class);
        assertEquals(MESSAGE_NOT_FOUND, errorResponse.message());
    }

    @Test
    @DirtiesContext
    @DisplayName("Delete by id - successful")
    void deleteById_successful() throws Exception {
        Wishlist wishlistFirst = Wishlist.builder()
                .id(ID_FIRST)
                .publicId(PUBLIC_ID_FIRST)
                .title(TITLE_FIRST)
                .description(DESCRIPTION_FIRST)
                .itemIds(
                        List.of(
                                ItemIdStorage.builder().privateId(PRIVATE_ITEM_ID_FIRST).publicId(PUBLIC_ITEM_ID_FIRST).build(),
                                ItemIdStorage.builder().privateId(PRIVATE_ITEM_ID_THIRD).publicId(PUBLIC_ITEM_ID_THIRD).build()
                        )
                )
                .build();
        Wishlist wishlistSecond = Wishlist.builder()
                .id(ID_SECOND)
                .publicId(PUBLIC_ID_SECOND)
                .title(TITLE_SECOND)
                .description(DESCRIPTION_SECOND)
                .itemIds(
                        List.of(
                                ItemIdStorage.builder().privateId(PRIVATE_ITEM_ID_SECOND).publicId(PUBLIC_ITEM_ID_SECOND).build()
                        )
                )
                .build();
        wishlistRepository.saveAll(
                List.of(
                        wishlistFirst,
                        wishlistSecond
                )
        );

        mockMvc.perform(
                delete(URL_WITH_ID, wishlistFirst.getId())
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );

        assertWishlistTableSize(1);
        assertWishlistNotInTable(wishlistFirst);
        assertWishlistInTable(wishlistSecond);
    }

    private void assertWishlistTableSize(int expectedSize) {
        List<Wishlist> wishlists = wishlistRepository.findAll();
        assertEquals(expectedSize, wishlists.size());
    }

    private void assertWishlistNotInTable(Wishlist wishlist) {
        Optional<Wishlist> optional = wishlistRepository.findById(wishlist.getId());
        assertTrue(optional.isEmpty());
    }

    private void assertWishlistInTable(Wishlist expected) {
        Optional<Wishlist> optional = wishlistRepository.findById(expected.getId());
        assertTrue(optional.isPresent());
        Wishlist actual = optional.get();
        assertEquals(expected.getPublicId(), actual.getPublicId());
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertWishlistItemEquality(expected.getItemIds(), actual.getItemIds());
    }

    private void assertWishlistItemEquality(List<ItemIdStorage> expected, List<ItemIdStorage> actual) {
        assertEquals(expected.size(), actual.size());
        assertTrue(
                actual.stream().map(ItemIdStorage::getPrivateId).toList().containsAll(
                        expected.stream().map(ItemIdStorage::getPrivateId).toList()
                )
        );
        assertTrue(
                actual.stream().map(ItemIdStorage::getPublicId).toList().containsAll(
                        expected.stream().map(ItemIdStorage::getPublicId).toList()
                )
        );
    }

    @Test
    @DirtiesContext
    @DisplayName("Delete by id - not found")
    void deleteById_notFound() throws Exception {
        Wishlist wishlistFirst = Wishlist.builder()
                .id(ID_FIRST)
                .publicId(PUBLIC_ID_FIRST)
                .title(TITLE_FIRST)
                .description(DESCRIPTION_FIRST).itemIds(
                        List.of(
                                ItemIdStorage.builder().privateId(PRIVATE_ITEM_ID_FIRST).publicId(PUBLIC_ITEM_ID_FIRST).build(),
                                ItemIdStorage.builder().privateId(PRIVATE_ITEM_ID_THIRD).publicId(PUBLIC_ITEM_ID_THIRD).build()
                        )
                )
                .build();
        wishlistRepository.saveAll(
                List.of(
                        wishlistFirst
                )
        );

        mockMvc.perform(
                delete(URL_WITH_ID, ID_SECOND)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );

        assertWishlistTableSize(1);
        assertWishlistInTable(wishlistFirst);
    }

    @Test
    @DirtiesContext
    @DisplayName("Update by id - successful")
    void updateById_successful() throws Exception {
        Wishlist wishlistFirst = Wishlist.builder()
                .id(ID_FIRST)
                .publicId(PUBLIC_ID_FIRST)
                .title(TITLE_FIRST)
                .description(DESCRIPTION_FIRST).itemIds(
                        List.of(
                                ItemIdStorage.builder().privateId(PRIVATE_ITEM_ID_FIRST).publicId(PUBLIC_ITEM_ID_FIRST).build(),
                                ItemIdStorage.builder().privateId(PRIVATE_ITEM_ID_THIRD).publicId(PUBLIC_ITEM_ID_THIRD).build()
                        )
                )
                .build();
        Wishlist wishlistSecond = Wishlist.builder()
                .id(ID_SECOND)
                .publicId(PUBLIC_ID_SECOND)
                .title(TITLE_SECOND)
                .description(DESCRIPTION_SECOND).itemIds(
                        List.of(
                                ItemIdStorage.builder().privateId(PRIVATE_ITEM_ID_SECOND).publicId(PUBLIC_ITEM_ID_SECOND).build()
                        )
                )
                .build();
        wishlistRepository.saveAll(
                List.of(
                        wishlistFirst,
                        wishlistSecond
                )
        );
        WishlistRequestMock wishlistRequest = new WishlistRequestMock(
                TITLE_THIRD,
                DESCRIPTION_THIRD,
                List.of(
                        new ItemIdsRequestMock(PRIVATE_ITEM_ID_THIRD, PUBLIC_ITEM_ID_THIRD)
                )
        );
        String editId = wishlistSecond.getId();

        MvcResult mvcResult = mockMvc.perform(
                        put(URL_WITH_ID, editId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(wishlistRequest))
                ).andExpect(
                        MockMvcResultMatchers.status().isOk()
                )
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        IdResponse idResponse = objectMapper.readValue(response, IdResponse.class);
        assertEquals(editId, idResponse.privateId());
        assertWishlistTableSize(2);
        assertWishlistRequestSaved(wishlistRequest, editId);
        assertWishlistInTable(wishlistFirst);
    }

    @Test
    @DirtiesContext
    @DisplayName("Update by id - not found")
    void updateById_notFound() throws Exception {
        Wishlist wishlistFirst = Wishlist.builder()
                .id(ID_FIRST)
                .publicId(PUBLIC_ID_FIRST)
                .title(TITLE_FIRST)
                .description(DESCRIPTION_FIRST).itemIds(
                        List.of(
                                ItemIdStorage.builder().privateId(PRIVATE_ITEM_ID_FIRST).publicId(PUBLIC_ITEM_ID_FIRST).build(),
                                ItemIdStorage.builder().privateId(PRIVATE_ITEM_ID_THIRD).publicId(PUBLIC_ITEM_ID_THIRD).build()
                        )
                )
                .build();
        wishlistRepository.saveAll(
                List.of(
                        wishlistFirst
                )
        );
        WishlistRequestMock wishlistRequest = new WishlistRequestMock(
                TITLE_THIRD,
                DESCRIPTION_THIRD,
                List.of(
                        new ItemIdsRequestMock(PRIVATE_ITEM_ID_THIRD, PUBLIC_ITEM_ID_THIRD)
                )
        );

        MvcResult mvcResult = mockMvc.perform(
                        put(URL_WITH_ID, ID_SECOND)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(wishlistRequest))
                ).andExpect(
                        MockMvcResultMatchers.status().is4xxClientError()
                )
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(response, ErrorResponse.class);
        assertEquals(MESSAGE_NOT_FOUND, errorResponse.message());
        assertWishlistTableSize(1);
        assertWishlistInTable(wishlistFirst);
    }
}
