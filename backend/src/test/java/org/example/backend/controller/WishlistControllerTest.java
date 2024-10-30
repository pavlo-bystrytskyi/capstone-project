package org.example.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.backend.dto.ErrorResponse;
import org.example.backend.dto.IdResponse;
import org.example.backend.dto.wishlist.WishlistResponse;
import org.example.backend.mock.dto.WishlistRequestMock;
import org.example.backend.model.Wishlist;
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
class WishlistControllerTest {

    private static final String URL_BASE = "/api/wishlist";
    private static final String URL_WITH_ID = "/api/wishlist/{id}";
    private static final String URL_PUBLIC_WITH_ID = "/api/wishlist/public/{id}";

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
    private static final String ITEM_ID_FIRST = "some item id 1";
    private static final String ITEM_ID_SECOND = "some item id 2";
    private static final String ITEM_ID_THIRD = "some item id 3";

    private static final String MESSAGE_NOT_FOUND = "No value present";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WishlistRepository wishlistRepository;

    static Stream<Arguments> nullParamDataProvider() {
        WishlistRequestMock wishlistRequest = new WishlistRequestMock(
                TITLE_FIRST,
                DESCRIPTION_FIRST,
                List.of(ITEM_ID_FIRST, ITEM_ID_SECOND)
        );

        return Stream.of(
                Arguments.of("Empty wishlist title", wishlistRequest.withTitle(null)),
                Arguments.of("Empty wishlist description", wishlistRequest.withDescription(null)),
                Arguments.of("Empty item list", wishlistRequest.withItemIds(null)),
                Arguments.of("Empty request", null)
        );
    }

