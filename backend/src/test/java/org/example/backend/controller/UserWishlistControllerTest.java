package org.example.backend.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.backend.dto.response.ErrorResponse;
import org.example.backend.dto.response.IdResponse;
import org.example.backend.dto.response.wishlist.PrivateItemIdsResponse;
import org.example.backend.dto.response.wishlist.PrivateWishlistResponse;
import org.example.backend.mock.dto.ItemIdsRequestMock;
import org.example.backend.mock.dto.WishlistRequestMock;
import org.example.backend.model.User;
import org.example.backend.model.Wishlist;
import org.example.backend.model.wishlist.ItemIdStorage;
import org.example.backend.repository.UserRepository;
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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserWishlistControllerTest {

    private static final String URL_BASE = "/api/user/wishlist";
    private static final String URL_WITH_ID = "/api/user/wishlist/{id}";

    private static final String ID_FIRST = "some id 1";
    private static final String ID_SECOND = "some id 2";
    private static final String ID_THIRD = "some id 3";
    private static final String PUBLIC_ID_FIRST = "some public id 1";
    private static final String PUBLIC_ID_SECOND = "some public id 2";
    private static final String PUBLIC_ID_THIRD = "some public id 3";
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
    private static final String EXTERNAL_ID_USER_FIRST = "some user external id 1";
    private static final String EXTERNAL_ID_USER_SECOND = "some user external id 2";

    private static final String MESSAGE_NOT_FOUND = "No value present";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WishlistRepository wishlistRepository;
    @Autowired
    private UserRepository userRepository;

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
        String userId = createUser(EXTERNAL_ID_USER_FIRST);
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
                                .with(mockUser())
                ).andExpect(
                        MockMvcResultMatchers.status().isOk()
                )
                .andReturn();

        IdResponse response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), IdResponse.class);
        assertWishlistRequestSaved(wishlistRequest, response.privateId());
        assertOwnerIdSet(userId, response.privateId());
    }

    private String createUser(String externalId) {
        User user = User.builder().externalId(externalId).build();

        return userRepository.save(user).getId();
    }

    private SecurityMockMvcRequestPostProcessors.OidcLoginRequestPostProcessor mockUser() {
        return oidcLogin()
                .idToken(idToken -> idToken.claim("sub", EXTERNAL_ID_USER_FIRST));
    }

    private void assertWishlistRequestSaved(WishlistRequestMock expected, String id) {
        Optional<Wishlist> optional = wishlistRepository.findById(id);
        assertTrue(optional.isPresent());
        Wishlist actual = optional.get();
        assertEquals(expected.title(), actual.getTitle());
        assertEquals(expected.description(), actual.getDescription());
        assertRequestItemEquality(expected.itemIds(), actual.getItemIds());
    }

    private void assertOwnerIdSet(String userId, String wishlistPrivateId) {
        Optional<Wishlist> optional = wishlistRepository.findById(wishlistPrivateId);
        assertTrue(optional.isPresent());
        assertEquals(userId, optional.get().getOwnerId());
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

    @Test
    @DisplayName("Create - user does not exist")
    @DirtiesContext
    void create_noSuchUser() throws Exception {
        createUser(EXTERNAL_ID_USER_SECOND);
        WishlistRequestMock wishlistRequest = new WishlistRequestMock(
                TITLE_FIRST,
                DESCRIPTION_FIRST,
                List.of(
                        new ItemIdsRequestMock(PRIVATE_ITEM_ID_FIRST, PUBLIC_ITEM_ID_FIRST),
                        new ItemIdsRequestMock(PRIVATE_ITEM_ID_SECOND, PUBLIC_ITEM_ID_SECOND)
                )
        );

        mockMvc.perform(
                post(URL_BASE)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wishlistRequest))
                        .with(mockUser())
        ).andExpect(
                MockMvcResultMatchers.status().is4xxClientError()
        );

        List<Wishlist> wishlistList = wishlistRepository.findAll();
        assertTrue(wishlistList.isEmpty());
    }

    @ParameterizedTest(name = "{0}")
    @DirtiesContext
    @DisplayName("Create - incorrect payload")
    @MethodSource("incorrectParamDataProvider")
    void create_incorrectParam(String name, WishlistRequestMock wishlistRequest, String expectedMessage) throws Exception {
        createUser(EXTERNAL_ID_USER_FIRST);
        MvcResult mvcResult = mockMvc.perform(
                post(URL_BASE)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wishlistRequest))
                        .with(mockUser())
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
        String userId = createUser(EXTERNAL_ID_USER_FIRST);
        Wishlist wishlistFirst = Wishlist.builder()
                .id(ID_FIRST)
                .publicId(PUBLIC_ID_FIRST)
                .ownerId(userId)
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
                                .with(mockUser())
                ).andExpect(
                        MockMvcResultMatchers.status().isOk()
                )
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertFalse(response.contains(wishlistFirst.getId()));
        assertFalse(response.contains(wishlistFirst.getPublicId()));
        PrivateWishlistResponse wishlistResponse = objectMapper.readValue(response, PrivateWishlistResponse.class);
        assertPrivateWishlistResponseInTable(wishlistResponse, wishlistFirst.getId());
    }

    private void assertPrivateWishlistResponseInTable(PrivateWishlistResponse actual, String id) {
        Optional<Wishlist> optional = wishlistRepository.findById(id);
        assertTrue(optional.isPresent());
        Wishlist expected = optional.get();
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
    @DisplayName("Get by id - incorrect user")
    void getById_incorrectUser() throws Exception {
        String userId = createUser(EXTERNAL_ID_USER_SECOND);
        Wishlist wishlistFirst = Wishlist.builder()
                .id(ID_FIRST)
                .publicId(PUBLIC_ID_FIRST)
                .ownerId(userId)
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

        mockMvc.perform(
                get(URL_WITH_ID, wishlistFirst.getId())
                        .with(mockUser())
        ).andExpect(
                MockMvcResultMatchers.status().is4xxClientError()
        );
    }

    @Test
    @DirtiesContext
    @DisplayName("Get by id - not found")
    void getById_notFound() throws Exception {
        String userId = createUser(EXTERNAL_ID_USER_FIRST);
        Wishlist wishlistFirst = Wishlist.builder()
                .id(ID_FIRST)
                .publicId(PUBLIC_ID_FIRST)
                .ownerId(userId)
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
                                .with(mockUser())
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
        String userId = createUser(EXTERNAL_ID_USER_FIRST);
        Wishlist wishlistFirst = Wishlist.builder()
                .id(ID_FIRST)
                .publicId(PUBLIC_ID_FIRST)
                .ownerId(userId)
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
                        .with(mockUser())
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
        assertEquals(expected.getOwnerId(), actual.getOwnerId());
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
    @DisplayName("Delete by id - incorrect user")
    void deleteById_incorrectUser() throws Exception {
        String userId = createUser(EXTERNAL_ID_USER_SECOND);
        Wishlist wishlist = Wishlist.builder()
                .id(ID_FIRST)
                .publicId(PUBLIC_ID_FIRST)
                .ownerId(userId)
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
                        wishlist
                )
        );

        mockMvc.perform(
                delete(URL_WITH_ID, wishlist.getId())
                        .with(mockUser())
        ).andExpect(
                MockMvcResultMatchers.status().is4xxClientError()
        );

        assertWishlistTableSize(1);
        assertWishlistInTable(wishlist);
    }

    @Test
    @DirtiesContext
    @DisplayName("Delete by id - not found")
    void deleteById_notFound() throws Exception {
        String userId = createUser(EXTERNAL_ID_USER_FIRST);
        Wishlist wishlistFirst = Wishlist.builder()
                .id(ID_FIRST)
                .publicId(PUBLIC_ID_FIRST)
                .ownerId(userId)
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
                        .with(mockUser())
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
        String userId = createUser(EXTERNAL_ID_USER_FIRST);
        Wishlist wishlistFirst = Wishlist.builder()
                .id(ID_FIRST)
                .publicId(PUBLIC_ID_FIRST)
                .ownerId(userId)
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
                .ownerId(userId)
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
                                .with(mockUser())
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
    @DisplayName("Update by id - incorrect user")
    void updateById_incorrectUser() throws Exception {
        String userId = createUser(EXTERNAL_ID_USER_SECOND);
        Wishlist wishlist = Wishlist.builder()
                .id(ID_FIRST)
                .publicId(PUBLIC_ID_FIRST)
                .ownerId(userId)
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
                        wishlist
                )
        );
        WishlistRequestMock wishlistRequest = new WishlistRequestMock(
                TITLE_THIRD,
                DESCRIPTION_THIRD,
                List.of(
                        new ItemIdsRequestMock(PRIVATE_ITEM_ID_THIRD, PUBLIC_ITEM_ID_THIRD)
                )
        );
        String editId = wishlist.getId();

        MvcResult mvcResult = mockMvc.perform(
                        put(URL_WITH_ID, editId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(wishlistRequest))
                                .with(mockUser())
                ).andExpect(
                        MockMvcResultMatchers.status().is4xxClientError()
                )
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(response, ErrorResponse.class);
        assertEquals(MESSAGE_NOT_FOUND, errorResponse.message());
        assertWishlistTableSize(1);
        assertWishlistInTable(wishlist);
    }

    @Test
    @DirtiesContext
    @DisplayName("Update by id - not found")
    void updateById_notFound() throws Exception {
        String userId = createUser(EXTERNAL_ID_USER_SECOND);
        Wishlist wishlistFirst = Wishlist.builder()
                .id(ID_FIRST)
                .publicId(PUBLIC_ID_FIRST)
                .ownerId(userId)
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
                                .with(mockUser())
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

    @Test
    @DirtiesContext
    @DisplayName("Get all - success")
    void getAll_success() throws Exception {
        String userIdFirst = createUser(EXTERNAL_ID_USER_FIRST);
        String userIdSecond = createUser(EXTERNAL_ID_USER_SECOND);
        Wishlist wishlistFirst = Wishlist.builder()
                .id(ID_FIRST)
                .publicId(PUBLIC_ID_FIRST)
                .ownerId(userIdFirst)
                .title(TITLE_FIRST)
                .description(DESCRIPTION_FIRST)
                .itemIds(
                        List.of(
                                ItemIdStorage.builder().privateId(PRIVATE_ITEM_ID_FIRST).publicId(PUBLIC_ITEM_ID_FIRST).build()
                        )
                )
                .build();
        Wishlist wishlistSecond = Wishlist.builder()
                .id(ID_SECOND)
                .publicId(PUBLIC_ID_SECOND)
                .ownerId(userIdSecond)
                .title(TITLE_SECOND)
                .description(DESCRIPTION_SECOND)
                .itemIds(
                        List.of(
                                ItemIdStorage.builder().privateId(PRIVATE_ITEM_ID_SECOND).publicId(PUBLIC_ITEM_ID_SECOND).build()
                        )
                )
                .build();
        Wishlist wishlistThird = Wishlist.builder()
                .id(ID_THIRD)
                .publicId(PUBLIC_ID_THIRD)
                .ownerId(userIdFirst)
                .title(TITLE_THIRD)
                .description(DESCRIPTION_THIRD)
                .itemIds(
                        List.of(
                                ItemIdStorage.builder().privateId(PRIVATE_ITEM_ID_THIRD).publicId(PUBLIC_ITEM_ID_THIRD).build()
                        )
                )
                .build();
        wishlistRepository.saveAll(
                List.of(
                        wishlistFirst,
                        wishlistSecond,
                        wishlistThird
                )
        );

        MvcResult mvcResult = mockMvc.perform(
                        get(URL_BASE)
                                .with(mockUser())
                ).andExpect(
                        MockMvcResultMatchers.status().isOk()
                )
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        List<PrivateWishlistResponse> wishlistResponse = objectMapper.readValue(response, new TypeReference<>() {
        });
        assertEquals(2, wishlistResponse.size());
        assertPrivateWishlistResponseInTable(wishlistResponse.get(0), wishlistFirst.getId());
        assertPrivateWishlistResponseInTable(wishlistResponse.get(1), wishlistThird.getId());
    }

    @Test
    @DirtiesContext
    @DisplayName("Get all - no wishlists found")
    void getAll_noWishlistsFound() throws Exception {
        createUser(EXTERNAL_ID_USER_FIRST);
        String userIdSecond = createUser(EXTERNAL_ID_USER_SECOND);
        Wishlist wishlistFirst = Wishlist.builder()
                .id(ID_FIRST)
                .publicId(PUBLIC_ID_FIRST)
                .ownerId(userIdSecond)
                .title(TITLE_FIRST)
                .description(DESCRIPTION_FIRST)
                .itemIds(
                        List.of(
                                ItemIdStorage.builder().privateId(PRIVATE_ITEM_ID_FIRST).publicId(PUBLIC_ITEM_ID_FIRST).build()
                        )
                )
                .build();
        Wishlist wishlistSecond = Wishlist.builder()
                .id(ID_SECOND)
                .publicId(PUBLIC_ID_SECOND)
                .ownerId(null)
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

        MvcResult mvcResult = mockMvc.perform(
                        get(URL_BASE)
                                .with(mockUser())
                ).andExpect(
                        MockMvcResultMatchers.status().isOk()
                )
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        List<PrivateWishlistResponse> wishlistResponse = objectMapper.readValue(response, new TypeReference<>() {
        });
        assertEquals(0, wishlistResponse.size());
    }
}
