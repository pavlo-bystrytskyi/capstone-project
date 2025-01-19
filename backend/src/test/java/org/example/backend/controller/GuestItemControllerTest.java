package org.example.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.backend.dto.response.ErrorResponse;
import org.example.backend.dto.response.IdResponse;
import org.example.backend.dto.response.item.PublicItemResponse;
import org.example.backend.dto.response.item.ProductResponse;
import org.example.backend.mock.dto.ItemRequestMock;
import org.example.backend.mock.dto.ProductRequestMock;
import org.example.backend.mock.dto.ItemStatusRequestMock;
import org.example.backend.model.Item;
import org.example.backend.model.Product;
import org.example.backend.model.item.ItemStatus;
import org.example.backend.repository.ItemRepository;
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

import static org.example.backend.model.item.ItemStatus.AVAILABLE;
import static org.example.backend.model.item.ItemStatus.PURCHASED;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
class GuestItemControllerTest {

    private static final String URL_BASE = "/api/guest/item";
    private static final String URL_WITH_ID = "/api/guest/item/{id}";
    private static final String URL_PUBLIC_WITH_ID = "/api/guest/item/public/{id}";

    private static final Double ITEM_QUANTITY_FIRST = 5.5;
    private static final Double ITEM_QUANTITY_SECOND = 10.1;

    private static final String PRIVATE_ID_FIRST = "some id 1";
    private static final String PRIVATE_ID_SECOND = "some id 2";
    private static final String PUBLIC_ID_FIRST = "some public id 1";
    private static final String PUBLIC_ID_SECOND = "some public id 2";
    private static final String PRODUCT_TITLE_FIRST = "some product title 1";
    private static final String PRODUCT_TITLE_SECOND = "some product title 2";
    private static final String PRODUCT_DESCRIPTION_FIRST = "some product description 1";
    private static final String PRODUCT_DESCRIPTION_SECOND = "some product description 2";
    private static final String PRODUCT_LINK_FIRST = "https://example.com/some-product-link-1";
    private static final String PRODUCT_LINK_SECOND = "https://example.com/some-product-link-2";

    private static final String MESSAGE_NOT_FOUND = "No value present";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ItemRepository itemRepository;

