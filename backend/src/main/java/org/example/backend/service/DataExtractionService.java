package org.example.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.backend.dto.chatgpt.ParsedProductData;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.server.UnsatisfiedRequestParameterException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DataExtractionService {

    private final ChatGptService chatGptService;

    private final ObjectMapper objectMapper;

    private final PageContentService pageContentService;

    public ParsedProductData extract(String userAgent, String url) {
        String rawContent = pageContentService.getPageContent(userAgent, url);
        String minifiedContent = minifyPageContent(rawContent);
        String rawParsedContent = chatGptService.parsePage(minifiedContent);
        try {
            ParsedProductData parsedContent = objectMapper.readValue(rawParsedContent, ParsedProductData.class);
            if (parsedContent.error() != null) {
                throw new IllegalArgumentException(parsedContent.error());
            }
            return parsedContent;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String minifyPageContent(String content) {
        List<Pair<String, String>> templateList = List.of(
                Pair.of("(?is)<script.*?</script>", ""),
                Pair.of("(?is)<style.*?</style>", ""),
                Pair.of("(?is)<iframe.*?</iframe>", ""),
                Pair.of("(?is)<!--.*?-->", ""),
                Pair.of("(?m)^[ \t]*\r?\n", ""),
                Pair.of("\\s+", " "),
                Pair.of("\"", "'"),
                Pair.of("\\\\&", "&"),
                Pair.of("(?is)<link.*?/>", ""),
                Pair.of("(?i)<[^>]*hidden[^>]*>.*?</[^>]+>", "")
        );
        for (Pair<String, String> template : templateList) {
            content = content.replaceAll(template.getFirst(), template.getSecond());
        }

        return content;
    }
}
