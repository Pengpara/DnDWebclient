package com.example.dungeonsanddragonswebclient.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class ChatService {

    private final String API_URL = "https://api.mistral.ai/v1/chat/completions";
    private final String API_KEY = System.getenv("API_KEY");
    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);
    private List<Map<String, String>> conversationHistory = new ArrayList<>();


    public String generateAdventureScenario(String userMessage) {
        try {
            if (conversationHistory.isEmpty()) {
                // Add system prompt only once
                conversationHistory.add(Map.of("role", "system", "content",
                        "You are a Dungeon Master guiding a player through a fantasy world. Always present 3-4 numbered choices."));
            }

            // Add the user's message
            conversationHistory.add(Map.of("role", "user", "content", userMessage));

            // Build messages array
            ObjectMapper mapper = new ObjectMapper();
            String messagesJson = mapper.writeValueAsString(conversationHistory);

            String requestJson = """
        {
          "model": "mistral-medium",
          "messages": %s,
          "temperature": 0.8
        }
        """.formatted(messagesJson);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + API_KEY);

            HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);
            JsonNode root = mapper.readTree(response.getBody());
            String botMessage = root.path("choices").get(0).path("message").path("content").asText();

            // Add assistant reply to history
            conversationHistory.add(Map.of("role", "assistant", "content", botMessage));

            return botMessage;

        } catch (Exception e) {
            e.printStackTrace();
            return "⚠️ Noget gik galt. Prøv igen.";
        }
    }
}