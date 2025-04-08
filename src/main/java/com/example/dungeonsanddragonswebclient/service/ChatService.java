package com.example.dungeonsanddragonswebclient.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {

    private List<String> messages;

    public ChatService() {
        // Initialiserer chatbeskederne
        messages = new ArrayList<>();
        messages.add("🤖 Dungeon Master: Welcome to your adventure! What will you do?");
    }

    // Hent initiale beskeder
    public List<String> getInitialMessages() {
        return messages;
    }

    // Tilføj en besked til listen og returner ny liste
    public void addMessage(String message) {
        messages.add(message);
    }

    // Simuler chatbot-svar
    public String getResponse(String userInput) {
        if (userInput.toLowerCase().contains("roll")) {
            return "🎲 Rolling dice...";
        } else {
            return "🧙 Dungeon Master: I didn't understand that, try again!";
        }
    }
}
