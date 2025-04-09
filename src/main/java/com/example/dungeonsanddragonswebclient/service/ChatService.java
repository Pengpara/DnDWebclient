package com.example.dungeonsanddragonswebclient.service;

import com.example.dungeonsanddragonswebclient.model.ChatRequest;
import com.example.dungeonsanddragonswebclient.model.ChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ChatService {

    private final WebClient webClient;

    @Value("${api.key}")
    private String apiKey;

    public ChatService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.mistral.ai") // Assuming you want to interact with the Mistral API
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey) // Adding the authorization header
                .build();
    }

    public String getChatResponse(ChatRequest request) {
        return webClient.post()
                .uri("/v1/chat/completions")
                .header("Authorization", "Bearer " + System.getenv("API_KEY"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ChatResponse.class)
                .map(response -> response.getMessage())
                .block();  // Blocking for simplicity in this example
    }


    public int rollDice() {
        return (int) (Math.random() * 20) + 1;  // Roll a D20
    }

    // Generate adventure scenario entirely from the AI
    public String generateAdventureScenario(String userMessage) {
        // Build the prompt that asks the AI to create an adventure
        String prompt = "You are the Dungeon Master of a Dungeons & Dragons adventure. Create a detailed and engaging scenario " +
                "for the player based on the following action or statement: '" + userMessage + "'. " +
                "Include descriptions of the environment, characters, and any events that occur. Make it exciting, " +
                "creative, and full of mystery. End with the question: 'What do you want to do next?'";

        // Create the ChatRequest to send to the AI
        ChatRequest chatRequest = new ChatRequest(
                "mistral-medium", // Model name or ID (e.g., mistral-medium, gpt-4, etc.)
                List.of(new ChatRequest.Message("user", prompt)),
                0.9,  // Temperature to control randomness
                1.0   // Top-p to control diversity
        );

        // Get the response from the AI
        String aiResponse = getChatResponse(chatRequest);

        return aiResponse;
    }
}