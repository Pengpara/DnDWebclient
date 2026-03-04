package com.example.dungeonsanddragonswebclient.model;

public class ChatRequest {
    private String userMessage;
    private boolean spendFatePoint = false;
    private boolean acceptDeath = false;

    public ChatRequest() {}

    public ChatRequest(String userMessage) {
        this.userMessage = userMessage;
    }

    public String getUserMessage() { return userMessage; }
    public void setUserMessage(String userMessage) { this.userMessage = userMessage; }

    public boolean isSpendFatePoint() { return spendFatePoint; }
    public void setSpendFatePoint(boolean spendFatePoint) { this.spendFatePoint = spendFatePoint; }

    public boolean isAcceptDeath() { return acceptDeath; }
    public void setAcceptDeath(boolean acceptDeath) { this.acceptDeath = acceptDeath; }
}
