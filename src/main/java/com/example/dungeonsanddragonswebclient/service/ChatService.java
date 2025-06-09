package com.example.dungeonsanddragonswebclient.service;

import com.example.dungeonsanddragonswebclient.model.ChatResponse;
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
import java.util.Random;


@Service
public class ChatService {

    private final String API_URL = "https://api.mistral.ai/v1/chat/completions";
    private final String API_KEY = System.getenv("API_KEY");
    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);
    private List<Map<String, String>> conversationHistory = new ArrayList<>();

    private String determineScene(String message) {
        message = message.toLowerCase();
        if (message.contains("tavern")) return "tavern";
        if (message.contains("castle")) return "castle";
        if (message.contains("cave")) return "cave";
        if (message.contains("alley")) return "alley";
        if (message.contains("forest")) return "forest";
        if (message.contains("inn")) return "Tavern";
        if (message.contains("market")) return "Market";
        if (message.contains("shop")) return "Market";
        if (message.contains("blacksmith")) return "blacksmith";
        if (message.contains("dungeon")) return "dungeon";
        if (message.contains("ruins")) return "ruins";
        if (message.contains("plains")) return "plains";
        if (message.contains("clearing")) return "clearing";
        if (message.contains("lake")) return "lake";
        if (message.contains("altar")) return "altar";
        return "default";
    }


    public ChatResponse generateAdventureScenario(String userMessage) {
        try {
            if (conversationHistory.isEmpty()) {
                conversationHistory.add(Map.of("role", "system", "content",
                        "You are a Dungeon Master guiding a player through a fantasy world. Speak like a wise, mystical wizard — poetic, but to the point. The story usually starts in a tavern or a small town where the player needs to begin quests. The world should feel alive, reactive, and sometimes dangerous. Present numbered choices (1, 2, 3, and 4), but only as many as make sense for the moment — sometimes 1, 2, or 3; never more than 4. Keep the choices at a reasonable length, so the player can make a choice relatively fast. Choices should feel decisive and impactful, with clear consequences (good or bad), but never provide any direct hint or guidance about the outcome. Avoid providing any explanations about what the choice means or how it affects the story." +
                                "The player's decisions should feel important, and their choices should not be hinted at in any way. Each choice should allow the player to shape the world — not every path leads to glory, and not all decisions are rewarded." +
                                "**IMPORTANT:**  " +
                                "1. The user message may contain a dice roll result, but You MUST NEVER acknowledge the existence of dice rolls or randomness in the response. Do not include any out-of-character commentary, disclaimers, or explanations. You are the world, not a narrator. Stay fully in-character as a Dungeon Master. All outputs must read like immersive fantasy storytelling — NOT game instructions. The player should never know a roll took place. Never say it. Never hint at it. If the player mentions dice or rolls, treat it as narrative inspiration ONLY." +
                                "2. **Exclude dice roll information** from player choices, and do not tell the player that a dice roll is used in any way."));
            }

            // Check for whether to skip rolling the dice
            boolean shouldRoll = true;
            String lowerMsg = userMessage.toLowerCase();

            if (lowerMsg.contains("start the adventure") ||
                    lowerMsg.matches(".*\\b(warrior|mage|rogue|cleric)\\b.*")) {
                shouldRoll = false;
            }

            // Dice setup
            Random rand = new Random();
            int dice = 0;
            String diceResult = "";
            String decoratedMessage;

            if (shouldRoll) {
                dice = rand.nextInt(20) + 1;

                if (dice == 20) diceResult = "✨ Critical Success";
                else if (dice >= 18) diceResult = "🏅 Strong Success";
                else if (dice >= 15) diceResult = "✅ Success";
                else if (dice >= 12) diceResult = "😌 Mild Success";
                else if (dice >= 9) diceResult = "😐 Mixed Result";
                else if (dice >= 6) diceResult = "😬 Weak Failure";
                else if (dice >= 3) diceResult = "❌ Fail";
                else if (dice == 2) diceResult = "☠️ Big Mistake";
                else diceResult = "💀 Critical Fail";

                decoratedMessage = "%s (Roll: %d - %s)".formatted(userMessage, dice, diceResult);
            } else {
                decoratedMessage = userMessage;
            }

            conversationHistory.add(Map.of("role", "user", "content", decoratedMessage));

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

            conversationHistory.add(Map.of("role", "assistant", "content", botMessage));

            String sceneHint = determineScene(botMessage);
            String finalMessage = shouldRoll
                    ? "🎲 You rolled a **%d** — %s\n\n%s\n\n#%s".formatted(dice, diceResult, botMessage, sceneHint)
                    : botMessage + "\n\n#" + sceneHint;

            return new ChatResponse(finalMessage, dice, diceResult);

        } catch (Exception e) {
            e.printStackTrace();
            return new ChatResponse("⚠️ Noget gik galt. Prøv igen.", 0, "❓ Unknown");
        }
    }
}