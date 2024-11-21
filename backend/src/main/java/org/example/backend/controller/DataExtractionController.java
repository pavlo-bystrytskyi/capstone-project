package org.example.backend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.example.backend.dto.chatgpt.ParsedProductData;
import org.example.backend.dto.request.DataExtractionRequest;
import org.example.backend.dto.response.DataExtractionResponse;
import org.example.backend.service.DataExtractionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/extraction")
@RequiredArgsConstructor
public class DataExtractionController {

    private final DataExtractionService dataExtractionService;

    @PostMapping
    public DataExtractionResponse extract(
            @RequestHeader("User-Agent") @NotNull String userAgent,
            @Valid @RequestBody DataExtractionRequest dataExtractionRequest
    ) {
        ParsedProductData parsedProductData = dataExtractionService.extract(userAgent, dataExtractionRequest.url());

        return DataExtractionResponse.of(parsedProductData);
    }
}