    @Test
    @DisplayName("Create - successful")
    @DirtiesContext
    void create_successful() throws Exception {
        WishlistRequestMock wishlistRequest = new WishlistRequestMock(
                TITLE_FIRST,
                DESCRIPTION_FIRST,
                List.of(ITEM_ID_FIRST, ITEM_ID_SECOND)
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
        assertWishlistRequestSaved(wishlistRequest, response.id());
    }

    private void assertWishlistRequestSaved(WishlistRequestMock expected, String id) {
        Optional<Wishlist> optional = wishlistRepository.findById(id);
        assertTrue(optional.isPresent());
        Wishlist actual = optional.get();
        assertEquals(expected.title(), actual.getTitle());
        assertEquals(expected.description(), actual.getDescription());
        assertEquals(expected.itemIds(), actual.getItemIds());
    }

    @ParameterizedTest(name = "{0}")
    @DirtiesContext
    @DisplayName("Create - incorrect payload")
    @MethodSource("nullParamDataProvider")
    void create_nullParam(String name, WishlistRequestMock wishlistRequest) throws Exception {
        mockMvc.perform(
                post(URL_BASE)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wishlistRequest))
        ).andExpect(
                MockMvcResultMatchers.status().is4xxClientError()
        );

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
                .itemIds(List.of(ITEM_ID_FIRST, ITEM_ID_THIRD))
                .build();
        Wishlist wishlistSecond = Wishlist.builder()
                .id(ID_SECOND)
                .publicId(PUBLIC_ID_SECOND)
                .title(TITLE_SECOND)
                .description(DESCRIPTION_SECOND)
                .itemIds(List.of(ITEM_ID_SECOND))
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
        assertFalse(response.contains(wishlistFirst.getId()));
        assertFalse(response.contains(wishlistFirst.getPublicId()));
        WishlistResponse wishlistResponse = objectMapper.readValue(response, WishlistResponse.class);
        assertWishlistResponseInTable(wishlistResponse, wishlistFirst.getId());
    }

    private void assertWishlistResponseInTable(WishlistResponse actual, String id) {
        Optional<Wishlist> optional = wishlistRepository.findById(id);
        assertTrue(optional.isPresent());
        Wishlist expected = optional.get();
        assertEquals(expected.getTitle(), actual.title());
        assertEquals(expected.getDescription(), actual.description());
        assertEquals(expected.getItemIds(), actual.itemIds());
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
                .itemIds(List.of(ITEM_ID_FIRST, ITEM_ID_THIRD))
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
                .itemIds(List.of(ITEM_ID_FIRST, ITEM_ID_THIRD))
                .build();
        Wishlist wishlistSecond = Wishlist.builder()
                .id(ID_SECOND)
                .publicId(PUBLIC_ID_SECOND)
                .title(TITLE_SECOND)
                .description(DESCRIPTION_SECOND)
                .itemIds(List.of(ITEM_ID_SECOND))
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
                )
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertFalse(response.contains(wishlistFirst.getId()));
        assertFalse(response.contains(wishlistFirst.getPublicId()));
        WishlistResponse wishlistResponse = objectMapper.readValue(response, WishlistResponse.class);
        assertWishlistResponseInTable(wishlistResponse, wishlistFirst.getId());
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
                .itemIds(List.of(ITEM_ID_FIRST, ITEM_ID_THIRD))
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
                .itemIds(List.of(ITEM_ID_FIRST, ITEM_ID_THIRD))
                .build();
        Wishlist wishlistSecond = Wishlist.builder()
                .id(ID_SECOND)
                .publicId(PUBLIC_ID_SECOND)
                .title(TITLE_SECOND)
                .description(DESCRIPTION_SECOND)
                .itemIds(List.of(ITEM_ID_SECOND))
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
        assertEquals(expected.getItemIds(), actual.getItemIds());
    }

    @Test
    @DirtiesContext
    @DisplayName("Delete by id - not found")
    void deleteById_notFound() throws Exception {
        Wishlist wishlistFirst = Wishlist.builder()
                .id(ID_FIRST)
                .publicId(PUBLIC_ID_FIRST)
                .title(TITLE_FIRST)
                .description(DESCRIPTION_FIRST)
                .itemIds(List.of(ITEM_ID_FIRST, ITEM_ID_THIRD))
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
                .description(DESCRIPTION_FIRST)
                .itemIds(List.of(ITEM_ID_FIRST, ITEM_ID_THIRD))
                .build();
        Wishlist wishlistSecond = Wishlist.builder()
                .id(ID_SECOND)
                .publicId(PUBLIC_ID_SECOND)
                .title(TITLE_SECOND)
                .description(DESCRIPTION_SECOND)
                .itemIds(List.of(ITEM_ID_SECOND))
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
                List.of(ITEM_ID_THIRD)
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
        WishlistResponse wishlistResponse = objectMapper.readValue(response, WishlistResponse.class);

        assetWishlistRequestReturned(wishlistRequest, wishlistResponse);
        assertWishlistTableSize(2);
        assertWishlistRequestSaved(wishlistRequest, editId);
        assertWishlistInTable(wishlistFirst);
    }

    private void assetWishlistRequestReturned(WishlistRequestMock expected, WishlistResponse actual) {
        assertEquals(expected.title(), actual.title());
        assertEquals(expected.description(), actual.description());
        assertEquals(expected.itemIds(), actual.itemIds());
    }

    @Test
    @DirtiesContext
    @DisplayName("Update by id - not found")
    void updateById_notFound() throws Exception {
        Wishlist wishlistFirst = Wishlist.builder()
                .id(ID_FIRST)
                .publicId(PUBLIC_ID_FIRST)
                .title(TITLE_FIRST)
                .description(DESCRIPTION_FIRST)
                .itemIds(List.of(ITEM_ID_FIRST, ITEM_ID_THIRD))
                .build();
        wishlistRepository.saveAll(
                List.of(
                        wishlistFirst
                )
        );
        WishlistRequestMock wishlistRequest = new WishlistRequestMock(
                TITLE_THIRD,
                DESCRIPTION_THIRD,
                List.of(ITEM_ID_THIRD)
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
