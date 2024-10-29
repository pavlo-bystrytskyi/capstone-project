package org.example.backend.advice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class GlobalExceptionHandlerTest {

    private static final String PATH_UNKNOWN = "/api/some-endpoint";
    private static final String PATH_ITEM = "/api/item";

    private static final String MESSAGE_UNKNOWN_RESOURCE = "No static resource %s.".formatted(PATH_UNKNOWN.substring(1));

    private static final String MESSAGE_NOT_READABLE = "Not readable request body";

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Empty payload")
    void handleHttpMessageNotReadableException() throws Exception {
        mockMvc.perform(
                        post(PATH_ITEM)
                                .contentType(APPLICATION_JSON)
                                .content("")
                )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(jsonPath("$.message").value(MESSAGE_NOT_READABLE));
    }

    @Test
    @DisplayName("Unknown endpoint")
    void handleNoResourceFoundException() throws Exception {
        mockMvc.perform(get(PATH_UNKNOWN))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(jsonPath("$.message").value(MESSAGE_UNKNOWN_RESOURCE));
    }
}