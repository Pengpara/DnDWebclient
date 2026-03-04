package com.example.dungeonsanddragonswebclient.service;

import com.example.dungeonsanddragonswebclient.model.ChatRequest;
import com.example.dungeonsanddragonswebclient.model.ChatResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ChatService {

    private final String API_URL = "https://api.mistral.ai/v1/chat/completions";
    private final String API_KEY = System.getenv("API_KEY");
    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);
    private final RestTemplate restTemplate;
    private List<Map<String, String>> conversationHistory = new ArrayList<>();
    private int fatePoints = 3;

    public ChatService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public int getFatePoints() {
        return fatePoints;
    }

    public void reset() {
        conversationHistory = new ArrayList<>();
        fatePoints = 3;
    }

    private static final List<Map.Entry<String, List<Pattern>>> SCENE_PATTERNS;
    static {
        Map<String, List<String>> synonyms = new LinkedHashMap<>();
        synonyms.put("tavern",     List.of("tavern", "inn", "pub", "alehouse", "taproom"));
        synonyms.put("blacksmith", List.of("blacksmith", "forge", "smithy", "anvil"));
        synonyms.put("altar",      List.of("altar", "shrine", "temple", "chapel", "sanctuary"));
        synonyms.put("alley",      List.of("alley", "alleyway"));
        synonyms.put("dungeon",    List.of("dungeon", "prison", "cell", "crypt", "tomb", "catacomb"));
        synonyms.put("castle",     List.of("castle", "fortress", "citadel", "keep", "stronghold", "battlements", "tower"));
        synonyms.put("cave",       List.of("cave", "cavern", "grotto", "tunnel"));
        synonyms.put("forest",     List.of("forest", "woods", "woodland", "grove", "thicket", "wilderness"));
        synonyms.put("ruins",      List.of("ruins", "rubble", "remnants", "abandoned"));
        synonyms.put("clearing",   List.of("clearing", "glade"));
        synonyms.put("plains",     List.of("plains", "fields", "meadow", "grassland", "heath", "moor"));
        synonyms.put("lake",       List.of("lake", "river", "pond", "stream", "shore", "bay", "coast"));
        SCENE_PATTERNS = synonyms.entrySet().stream()
            .map(e -> Map.entry(
                e.getKey(),
                e.getValue().stream()
                    .map(kw -> Pattern.compile("\\b" + kw + "\\b"))
                    .toList()
            ))
            .toList();
    }

    private static final Pattern SCENE_TAG_PATTERN = Pattern.compile(
        "#(tavern|castle|cave|forest|dungeon|ruins|plains|clearing|lake|blacksmith|altar|alley)\\b",
        Pattern.CASE_INSENSITIVE
    );

    String extractSceneTag(String message) {
        Matcher m = SCENE_TAG_PATTERN.matcher(message);
        return m.find() ? m.group(1).toLowerCase() : null;
    }

    String determineScene(String message) {
        String lower = message.toLowerCase();
        for (var entry : SCENE_PATTERNS) {
            for (Pattern p : entry.getValue()) {
                if (p.matcher(lower).find()) {
                    return entry.getKey();
                }
            }
        }
        return "default";
    }

    private String callMistral(List<Map<String, String>> messages) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String messagesJson = mapper.writeValueAsString(messages);

        String requestJson = String.format(
                "{\n" +
                "  \"model\": \"mistral-medium\",\n" +
                "  \"messages\": %s,\n" +
                "  \"temperature\": 0.8\n" +
                "}", messagesJson);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + API_KEY);

        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
        ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);
        JsonNode root = mapper.readTree(response.getBody());
        return root.path("choices").get(0).path("message").path("content").asText();
    }

    private static final String SYSTEM_PROMPT =
        "You are a Dungeon Master guiding a player through a fantasy world. Speak like a wise, mystical wizard — poetic, but to the point. The story usually starts in a tavern or a small town where the player needs to begin quests. The world should feel alive, reactive, and sometimes dangerous. Present numbered choices (1, 2, 3, and 4), but only as many as make sense for the moment — sometimes 1, 2, or 3; never more than 4. Keep the choices at a reasonable length, so the player can make a choice relatively fast. Choices should feel decisive and impactful, with clear consequences (good or bad), but never provide any direct hint or guidance about the outcome. Avoid providing any explanations about what the choice means or how it affects the story." +
        "The player's decisions should feel important, and their choices should not be hinted at in any way. Each choice should allow the player to shape the world — not every path leads to glory, and not all decisions are rewarded." +
        "**IMPORTANT:**  " +
        "1. The user message may contain a dice roll result, but You MUST NEVER acknowledge the existence of dice rolls or randomness in the response. Do not include any out-of-character commentary, disclaimers, or explanations. You are the world, not a narrator. Stay fully in-character as a Dungeon Master. All outputs must read like immersive fantasy storytelling — NOT game instructions. The player should never know a roll took place. Never say it. Never hint at it. If the player mentions dice or rolls, treat it as narrative inspiration ONLY." +
        "2. **Exclude dice roll information** from player choices, and do not tell the player that a dice roll is used in any way." +
        "3. When the player faces a certain fatal outcome — mortal wounds, lethal trap, or overwhelming defeat with no possible escape — append exactly '#death' at the very end of your response, after all scene tags. Use #death ONLY for genuinely fatal outcomes, not merely dangerous situations." +
        "4. When the player message is exactly 'I spend a fate point to escape death.', narrate their miraculous narrow escape (waking in a tavern, saved by a wandering traveler, etc.). Do not present numbered choices — only the escape narrative. End with a scene tag." +
        "5. When the player message is exactly 'I accept my fate.', write a final poetic death narrative for this character. No numbered choices. This is the end of their story." +
        "6. At the end of every response, append exactly one scene tag from this list: " +
        "#tavern #castle #cave #forest #dungeon #ruins #plains #clearing #lake #blacksmith #altar #alley. " +
        "Choose the tag that best reflects where the scene is currently taking place. " +
        "Always include it — even if the location has not changed. Place it after #death if applicable.";

    public ChatResponse generateAdventureScenario(ChatRequest request) {
        try {
            if (request.isAcceptDeath()) {
                ChatResponse resp = new ChatResponse("", 0, "");
                resp.setFatePoints(fatePoints);
                resp.setGameOver(true);
                return resp;
            }

            if (conversationHistory.isEmpty()) {
                conversationHistory.add(Map.of("role", "system", "content", SYSTEM_PROMPT));
            }

            String userMessage;
            if (request.isSpendFatePoint()) {
                fatePoints--;
                userMessage = "I spend a fate point to escape death.";
            } else {
                userMessage = request.getUserMessage();
            }

            boolean shouldRoll = !request.isSpendFatePoint() && userMessage != null &&
                    !userMessage.toLowerCase().contains("start the adventure") &&
                    !userMessage.toLowerCase().matches(".*\\b(warrior|mage|rogue|cleric)\\b.*");

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
                decoratedMessage = userMessage != null ? userMessage : "";
            }

            conversationHistory.add(Map.of("role", "user", "content", decoratedMessage));
            String botMessage = callMistral(conversationHistory);

            boolean hasDeath = botMessage.contains("#death");
            if (hasDeath) {
                botMessage = botMessage.replace("#death", "").trim();
            }

            // AI tag takes priority; fall back to keyword matching
            String sceneHint = extractSceneTag(botMessage);
            if (sceneHint != null) {
                botMessage = SCENE_TAG_PATTERN.matcher(botMessage).replaceAll("").trim();
            } else {
                sceneHint = determineScene(botMessage);
            }

            conversationHistory.add(Map.of("role", "assistant", "content", botMessage));
            String finalMessage = shouldRoll
                    ? "🎲 You rolled a **%d** — %s\n\n%s\n\n#%s".formatted(dice, diceResult, botMessage, sceneHint)
                    : botMessage + "\n\n#" + sceneHint;

            ChatResponse response = new ChatResponse(finalMessage, dice, diceResult);
            response.setFatePoints(fatePoints);

            if (hasDeath) {
                if (fatePoints > 0) {
                    response.setRequiresFatePoint(true);
                } else {
                    response.setGameOver(true);
                }
            }

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return new ChatResponse("⚠️ Noget gik galt. Prøv igen.", 0, "❓ Unknown");
        }
    }
}
