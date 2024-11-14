package org.example.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.backend.dto.ErrorResponse;
import org.example.backend.dto.IdResponse;
import org.example.backend.dto.item.ProductResponse;
import org.example.backend.dto.item.PublicItemResponse;
import org.example.backend.mock.dto.ItemRequestMock;
import org.example.backend.mock.dto.ProductRequestMock;
import org.example.backend.model.Item;
import org.example.backend.model.Product;
import org.example.backend.model.User;
import org.example.backend.repository.ItemRepository;
import org.example.backend.repository.UserRepository;
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

import static org.example.backend.model.item.ItemStatus.AVAILABLE;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserItemControllerTest {

    private static final String URL_BASE = "/api/user/item";
    private static final String URL_WITH_ID = "/api/user/item/{id}";

    private static final Double ITEM_QUANTITY_FIRST = 5.5;
    private static final Double ITEM_QUANTITY_SECOND = 10.1;

    private static final String ID_FIRST = "some id 1";
    private static final String ID_SECOND = "some id 2";
    private static final String PUBLIC_ID_FIRST = "some public id 1";
    private static final String PUBLIC_ID_SECOND = "some public id 2";
    private static final String PRODUCT_TITLE_FIRST = "some product title 1";
    private static final String PRODUCT_TITLE_SECOND = "some product title 2";
    private static final String PRODUCT_DESCRIPTION_FIRST = "some product description 1";
    private static final String PRODUCT_DESCRIPTION_SECOND = "some product description 2";
    private static final String PRODUCT_LINK_FIRST = "https://example.com/some-product-link-1";
    private static final String PRODUCT_LINK_SECOND = "https://example.com/some-product-link-2";
    private static final String EXTERNAL_ID_USER_FIRST = "some user external id 1";
    private static final String EXTERNAL_ID_USER_SECOND = "some user external id 2";

    private static final String MESSAGE_NOT_FOUND = "No value present";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    static Stream<Arguments> nullParamDataProvider() {
        ProductRequestMock productRequest = new ProductRequestMock(
                PRODUCT_TITLE_FIRST,
                PRODUCT_DESCRIPTION_FIRST,
                PRODUCT_LINK_FIRST
        );
        ItemRequestMock itemRequest = new ItemRequestMock(
                ITEM_QUANTITY_FIRST,
                AVAILABLE,
                productRequest
        );

        return Stream.of(
                Arguments.of("Empty item quantity", itemRequest.withQuantity(null)),
                Arguments.of("Empty product data", itemRequest.withProduct(null)),
                Arguments.of("Empty product title", itemRequest.withProduct(productRequest.withTitle(null))),
                Arguments.of("Empty product description", itemRequest.withProduct(productRequest.withDescription(null))),
                Arguments.of("Empty product link", itemRequest.withProduct(productRequest.withLink(null))),
                Arguments.of("Empty request", null)
        );
    }

    private SecurityMockMvcRequestPostProcessors.OidcLoginRequestPostProcessor mockUser() {
        return oidcLogin()
                .idToken(idToken -> idToken.claim("sub", EXTERNAL_ID_USER_FIRST));
    }

    @Test
    @DisplayName("Create - successful")
    @DirtiesContext
    void create_successful() throws Exception {
        String userId = createUser(EXTERNAL_ID_USER_FIRST);
        ProductRequestMock productRequest = new ProductRequestMock(
                PRODUCT_TITLE_FIRST,
                PRODUCT_DESCRIPTION_FIRST,
                PRODUCT_LINK_FIRST
        );
        ItemRequestMock itemRequest = new ItemRequestMock(
                ITEM_QUANTITY_FIRST,
                AVAILABLE,
                productRequest
        );

        MvcResult mvcResult = mockMvc.perform(
                        post(URL_BASE)
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(itemRequest))
                                .with(mockUser())
                ).andExpect(
                        MockMvcResultMatchers.status().isOk()
                )
                .andReturn();

        IdResponse response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), IdResponse.class);

        Optional<Item> result = itemRepository.findById(response.privateId());
        assertTrue(result.isPresent());
        Item item = result.get();
        assertEquals(itemRequest.quantity(), item.getQuantity());
        Product product = item.getProduct();
        assertEquals(productRequest.title(), product.getTitle());
        assertEquals(productRequest.description(), product.getDescription());
        assertEquals(productRequest.link(), product.getLink());
        assertOwnerIdSet(userId, response.privateId());
    }

    private void assertOwnerIdSet(String userId, String itemPrivateId) {
        Optional<Item> optional = itemRepository.findById(itemPrivateId);
        assertTrue(optional.isPresent());
        assertEquals(userId, optional.get().getOwnerId());
    }

    @Test
    @DisplayName("Create - user does not exist")
    @DirtiesContext
    void create_noSuchUser() throws Exception {
        createUser(EXTERNAL_ID_USER_SECOND);
        ProductRequestMock productRequest = new ProductRequestMock(
                PRODUCT_TITLE_FIRST,
                PRODUCT_DESCRIPTION_FIRST,
                PRODUCT_LINK_FIRST
        );
        ItemRequestMock itemRequest = new ItemRequestMock(
                ITEM_QUANTITY_FIRST,
                AVAILABLE,
                productRequest
        );

        mockMvc.perform(
                        post(URL_BASE)
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(itemRequest))
                                .with(mockUser())
                ).andExpect(
                        MockMvcResultMatchers.status().is4xxClientError()
                );

        List<Item> itemList = itemRepository.findAll();
        assertTrue(itemList.isEmpty());
    }

    @ParameterizedTest(name = "{0}")
    @DirtiesContext
    @DisplayName("Create - incorrect payload")
    @MethodSource("nullParamDataProvider")
    void create_nullParam(String name, ItemRequestMock itemRequest) throws Exception {
        createUser(EXTERNAL_ID_USER_FIRST);
        mockMvc.perform(
                post(URL_BASE)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequest))
                        .with(mockUser())
        ).andExpect(
                MockMvcResultMatchers.status().is4xxClientError()
        );

        List<Item> itemList = itemRepository.findAll();
        assertTrue(itemList.isEmpty());
    }

    private String createUser(String externalId) {
        User user = User.builder().externalId(externalId).build();

        return userRepository.save(user).getId();
    }

    @Test
    @DirtiesContext
    @DisplayName("Get by id - successful")
    void getById_successful() throws Exception {
        String userId = createUser(EXTERNAL_ID_USER_FIRST);
        Product productFirst = Product.builder()
                .title(PRODUCT_TITLE_FIRST)
                .description(PRODUCT_DESCRIPTION_FIRST)
                .link(PRODUCT_LINK_FIRST)
                .build();
        Item itemFirst = Item.builder()
                .id(ID_FIRST)
                .publicId(PUBLIC_ID_FIRST)
                .ownerId(userId)
                .status(AVAILABLE)
                .product(productFirst)
                .quantity(ITEM_QUANTITY_FIRST)
                .build();
        Product productSecond = Product.builder()
                .title(PRODUCT_TITLE_SECOND)
                .description(PRODUCT_DESCRIPTION_SECOND)
                .link(PRODUCT_LINK_SECOND)
                .build();
        Item itemSecond = Item.builder()
                .id(ID_SECOND)
                .publicId(PUBLIC_ID_SECOND)
                .ownerId(userId)
                .status(AVAILABLE)
                .product(productSecond)
                .quantity(ITEM_QUANTITY_SECOND)
                .build();
        itemRepository.saveAll(
                List.of(
                        itemFirst,
                        itemSecond
                )
        );

        MvcResult mvcResult = mockMvc.perform(
                        get(URL_WITH_ID, itemFirst.getId())
                                .with(mockUser())
                ).andExpect(
                        MockMvcResultMatchers.status().isOk()
                )
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertFalse(response.contains(itemFirst.getId()));
        assertTrue(response.contains(itemFirst.getPublicId()));
        PublicItemResponse itemResponse = objectMapper.readValue(response, PublicItemResponse.class);
        assertEquals(itemFirst.getQuantity(), itemResponse.quantity());
        ProductResponse productResponse = itemResponse.product();
        assertEquals(productFirst.getTitle(), productResponse.title());
        assertEquals(productFirst.getDescription(), productResponse.description());
        assertEquals(productFirst.getLink(), productResponse.link());
    }

    @Test
    @DirtiesContext
    @DisplayName("Get by id - incorrect user")
    void getById_incorrectUser() throws Exception {
        String userId = createUser(EXTERNAL_ID_USER_SECOND);
        Product productFirst = Product.builder()
                .title(PRODUCT_TITLE_FIRST)
                .description(PRODUCT_DESCRIPTION_FIRST)
                .link(PRODUCT_LINK_FIRST)
                .build();
        Item itemFirst = Item.builder()
                .id(ID_FIRST)
                .publicId(PUBLIC_ID_FIRST)
                .ownerId(userId)
                .status(AVAILABLE)
                .product(productFirst)
                .quantity(ITEM_QUANTITY_FIRST)
                .build();
        Product productSecond = Product.builder()
                .title(PRODUCT_TITLE_SECOND)
                .description(PRODUCT_DESCRIPTION_SECOND)
                .link(PRODUCT_LINK_SECOND)
                .build();
        Item itemSecond = Item.builder()
                .id(ID_SECOND)
                .publicId(PUBLIC_ID_SECOND)
                .ownerId(userId)
                .status(AVAILABLE)
                .product(productSecond)
                .quantity(ITEM_QUANTITY_SECOND)
                .build();
        itemRepository.saveAll(
                List.of(
                        itemFirst,
                        itemSecond
                )
        );

        mockMvc.perform(
                        get(URL_WITH_ID, itemFirst.getId())
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
        Product productFirst = Product.builder()
                .title(PRODUCT_TITLE_FIRST)
                .description(PRODUCT_DESCRIPTION_FIRST)
                .link(PRODUCT_LINK_FIRST)
                .build();
        Item itemFirst = Item.builder()
                .id(ID_FIRST)
                .publicId(PUBLIC_ID_FIRST)
                .ownerId(userId)
                .status(AVAILABLE)
                .product(productFirst)
                .quantity(ITEM_QUANTITY_FIRST)
                .build();
        itemRepository.saveAll(
                List.of(
                        itemFirst
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
        Product productFirst = Product.builder()
                .title(PRODUCT_TITLE_FIRST)
                .description(PRODUCT_DESCRIPTION_FIRST)
                .link(PRODUCT_LINK_FIRST)
                .build();
        Item itemFirst = Item.builder()
                .id(ID_FIRST)
                .publicId(PUBLIC_ID_FIRST)
                .ownerId(userId)
                .status(AVAILABLE)
                .product(productFirst)
                .quantity(ITEM_QUANTITY_FIRST)
                .build();
        Product productSecond = Product.builder()
                .title(PRODUCT_TITLE_SECOND)
                .description(PRODUCT_DESCRIPTION_SECOND)
                .link(PRODUCT_LINK_SECOND)
                .build();
        Item itemSecond = Item.builder()
                .id(ID_SECOND)
                .publicId(PUBLIC_ID_SECOND)
                .ownerId(userId)
                .status(AVAILABLE).product(productSecond)
                .quantity(ITEM_QUANTITY_SECOND)
                .build();
        itemRepository.saveAll(
                List.of(
                        itemFirst,
                        itemSecond
                )
        );

        mockMvc.perform(
                delete(URL_WITH_ID, itemFirst.getId())
                        .with(mockUser())
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );

        List<Item> items = itemRepository.findAll();
        assertEquals(1, items.size());
        Optional<Item> item = itemRepository.findById(itemFirst.getId());
        assertTrue(item.isEmpty());
    }

    @Test
    @DirtiesContext
    @DisplayName("Delete by id - not found")
    void deleteById_notFound() throws Exception {
        String userId = createUser(EXTERNAL_ID_USER_FIRST);
        Product productFirst = Product.builder()
                .title(PRODUCT_TITLE_FIRST)
                .description(PRODUCT_DESCRIPTION_FIRST)
                .link(PRODUCT_LINK_FIRST)
                .build();
        Item itemFirst = Item.builder()
                .id(ID_FIRST)
                .publicId(PUBLIC_ID_FIRST)
                .ownerId(userId)
                .status(AVAILABLE)
                .product(productFirst)
                .quantity(ITEM_QUANTITY_FIRST)
                .build();
        itemRepository.saveAll(
                List.of(
                        itemFirst
                )
        );

        mockMvc.perform(
                delete(URL_WITH_ID, ID_SECOND)
                        .with(mockUser())
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );

        List<Item> items = itemRepository.findAll();
        assertEquals(1, items.size());
        Optional<Item> item = itemRepository.findById(itemFirst.getId());
        assertTrue(item.isPresent());
    }

    @Test
    @DirtiesContext
    @DisplayName("Delete by id - incorrect user")
    void deleteById_incorrectUser() throws Exception {
        String userId = createUser(EXTERNAL_ID_USER_SECOND);
        Product productFirst = Product.builder()
                .title(PRODUCT_TITLE_FIRST)
                .description(PRODUCT_DESCRIPTION_FIRST)
                .link(PRODUCT_LINK_FIRST)
                .build();
        Item itemFirst = Item.builder()
                .id(ID_FIRST)
                .publicId(PUBLIC_ID_FIRST)
                .ownerId(userId)
                .status(AVAILABLE)
                .product(productFirst)
                .quantity(ITEM_QUANTITY_FIRST)
                .build();
        itemRepository.saveAll(
                List.of(
                        itemFirst
                )
        );

        mockMvc.perform(
                delete(URL_WITH_ID, ID_SECOND)
                        .with(mockUser())
        ).andExpect(
                MockMvcResultMatchers.status().is4xxClientError()
        );

        List<Item> items = itemRepository.findAll();
        assertEquals(1, items.size());
        Optional<Item> item = itemRepository.findById(itemFirst.getId());
        assertTrue(item.isPresent());
    }

    @Test
    @DirtiesContext
    @DisplayName("Update by id - successful")
    void updateById_successful() throws Exception {
        String userId = createUser(EXTERNAL_ID_USER_FIRST);
        Product productFirst = Product.builder()
                .title(PRODUCT_TITLE_FIRST)
                .description(PRODUCT_DESCRIPTION_FIRST)
                .link(PRODUCT_LINK_FIRST)
                .build();
        Item itemFirst = Item.builder()
                .id(ID_FIRST)
                .publicId(PUBLIC_ID_FIRST)
                .ownerId(userId)
                .status(AVAILABLE)
                .product(productFirst)
                .quantity(ITEM_QUANTITY_FIRST)
                .build();
        Product productSecond = Product.builder()
                .title(PRODUCT_TITLE_SECOND)
                .description(PRODUCT_DESCRIPTION_SECOND)
                .link(PRODUCT_LINK_SECOND)
                .build();
        Item itemSecond = Item.builder()
                .id(ID_SECOND)
                .publicId(PUBLIC_ID_SECOND)
                .ownerId(userId)
                .status(AVAILABLE)
                .product(productSecond)
                .quantity(ITEM_QUANTITY_SECOND)
                .build();
        itemRepository.saveAll(
                List.of(
                        itemFirst,
                        itemSecond
                )
        );
        ProductRequestMock productRequest = new ProductRequestMock(
                PRODUCT_TITLE_FIRST,
                PRODUCT_DESCRIPTION_FIRST,
                PRODUCT_LINK_FIRST
        );
        ItemRequestMock itemRequest = new ItemRequestMock(
                ITEM_QUANTITY_FIRST,
                AVAILABLE,
                productRequest
        );
        String updateId = itemSecond.getId();

        MvcResult mvcResult = mockMvc.perform(
                        put(URL_WITH_ID, updateId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(itemRequest))
                                .with(mockUser())
                ).andExpect(
                        MockMvcResultMatchers.status().isOk()
                )
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        PublicItemResponse itemResponse = objectMapper.readValue(response, PublicItemResponse.class);

        assetItemRequestReturned(itemRequest, itemResponse);
        assertItemTableSize(2);
        assertItemRequestSaved(itemRequest, updateId);
        assertItemInTable(itemFirst);
    }

    private void assetItemRequestReturned(ItemRequestMock itemRequest, PublicItemResponse itemResponse) {
        assertEquals(itemRequest.quantity(), itemResponse.quantity());
        assetProductRequestReturned(itemRequest.product(), itemResponse.product());
    }

    private void assertItemTableSize(int size) {
        List<Item> items = itemRepository.findAll();
        assertEquals(size, items.size());
    }

    private void assertItemRequestSaved(ItemRequestMock itemRequest, String updateId) {
        Optional<Item> itemOptional = itemRepository.findById(updateId);
        assertTrue(itemOptional.isPresent());
        Item actualItem = itemOptional.get();
        assertEquals(itemRequest.quantity(), actualItem.getQuantity());
        assertProductRequestSaved(itemRequest.product(), actualItem.getProduct());
    }

    private void assertItemInTable(Item expectedItem) {
        Optional<Item> itemOptional = itemRepository.findById(expectedItem.getId());
        assertTrue(itemOptional.isPresent());
        Item actualItem = itemOptional.get();
        assertEquals(expectedItem.getId(), actualItem.getId());
        assertEquals(expectedItem.getPublicId(), actualItem.getPublicId());
        assertEquals(expectedItem.getQuantity(), actualItem.getQuantity());
        assertEquals(expectedItem.getProduct(), actualItem.getProduct());
    }

    private void assetProductRequestReturned(ProductRequestMock productRequest, ProductResponse productResponse) {
        assertEquals(productRequest.title(), productResponse.title());
        assertEquals(productRequest.description(), productResponse.description());
        assertEquals(productRequest.link(), productResponse.link());
    }

    private void assertProductRequestSaved(ProductRequestMock productRequest, Product product) {
        assertEquals(productRequest.title(), product.getTitle());
        assertEquals(productRequest.description(), product.getDescription());
        assertEquals(productRequest.link(), product.getLink());
    }

    @Test
    @DirtiesContext
    @DisplayName("Update by id - not found")
    void updateById_notFound() throws Exception {
        String userId = createUser(EXTERNAL_ID_USER_FIRST);
        Product productFirst = Product.builder()
                .title(PRODUCT_TITLE_FIRST)
                .description(PRODUCT_DESCRIPTION_FIRST)
                .link(PRODUCT_LINK_FIRST)
                .build();
        Item itemFirst = Item.builder()
                .id(ID_FIRST)
                .publicId(PUBLIC_ID_FIRST)
                .ownerId(userId)
                .status(AVAILABLE)
                .product(productFirst)
                .quantity(ITEM_QUANTITY_FIRST)
                .build();
        itemRepository.saveAll(
                List.of(
                        itemFirst
                )
        );
        ProductRequestMock productRequest = new ProductRequestMock(
                PRODUCT_TITLE_SECOND,
                PRODUCT_DESCRIPTION_SECOND,
                PRODUCT_LINK_SECOND
        );
        ItemRequestMock itemRequest = new ItemRequestMock(
                ITEM_QUANTITY_SECOND,
                AVAILABLE,
                productRequest
        );

        MvcResult mvcResult = mockMvc.perform(
                        put(URL_WITH_ID, ID_SECOND)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(itemRequest))
                                .with(mockUser())
                ).andExpect(
                        MockMvcResultMatchers.status().is4xxClientError()
                )
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(response, ErrorResponse.class);
        assertEquals(MESSAGE_NOT_FOUND, errorResponse.message());
        assertItemTableSize(1);
        assertItemInTable(itemFirst);
    }

    @Test
    @DirtiesContext
    @DisplayName("Update by id - incorrect user")
    void updateById_incorrectUser() throws Exception {
        String userId = createUser(EXTERNAL_ID_USER_SECOND);
        Product productFirst = Product.builder()
                .title(PRODUCT_TITLE_FIRST)
                .description(PRODUCT_DESCRIPTION_FIRST)
                .link(PRODUCT_LINK_FIRST)
                .build();
        Item itemFirst = Item.builder()
                .id(ID_FIRST)
                .publicId(PUBLIC_ID_FIRST)
                .ownerId(userId)
                .status(AVAILABLE)
                .product(productFirst)
                .quantity(ITEM_QUANTITY_FIRST)
                .build();
        itemRepository.saveAll(
                List.of(
                        itemFirst
                )
        );
        ProductRequestMock productRequest = new ProductRequestMock(
                PRODUCT_TITLE_SECOND,
                PRODUCT_DESCRIPTION_SECOND,
                PRODUCT_LINK_SECOND
        );
        ItemRequestMock itemRequest = new ItemRequestMock(
                ITEM_QUANTITY_SECOND,
                AVAILABLE,
                productRequest
        );

        MvcResult mvcResult = mockMvc.perform(
                        put(URL_WITH_ID, ID_SECOND)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(itemRequest))
                                .with(mockUser())
                ).andExpect(
                        MockMvcResultMatchers.status().is4xxClientError()
                )
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(response, ErrorResponse.class);
        assertEquals(MESSAGE_NOT_FOUND, errorResponse.message());
        assertItemTableSize(1);
        assertItemInTable(itemFirst);
    }
}
