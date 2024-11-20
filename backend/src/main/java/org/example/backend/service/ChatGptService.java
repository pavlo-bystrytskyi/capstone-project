package org.example.backend.service;

import org.example.backend.dto.chatgpt.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class ChatGptService {

    private static final String PROMPT_TEMPLATE = """
            You should parse a page and get product title and product description.
            Answer should be in a json format:
            {
                "title": string,
                "description": string,
                "error": string | null
            }
            Here is the page content:
            %s
            """;
    public static final String REQUEST_URL = "https://api.openai.com/v1/chat/completions";
    public static final String REQUEST_ROLE = "user";
    public static final String REQUEST_MODEL = "gpt-4o-mini";
    public static final String REQUEST_TYPE = "json_object";
    public static final String REQUEST_AUTHORIZATION_HEADER = "Bearer %s";

    private final RestClient restClient;

    public ChatGptService(
            RestClient.Builder restClientBuilder,
            @Value("${chat.gpt.api.token}") String chatGptApiToken) {
        this.restClient = restClientBuilder
                .baseUrl(REQUEST_URL)
                .defaultHeader(HttpHeaders.AUTHORIZATION, REQUEST_AUTHORIZATION_HEADER.formatted(chatGptApiToken))
                .build();
    }

    public String parsePage(String content) {
        String prompt = PROMPT_TEMPLATE.formatted(content);

        return complete(prompt)
                .choices()
                .getFirst()
                .message()
                .content();
    }

    private ChatGptResponse complete(
            String prompt
    ) {
        ChatGptRequestMessage message = new ChatGptRequestMessage(REQUEST_ROLE, prompt);
        ChatGptRequest request = new ChatGptRequest(REQUEST_MODEL, List.of(message), new ChatGptResponseFormat(REQUEST_TYPE));

        RestClient.ResponseSpec response = restClient
                .post()
                .body(request)
                .retrieve();

        return response.body(ChatGptResponse.class);
    }
}
