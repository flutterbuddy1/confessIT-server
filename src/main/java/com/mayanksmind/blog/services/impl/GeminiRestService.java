package com.mayanksmind.blog.services.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mayanksmind.blog.dto.GeminiResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class GeminiRestService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String geminiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GeminiResponseDto analyzeConfession(String confession) {
        // Build safe prompt
        String prompt = """
            You are an AI that analyzes confessions.

            Task 1: Sentiment
            - Classify as Positive, Negative, or Neutral.

            Task 2: Redaction
            - Mask names → keep first letter, replace rest with "**".
              Example: Mayank → M**, Ravi → R**.
            - Mask abusive/offensive words → keep first letter, replace rest with "*".
              Example: stupid → s*****, idiot → i****.
            - Preserve sentence structure.

            Output JSON only (no markdown, no explanations):
            {
              "sentiment": "Positive|Negative|Neutral",
              "cleaned_text": "text with masking"
            }

            Confession:
            "%s"
            """.formatted(confession);

        String url = geminiUrl + apiKey;

        try {
            // 🔹 Build JSON safely instead of string formatting
            Map<String, Object> payload = Map.of(
                    "contents", new Object[]{
                            Map.of("parts", new Object[]{
                                    Map.of("text", prompt)
                            })
                    }
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(payload), headers);

            ResponseEntity<String> response =
                    restTemplate.postForEntity(url, entity, String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            String rawText = root.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

            // 🔹 Clean markdown wrapper like ```json ... ```
            String cleaned = rawText
                    .replaceAll("```json", "")
                    .replaceAll("```", "")
                    .trim();

            JsonNode jsonResult = objectMapper.readTree(cleaned);

            return new GeminiResponseDto(
                    jsonResult.path("sentiment").asText(),
                    jsonResult.path("cleaned_text").asText()
            );

        } catch (HttpClientErrorException ex) {
            return new GeminiResponseDto("Error", ex.getResponseBodyAsString());
        } catch (Exception ex) {
            return new GeminiResponseDto("Error", ex.getMessage());
        }
    }
}
