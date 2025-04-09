package com.example.dungeonsanddragonswebclient;

import com.example.dungeonsanddragonswebclient.model.ChatRequest;
import com.example.dungeonsanddragonswebclient.model.ChatResponse;
import com.example.dungeonsanddragonswebclient.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
        String botReply = chatService.generateAdventureScenario(chatRequest.getUserMessage());
        return new ChatResponse(botReply);
    }


    @PostMapping("/chat")
    @ResponseBody
    public ResponseEntity<Map<String, String>> getAdventureResponse(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        String response = chatService.generateAdventureScenario(prompt);
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("response", response);
        return ResponseEntity.ok(responseMap);
    }
}
