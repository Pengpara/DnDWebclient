package com.example.dungeonsanddragonswebclient.service;

import com.example.dungeonsanddragonswebclient.model.ChatRequest;
import com.example.dungeonsanddragonswebclient.model.ChatResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private ChatService chatService;

    private ResponseEntity<String> mistralResponse(String content) {
        String body = """
            {"choices":[{"message":{"content":"%s"}}]}
            """.formatted(content.replace("\"", "\\\""));
        return new ResponseEntity<>(body, HttpStatus.OK);
    }

    @BeforeEach
    void setUp() {
        chatService = new ChatService(restTemplate);
    }

    @Test
    void normalResponseReturnsFatePoints3AndNoFlags() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(String.class)))
            .thenReturn(mistralResponse("You enter the tavern. #tavern"));

        ChatRequest req = new ChatRequest("I look around");
        ChatResponse resp = chatService.generateAdventureScenario(req);

        assertEquals(3, resp.getFatePoints());
        assertFalse(resp.isRequiresFatePoint());
        assertFalse(resp.isGameOver());
    }

    @Test
    void deathTagWithFatePointsAvailableSetsRequiresFatePoint() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(String.class)))
            .thenReturn(mistralResponse("The dragon kills you. #dungeon #death"));

        ChatRequest req = new ChatRequest("I fight the dragon");
        ChatResponse resp = chatService.generateAdventureScenario(req);

        assertTrue(resp.isRequiresFatePoint());
        assertFalse(resp.isGameOver());
        assertEquals(3, resp.getFatePoints());
        assertFalse(resp.getMessage().contains("#death"));
    }

    @Test
    void deathTagWithNoFatePointsSetsGameOver() {
        for (int i = 0; i < 3; i++) {
            when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenReturn(mistralResponse("You escape. #tavern"));
            ChatRequest spend = new ChatRequest("choice");
            spend.setSpendFatePoint(true);
            chatService.generateAdventureScenario(spend);
        }

        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(String.class)))
            .thenReturn(mistralResponse("You are slain. #dungeon #death"));

        ChatRequest req = new ChatRequest("I fight");
        ChatResponse resp = chatService.generateAdventureScenario(req);

        assertFalse(resp.isRequiresFatePoint());
        assertTrue(resp.isGameOver());
        assertEquals(0, resp.getFatePoints());
    }

    @Test
    void spendFatePointDecrementsFatePoints() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(String.class)))
            .thenReturn(mistralResponse("A traveler finds you and carries you to safety. #tavern"));

        ChatRequest req = new ChatRequest(null);
        req.setSpendFatePoint(true);
        ChatResponse resp = chatService.generateAdventureScenario(req);

        assertEquals(2, resp.getFatePoints());
        assertFalse(resp.isRequiresFatePoint());
        assertFalse(resp.isGameOver());
    }

    @Test
    void acceptDeathReturnsGameOverWithoutApiCall() {
        ChatRequest req = new ChatRequest(null);
        req.setAcceptDeath(true);
        ChatResponse resp = chatService.generateAdventureScenario(req);

        assertTrue(resp.isGameOver());
        verifyNoInteractions(restTemplate);
    }

    @Test
    void resetRestoresFatePointsAndClearsHistory() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(), eq(String.class)))
            .thenReturn(mistralResponse("You escape. #tavern"));
        ChatRequest spend = new ChatRequest(null);
        spend.setSpendFatePoint(true);
        chatService.generateAdventureScenario(spend);
        assertEquals(2, chatService.getFatePoints());

        chatService.reset();
        assertEquals(3, chatService.getFatePoints());
    }
}
