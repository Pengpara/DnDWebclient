package com.example.dungeonsanddragonswebclient.controller;

import com.example.dungeonsanddragonswebclient.model.ChatRequest;
import com.example.dungeonsanddragonswebclient.model.ChatResponse;
import com.example.dungeonsanddragonswebclient.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/adventure")
    public ChatResponse getAdventureResponse(@RequestBody ChatRequest chatRequest) {
        return chatService.generateAdventureScenario(chatRequest);
    }

    @PostMapping("/reset")
    public ResponseEntity<Void> reset() {
        chatService.reset();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/chat")
    @ResponseBody
    public ResponseEntity<Map<String, String>> getChatResponse(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        ChatRequest chatRequest = new ChatRequest(prompt);
        ChatResponse chatResponse = chatService.generateAdventureScenario(chatRequest);

        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("response", chatResponse.getMessage());
        responseMap.put("diceRoll", String.valueOf(chatResponse.getDiceRoll()));
        responseMap.put("diceResult", chatResponse.getDiceResult());

        return ResponseEntity.ok(responseMap);
    }
}
