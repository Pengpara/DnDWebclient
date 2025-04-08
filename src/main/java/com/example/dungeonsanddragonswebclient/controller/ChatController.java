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
        String scenario = chatService.generateAdventureScenario(chatRequest.getUserMessage());
        return new ResponseEntity<>(new ChatResponse(scenario), HttpStatus.OK);
    }

    @PostMapping("/roll-dice")
    public ResponseEntity<ChatResponse> rollDice() {
        int rollResult = chatService.rollDice();
        String rollMessage = "🎲 Du rullede en: " + rollResult;
        return new ResponseEntity<>(new ChatResponse(rollMessage), HttpStatus.OK);
    }
}
