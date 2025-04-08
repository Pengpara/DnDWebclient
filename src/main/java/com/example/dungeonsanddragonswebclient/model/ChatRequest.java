package com.example.dungeonsanddragonswebclient.model;

public class ChatRequest {

    private String userMessage;  // Brugerens besked, der sendes til chatbotten

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

    @Override
    public String toString() {
        return "ChatRequest{" +
                "userMessage='" + userMessage + '\'' +
                '}';
    }
}
