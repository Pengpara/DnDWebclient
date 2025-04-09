package com.example.dungeonsanddragonswebclient;

import com.example.dungeonsanddragonswebclient.service.ChatService;
import com.example.dungeonsanddragonswebclient.model.ChatRequest;
import com.example.dungeonsanddragonswebclient.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/adventure")
    public ResponseEntity<ChatResponse> startAdventure(@RequestBody ChatRequest chatRequest) {
        // Extract the user message from the first Message object in the list
        String userMessage = chatRequest.getMessages().get(0).getContent(); // Get the content of the first message
        String scenario = chatService.generateAdventureScenario(userMessage);
        return new ResponseEntity<>(new ChatResponse(scenario), HttpStatus.OK);
    }

    @PostMapping("/roll-dice")
    public ResponseEntity<ChatResponse> rollDice() {
        int rollResult = chatService.rollDice();
        String rollMessage = "🎲 You rolled: " + rollResult;
        return new ResponseEntity<>(new ChatResponse(rollMessage), HttpStatus.OK);
    }
}
