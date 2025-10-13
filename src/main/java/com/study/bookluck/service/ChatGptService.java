package com.study.bookluck.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ChatGptService {

    @Value("${openai.api.key}")
    private String OPENAI_API_KEY;

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    public String summarizeReview(String fullReview) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper mapper = new ObjectMapper();

            String prompt = "다음 독후감을 2문장, 50자 이내로 자연스럽게 요약해줘:\n" + fullReview;

            String body = mapper.writeValueAsString(Map.of(
                    "model", "gpt-4o-mini",
                    "messages", List.of(Map.of("role", "user", "content", prompt))
            ));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(OPENAI_API_KEY);

            HttpEntity<String> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, request, String.class);

            JsonNode node = mapper.readTree(response.getBody());
            return node.get("choices").get(0).get("message").get("content").asText().trim();

        } catch (Exception e) {
            e.printStackTrace();
            return "요약 실패";
        }
    }
}
