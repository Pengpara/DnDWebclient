package com.example.dungeonsanddragonswebclient.controller;

import com.example.dungeonsanddragonswebclient.model.ChatRequest;
import com.example.dungeonsanddragonswebclient.model.ChatResponse;
import com.example.dungeonsanddragonswebclient.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "*") // Allow requests from any origin for portfolio demo
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private ChatService chatService;

    @PostMapping("/send")
    public ResponseEntity<ChatResponse> sendMessage(@RequestBody ChatRequest request) {
        try {
            logger.info("Received chat request from session: {}", request.getSessionId());

            // Validate request
            if (request.getUserMessage() == null || request.getUserMessage().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ChatResponse("⚠️ Message cannot be empty", 0, "❓ Unknown"));
            }

            if (request.getApiKey() == null || request.getApiKey().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ChatResponse("⚠️ API key is required", 0, "❓ Unknown"));
            }

            if (request.getSessionId() == null || request.getSessionId().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ChatResponse("⚠️ Session ID is required", 0, "❓ Unknown"));
            }

            // Generate response using provided API key
            ChatResponse response = chatService.generateAdventureScenario(
                    request.getUserMessage(),
                    request.getApiKey(),
                    request.getSessionId()
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error processing chat request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ChatResponse("⚠️ An error occurred: " + e.getMessage(), 0, "❓ Unknown"));
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetSession(@RequestBody ChatRequest request) {
        try {
            if (request.getSessionId() != null) {
                chatService.clearSession(request.getSessionId());
                return ResponseEntity.ok("Session reset successfully");
            }
            return ResponseEntity.badRequest().body("Session ID is required");
        } catch (Exception e) {
            logger.error("Error resetting session", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error resetting session: " + e.getMessage());
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Chat service is running. Active sessions: " + chatService.getActiveSessionCount());
    }
}