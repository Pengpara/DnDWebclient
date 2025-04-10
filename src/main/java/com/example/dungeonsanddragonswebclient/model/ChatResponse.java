package com.example.dungeonsanddragonswebclient.model;

public class ChatResponse {
    private String message;
    private int diceRoll;
    private String diceResult;

    public ChatResponse() {}

    public ChatResponse(String message, int diceRoll, String diceResult) {
        this.message = message;
        this.diceRoll = diceRoll;
        this.diceResult = diceResult;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getDiceRoll() {
        return diceRoll;
    }

    public void setDiceRoll(int diceRoll) {
        this.diceRoll = diceRoll;
    }

    public String getDiceResult() {
        return diceResult;
    }

    public void setDiceResult(String diceResult) {
        this.diceResult = diceResult;
    }
}
