package com.example.dungeonsanddragonswebclient.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class ChatService {

    private final String API_URL = "https://api.mistral.ai/v1/chat/completions";
    private final String API_KEY = System.getenv("API_KEY");
    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    public String generateAdventureScenario(String prompt) {
        try {
            logger.info("Sending request to Mistral API with prompt: {}", prompt);

            RestTemplate restTemplate = new RestTemplate();

            String requestJson = """
                {
                  "model": "mistral-medium",
                  "messages": [
                    {"role": "system", "content": "You are a Dungeon Master guiding a player through a fantasy world. Always present 3-4 numbered choices in your responses like a text-based adventure game."}
                  ],
                  "temperature": 0.8
                }
                """.formatted(prompt.replace("\"", "\\\""));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + API_KEY);

            HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
            ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            String content = root.path("choices").get(0).path("message").path("content").asText();

            return content;

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Error while calling Mistral API", e);
            return "⚠️ Noget gik galt. Prøv igen.";
        }
    }
}