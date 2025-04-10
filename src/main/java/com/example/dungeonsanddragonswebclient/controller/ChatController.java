package com.example.dungeonsanddragonswebclient.controller;

import com.example.dungeonsanddragonswebclient.model.ChatRequest;
import com.example.dungeonsanddragonswebclient.model.ChatResponse;
import com.example.dungeonsanddragonswebclient.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

//fed ko
@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "*")  // 🟢 Tillad frontend at tale med backend
public class ChatController {

    @Autowired
    private ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/adventure")
    public ChatResponse getAdventureResponse(@RequestBody ChatRequest chatRequest) {
        return chatService.generateAdventureScenario(chatRequest.getUserMessage());
    }


    @PostMapping("/chat")
    @ResponseBody
    public ResponseEntity<Map<String, String>> getAdventureResponse(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        ChatResponse chatResponse = chatService.generateAdventureScenario(prompt);

        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("response", chatResponse.getMessage());
        responseMap.put("diceRoll", String.valueOf(chatResponse.getDiceRoll()));
        responseMap.put("diceResult", chatResponse.getDiceResult());

        return ResponseEntity.ok(responseMap);
    }
}

