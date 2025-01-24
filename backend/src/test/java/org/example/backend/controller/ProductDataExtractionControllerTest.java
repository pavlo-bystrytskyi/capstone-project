package org.example.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.backend.dto.chatgpt.ChatGptResponse;
import org.example.backend.dto.chatgpt.ChatGptResponseChoice;
import org.example.backend.dto.chatgpt.ChatGptResponseChoiceMessage;
import org.example.backend.dto.chatgpt.ParsedProductData;
import org.example.backend.dto.request.DataExtractionRequest;
import org.example.backend.dto.response.DataExtractionResponse;
import org.example.backend.dto.response.ErrorResponse;
import org.example.backend.service.PageContentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureMockRestServiceServer
class ProductDataExtractionControllerTest {

    private static final String PRODUCT_TITLE = "some title";
    private static final String PRODUCT_DESCRIPTION = "some description";
    private static final String PRODUCT_ERROR = "some error";
    private static final String CHATGPT_INCORRECT_RESPONSE = "Some text";
    private static final String CONTROLLER_URL = "/api/extraction";
    private static final String USER_AGENT_HEADER_NAME = "User-Agent";
    private static final String USER_AGENT_HEADER_VALUE = "some user agent";
    private static final String PRODUCT_PAGE_URL = "https://www.example.com";
    private static final String PRODUCT_PAGE_CONTENT = "some page content";
    private static final String ERROR_MESSAGE_GENERIC = "Something went wrong";

    @MockBean
    private PageContentService pageContentService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private MockRestServiceServer mockRestServiceServer;

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setUpChatGptMock(String response) {
        ChatGptResponse chatGptResponse = new ChatGptResponse(
                List.of(
                        new ChatGptResponseChoice(
                                new ChatGptResponseChoiceMessage(
                                        "User",
                                        response
                                )
                        )
                )
        );
        mockRestServiceServer.expect(requestTo("https://api.openai.com/v1/chat/completions"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(
                        withSuccess(
                                asJsonString(chatGptResponse),
                                MediaType.APPLICATION_JSON)
                );
    }

    private void setUpProductPageMock(String uri, String response) {
        when(
                pageContentService.getPageContent(USER_AGENT_HEADER_VALUE, uri)
        ).thenReturn(
                response
        );
    }

    @Test
    @DisplayName("Extract product data - successful")
    @DirtiesContext
    void extract_successful() throws Exception {
        ParsedProductData chatGptResponse = new ParsedProductData(PRODUCT_TITLE, PRODUCT_DESCRIPTION, null);
        String chatGptRawResponse = asJsonString(chatGptResponse);
        setUpChatGptMock(chatGptRawResponse);
        setUpProductPageMock(PRODUCT_PAGE_URL, PRODUCT_PAGE_CONTENT);
        MvcResult mvcResult = mvc.perform(
                        post(CONTROLLER_URL)
                                .header(USER_AGENT_HEADER_NAME, USER_AGENT_HEADER_VALUE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(new DataExtractionRequest(PRODUCT_PAGE_URL))))
                .andExpect(status().isOk())
                .andReturn();

        DataExtractionResponse response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), DataExtractionResponse.class);
        assertNotNull(response);
        assertEquals(chatGptResponse.title(), response.title());
        assertEquals(chatGptResponse.description(), response.description());
    }

    @Test
    @DisplayName("Extract product data - failed")
    void extract_failed() throws Exception {
        ParsedProductData chatGptResponse = new ParsedProductData(PRODUCT_TITLE, PRODUCT_DESCRIPTION, PRODUCT_ERROR);
        String chatGptRawResponse = asJsonString(chatGptResponse);
        setUpChatGptMock(chatGptRawResponse);
        setUpProductPageMock(PRODUCT_PAGE_URL, PRODUCT_PAGE_CONTENT);
        MvcResult mvcResult = mvc.perform(
                        post(CONTROLLER_URL)
                                .header(USER_AGENT_HEADER_NAME, USER_AGENT_HEADER_VALUE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(new DataExtractionRequest(PRODUCT_PAGE_URL))))
                .andExpect(status().is4xxClientError())
                .andReturn();

        ErrorResponse response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorResponse.class);
        assertNotNull(response);
        assertEquals(chatGptResponse.error(), response.message());
    }

    @Test
    @DisplayName("Extract product data - parsing exception")
    void extract_exception() throws Exception {
        setUpChatGptMock(CHATGPT_INCORRECT_RESPONSE);
        setUpProductPageMock(PRODUCT_PAGE_URL, PRODUCT_PAGE_CONTENT);
        MvcResult mvcResult = mvc.perform(
                        post(CONTROLLER_URL)
                                .header(USER_AGENT_HEADER_NAME, USER_AGENT_HEADER_VALUE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(asJsonString(new DataExtractionRequest(PRODUCT_PAGE_URL))))
                .andExpect(status().is5xxServerError())
                .andReturn();

        ErrorResponse response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ErrorResponse.class);
        assertNotNull(response);
        assertEquals(ERROR_MESSAGE_GENERIC, response.message());
    }
}
