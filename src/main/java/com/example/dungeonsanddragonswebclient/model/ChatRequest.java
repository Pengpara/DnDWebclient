package com.example.dungeonsanddragonswebclient.model;

public class ChatRequest {
    private String userMessage;

    public ChatRequest() {}

    public ChatRequest(String userMessage) {
        this.userMessage = userMessage;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }
}
