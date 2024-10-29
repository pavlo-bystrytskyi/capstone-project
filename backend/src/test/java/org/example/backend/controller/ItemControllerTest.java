package org.example.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.backend.dto.IdResponse;
import org.example.backend.mock.dto.ItemRequestMock;
import org.example.backend.mock.dto.ProductRequestMock;
import org.example.backend.model.Item;
import org.example.backend.model.Product;
import org.example.backend.repository.ItemRepository;
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
class ItemControllerTest {

    private static final String BASE_URL = "/api/item";

    private static final Double ITEM_QUANTITY = 5.5;

    private static final String PRODUCT_TITLE = "some product title";
    private static final String PRODUCT_DESCRIPTION = "some product description";
    private static final String PRODUCT_LINK = "https://example.com/some-product-link";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ItemRepository itemRepository;

    static Stream<Arguments> nullParamDataProvider() {
        ProductRequestMock productRequest = new ProductRequestMock(
                PRODUCT_TITLE,
                PRODUCT_DESCRIPTION,
                PRODUCT_LINK
        );
        ItemRequestMock itemRequest = new ItemRequestMock(
                ITEM_QUANTITY,
                productRequest
        );

        return Stream.of(
                Arguments.of("Empty item quantity", itemRequest.withQuantity(null)),
                Arguments.of("Empty product data", itemRequest.withProduct(null)),
                Arguments.of("Empty product title", itemRequest.withProduct(productRequest.withTitle(null))),
                Arguments.of("Empty product description", itemRequest.withProduct(productRequest.withDescription(null))),
                Arguments.of("Empty product link", itemRequest.withProduct(productRequest.withLink(null)))
        );
    }

    @Test
    @DisplayName("Correct payload")
    @DirtiesContext
    void create_successful() throws Exception {
        ProductRequestMock productRequest = new ProductRequestMock(
                PRODUCT_TITLE,
                PRODUCT_DESCRIPTION,
                PRODUCT_LINK
        );
        ItemRequestMock itemRequest = new ItemRequestMock(
                ITEM_QUANTITY,
                productRequest
        );

        MvcResult mvcResult = mockMvc.perform(
                        post(BASE_URL)
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(itemRequest))
                ).andExpect(
                        MockMvcResultMatchers.status().isOk()
                )
                .andReturn();

        IdResponse response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), IdResponse.class);

        Optional<Item> result = itemRepository.findById(response.id());
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
    @DisplayName("Incorrect payload")
    @MethodSource("nullParamDataProvider")
    void create_nullParam(String name, ItemRequestMock itemRequest) throws Exception {
        mockMvc.perform(
                post(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequest))
        ).andExpect(
                MockMvcResultMatchers.status().is4xxClientError()
        );

        List<Item> itemList = itemRepository.findAll();
        assertTrue(itemList.isEmpty());
    }
}
