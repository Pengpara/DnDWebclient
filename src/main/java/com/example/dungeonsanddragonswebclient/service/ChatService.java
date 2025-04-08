package com.example.dungeonsanddragonswebclient.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Service
public class ChatService {

    private final String API_URL = "https://api.mistral.ai/generate";  // URL for Mistral API
    private final String API_KEY = "api.key"; // Din API-nøgle her

    public String generateAdventureScenario(String prompt) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + API_KEY);

            String body = "{\"prompt\": \"" + prompt + "\", \"max_tokens\": 150}";
            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);

            return response.getBody(); // For simplicity, return hele svaret.
        } catch (Exception e) {
            e.printStackTrace();
            return "Der opstod en fejl i eventyret. Prøv igen.";
        }
    }

    public int rollDice() {
        return (int) (Math.random() * 20) + 1;  // Rul en D20
    }
}
