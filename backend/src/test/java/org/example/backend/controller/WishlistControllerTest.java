package org.example.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.backend.dto.IdResponse;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
class WishlistControllerTest {

    private static final String URL_BASE = "/api/wishlist";

    private static final String WISHLIST_TITLE = "some wishlist title";
    private static final String WISHLIST_DESCRIPTION = "some wishlist description";
    private static final String ITEM_ID_FIRST = "some item id 1";
    private static final String ITEM_ID_SECOND = "some item id 2";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WishlistRepository wishlistRepository;

    static Stream<Arguments> nullParamDataProvider() {
        WishlistRequestMock wishlistRequest = new WishlistRequestMock(
                WISHLIST_TITLE,
                WISHLIST_DESCRIPTION,
                List.of(ITEM_ID_FIRST, ITEM_ID_SECOND)
        );

        return Stream.of(
                Arguments.of("Empty wishlist title", wishlistRequest.withTitle(null)),
                Arguments.of("Empty wishlist description", wishlistRequest.withDescription(null)),
                Arguments.of("Empty item list", wishlistRequest.withItemIds(null))
        );
    }

    @Test
    @DisplayName("Correct payload")
    @DirtiesContext
    void create_successful() throws Exception {
        WishlistRequestMock wishlistRequest = new WishlistRequestMock(
                WISHLIST_TITLE,
                WISHLIST_DESCRIPTION,
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

        Optional<Wishlist> result = wishlistRepository.findById(response.id());
        assertTrue(result.isPresent());
        Wishlist wishlist = result.get();
        assertEquals(wishlistRequest.title(), wishlist.getTitle());
        assertEquals(wishlistRequest.description(), wishlist.getDescription());
        assertEquals(wishlistRequest.itemIds(), wishlist.getItemIds());
    }

    @ParameterizedTest(name = "{0}")
    @DirtiesContext
    @DisplayName("Incorrect payload")
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
}
