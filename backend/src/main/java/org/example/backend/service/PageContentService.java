package org.example.backend.service;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class PageContentService {

    private final RestClient restClient;

    public PageContentService(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    public String getPageContent(String userAgent, String uri) {
        RestClient.ResponseSpec response = restClient
                .get()
                .uri(uri)
                .header(HttpHeaders.USER_AGENT, userAgent)
                .retrieve();

        return response.body(String.class);
    }
}
