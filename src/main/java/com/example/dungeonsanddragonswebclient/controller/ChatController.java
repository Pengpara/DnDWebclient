package com.example.dungeonsanddragonswebclient.controller;

import com.example.dungeonsanddragonswebclient.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ChatController {

    @Autowired
    private ChatService chatService;

    // Vis HTML-siden med startbeskeder
    @GetMapping("/")
    public String showChat(Model model) {
        model.addAttribute("chatMessages", chatService.getInitialMessages());
        return "index"; // Thymeleaf view navn (index.html)
    }

    // Håndterer brugerens input (chat)
    @PostMapping("/sendMessage")
    public String sendMessage(@RequestParam String userInput, Model model) {
        // Hent chatbot svar ved at bruge chatService
        String response = chatService.getResponse(userInput);

        // Tilføj brugerens input og DM's svar til chatbeskedlisten
        chatService.addMessage("🧝 You: " + userInput);
        chatService.addMessage("🧙 Dungeon Master: " + response);

        // Opdater modellen med chatbeskeder
        model.addAttribute("chatMessages", chatService.getInitialMessages());
        return "index"; // Returnerer til index.html med opdaterede beskeder
    }

    // Håndterer roll dice
    @PostMapping("/rollDice")
    public String rollDice(Model model) {
        int roll = (int) (Math.random() * 20) + 1;
        String diceMessage = "You rolled a " + roll;

        // Tilføj terningens resultat til beskedlisten
        chatService.addMessage("🎲 Dice: " + diceMessage);

        // Opdater modellen med chatbeskeder
        model.addAttribute("chatMessages", chatService.getInitialMessages());
        return "index"; // Returner til index.html med opdaterede beskeder
    }
}
