package com.example.dungeonsanddragonswebclient.model;

public class ChatRequest {
    private String userMessage;
    private String apiKey;
    private String sessionId;

    public ChatRequest() {}

    public ChatRequest(String userMessage, String apiKey, String sessionId) {
        this.userMessage = userMessage;
        this.apiKey = apiKey;
        this.sessionId = sessionId;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}