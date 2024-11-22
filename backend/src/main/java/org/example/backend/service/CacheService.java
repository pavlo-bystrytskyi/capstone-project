package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.dto.chatgpt.ParsedProductData;
import org.example.backend.model.Cache;
import org.example.backend.repository.CacheRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class CacheService {

    private final CacheRepository cacheRepository;

    public void save(String uri, ParsedProductData data) {
        cacheRepository.save(
                Cache
                        .builder()
                        .key(trimUriQuery(uri))
                        .value(data)
                        .build()
        );
    }

    private String trimUriQuery(String uri) {
        return UriComponentsBuilder
                .fromUriString(uri)
                .replaceQuery(null)
                .build()
                .toUriString();
    }

    public ParsedProductData get(String uri) {
        return cacheRepository
                .findById(trimUriQuery(uri))
                .map(Cache::getValue)
                .orElse(null);
    }
}