    static Stream<Arguments> incorrectParamDataProvider() {
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
                Arguments.of("Empty item quantity", itemRequest.withQuantity(null), "quantity: must not be null"),
                Arguments.of("Zero item quantity", itemRequest.withQuantity(0.0), "quantity: must be greater than 0"),
                Arguments.of("Negative item quantity", itemRequest.withQuantity(-1.0), "quantity: must be greater than 0"),
                Arguments.of("Empty product data", itemRequest.withProduct(null), "product: must not be null"),
                Arguments.of("Empty product title", itemRequest.withProduct(productRequest.withTitle(null)), "product.title: must not be null"),
                Arguments.of("Short product title", itemRequest.withProduct(productRequest.withTitle("_".repeat(3))), "product.title: size must be between 4 and 255"),
                Arguments.of("Long product title", itemRequest.withProduct(productRequest.withTitle("_".repeat(256))), "product.title: size must be between 4 and 255"),
                Arguments.of("Empty product description", itemRequest.withProduct(productRequest.withDescription(null)), "product.description: must not be null"),
                Arguments.of("Long product description", itemRequest.withProduct(productRequest.withDescription("_".repeat(4096))), "product.description: size must be between 0 and 4095"),
                Arguments.of("Empty product link", itemRequest.withProduct(productRequest.withLink(null)), "product.link: must not be null"),
                Arguments.of("Incorrect product link", itemRequest.withProduct(productRequest.withLink("null")), "product.link: must be a valid URL"),
                Arguments.of("Empty request", null, "Not readable request body")
        );
    }

    @Test
    @DisplayName("Create - successful")
    @DirtiesContext
    void create_successful() throws Exception {
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
                ).andExpect(
                        MockMvcResultMatchers.status().isOk()
                )
                .andReturn();

        IdResponse response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), IdResponse.class);

        Optional<Item> result = itemRepository.findByPrivateId(response.privateId());
        assertTrue(result.isPresent());
        Item item = result.get();
        assertEquals(itemRequest.quantity(), item.getQuantity());
        Product product = item.getProduct();
        assertEquals(productRequest.title(), product.getTitle());
        assertEquals(productRequest.description(), product.getDescription());
        assertEquals(productRequest.link(), product.getLink());
    }

    @ParameterizedTest(name = "{0}")
    @DirtiesContext
    @DisplayName("Create - incorrect payload")
    @MethodSource("incorrectParamDataProvider")
    void create_incorrectParam(String name, ItemRequestMock itemRequest, String expectedMessage) throws Exception {
        MvcResult mvcResult = mockMvc.perform(
                post(URL_BASE)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequest))
        ).andExpect(
                MockMvcResultMatchers.status().is4xxClientError()
        ).andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        ErrorResponse errorResponse = objectMapper.readValue(response, ErrorResponse.class);
        assertEquals(expectedMessage, errorResponse.message());
        List<Item> itemList = itemRepository.findAll();
        assertTrue(itemList.isEmpty());
    }

    @Test
    @DirtiesContext
    @DisplayName("Get by id - successful")
    void getById_successful() throws Exception {
        Product productFirst = Product.builder()
                .title(PRODUCT_TITLE_FIRST)
                .description(PRODUCT_DESCRIPTION_FIRST)
                .link(PRODUCT_LINK_FIRST)
                .build();
        Item itemFirst = Item.builder()
                .privateId(PRIVATE_ID_FIRST)
                .publicId(PUBLIC_ID_FIRST)
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
                .privateId(PRIVATE_ID_SECOND)
                .publicId(PUBLIC_ID_SECOND)
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
                        get(URL_WITH_ID, itemFirst.getPrivateId())
                ).andExpect(
                        MockMvcResultMatchers.status().isOk()
                )
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertFalse(response.contains(itemFirst.getPrivateId()));
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
    @DisplayName("Get by id - not found")
    void getById_notFound() throws Exception {
        Product productFirst = Product.builder()
                .title(PRODUCT_TITLE_FIRST)
                .description(PRODUCT_DESCRIPTION_FIRST)
                .link(PRODUCT_LINK_FIRST)
                .build();
        Item itemFirst = Item.builder()
                .privateId(PRIVATE_ID_FIRST)
                .publicId(PUBLIC_ID_FIRST)
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
                        get(URL_WITH_ID, PRIVATE_ID_SECOND)
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
        Product productFirst = Product.builder()
                .title(PRODUCT_TITLE_FIRST)
                .description(PRODUCT_DESCRIPTION_FIRST)
                .link(PRODUCT_LINK_FIRST)
                .build();
        Item itemFirst = Item.builder()
                .privateId(PRIVATE_ID_FIRST)
                .publicId(PUBLIC_ID_FIRST)
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
                .privateId(PRIVATE_ID_SECOND)
                .publicId(PUBLIC_ID_SECOND)
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
                        get(URL_PUBLIC_WITH_ID, itemFirst.getPublicId())
                ).andExpect(
                        MockMvcResultMatchers.status().isOk()
                )
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertFalse(response.contains(itemFirst.getPrivateId()));
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
    @DisplayName("Get by public id - not found")
    void getByPublicId_notFound() throws Exception {
        Product productFirst = Product.builder()
                .title(PRODUCT_TITLE_FIRST)
                .description(PRODUCT_DESCRIPTION_FIRST)
                .link(PRODUCT_LINK_FIRST)
                .build();
        Item itemFirst = Item.builder()
                .privateId(PRIVATE_ID_FIRST)
                .publicId(PUBLIC_ID_FIRST)
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
        Product productFirst = Product.builder()
                .title(PRODUCT_TITLE_FIRST)
                .description(PRODUCT_DESCRIPTION_FIRST)
                .link(PRODUCT_LINK_FIRST)
                .build();
        Item itemFirst = Item.builder()
                .privateId(PRIVATE_ID_FIRST)
                .publicId(PUBLIC_ID_FIRST)
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
                .privateId(PRIVATE_ID_SECOND)
                .publicId(PUBLIC_ID_SECOND)
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
                delete(URL_WITH_ID, itemFirst.getPrivateId())
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );

        List<Item> items = itemRepository.findAll();
        assertEquals(1, items.size());
        Optional<Item> item = itemRepository.findByPrivateId(itemFirst.getPrivateId());
        assertTrue(item.isEmpty());
    }

    @Test
    @DirtiesContext
    @DisplayName("Delete by id - not found")
    void deleteById_notFound() throws Exception {
        Product productFirst = Product.builder()
                .title(PRODUCT_TITLE_FIRST)
                .description(PRODUCT_DESCRIPTION_FIRST)
                .link(PRODUCT_LINK_FIRST)
                .build();
        Item itemFirst = Item.builder()
                .privateId(PRIVATE_ID_FIRST)
                .publicId(PUBLIC_ID_FIRST)
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
                delete(URL_WITH_ID, PRIVATE_ID_SECOND)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );

        List<Item> items = itemRepository.findAll();
        assertEquals(1, items.size());
        Optional<Item> item = itemRepository.findByPrivateId(itemFirst.getPrivateId());
        assertTrue(item.isPresent());
    }

    @Test
    @DirtiesContext
    @DisplayName("Update by id - successful")
    void updateById_successful() throws Exception {
        Product productFirst = Product.builder()
                .title(PRODUCT_TITLE_FIRST)
                .description(PRODUCT_DESCRIPTION_FIRST)
                .link(PRODUCT_LINK_FIRST)
                .build();
        Item itemFirst = Item.builder()
                .privateId(PRIVATE_ID_FIRST)
                .publicId(PUBLIC_ID_FIRST)
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
                .privateId(PRIVATE_ID_SECOND)
                .publicId(PUBLIC_ID_SECOND)
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
        String updateId = itemSecond.getPrivateId();

        MvcResult mvcResult = mockMvc.perform(
                        put(URL_WITH_ID, updateId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(itemRequest))
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
        Optional<Item> itemOptional = itemRepository.findByPrivateId(updateId);
        assertTrue(itemOptional.isPresent());
        Item actualItem = itemOptional.get();
        assertEquals(itemRequest.quantity(), actualItem.getQuantity());
        assertProductRequestSaved(itemRequest.product(), actualItem.getProduct());
    }

    private void assertItemInTable(Item expectedItem) {
        Optional<Item> itemOptional = itemRepository.findByPrivateId(expectedItem.getPrivateId());
        assertTrue(itemOptional.isPresent());
        Item actualItem = itemOptional.get();
        assertEquals(expectedItem.getPrivateId(), actualItem.getPrivateId());
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
        Product productFirst = Product.builder()
                .title(PRODUCT_TITLE_FIRST)
                .description(PRODUCT_DESCRIPTION_FIRST)
                .link(PRODUCT_LINK_FIRST)
                .build();
        Item itemFirst = Item.builder()
                .privateId(PRIVATE_ID_FIRST)
                .publicId(PUBLIC_ID_FIRST)
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
                        put(URL_WITH_ID, PRIVATE_ID_SECOND)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(itemRequest))
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
    @DisplayName("Update status by public id - successful")
    void updateStatusByPublicId_successful() throws Exception {
        Product productFirst = Product.builder()
                .title(PRODUCT_TITLE_FIRST)
                .description(PRODUCT_DESCRIPTION_FIRST)
                .link(PRODUCT_LINK_FIRST)
                .build();
        Item itemFirst = Item.builder()
                .privateId(PRIVATE_ID_FIRST)
                .publicId(PUBLIC_ID_FIRST)
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
                .privateId(PRIVATE_ID_SECOND)
                .publicId(PUBLIC_ID_SECOND)
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
        ItemStatus newStatus = PURCHASED;
        ItemStatusRequestMock itemRequest = new ItemStatusRequestMock(
                newStatus
        );
        String updateId = itemSecond.getPublicId();

        MvcResult mvcResult = mockMvc.perform(
                        put(URL_PUBLIC_WITH_ID, updateId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(itemRequest))
                ).andExpect(
                        MockMvcResultMatchers.status().isOk()
                )
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        PublicItemResponse publicItemResponse = objectMapper.readValue(response, PublicItemResponse.class);

        assertEquals(newStatus, publicItemResponse.status());
        assertItemTableSize(2);
        assertItemInTable(itemFirst);
        assertItemInTable(itemSecond.withStatus(newStatus));
    }
}
