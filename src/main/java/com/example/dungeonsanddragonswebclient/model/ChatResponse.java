package com.example.dungeonsanddragonswebclient.model;

public class ChatResponse {
    private String message;
    private int diceRoll;
    private String diceResult;
    private int fatePoints = 3;
    private boolean requiresFatePoint = false;
    private boolean gameOver = false;

    public ChatResponse() {}

    public ChatResponse(String message, int diceRoll, String diceResult) {
        this.message = message;
        this.diceRoll = diceRoll;
        this.diceResult = diceResult;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public int getDiceRoll() { return diceRoll; }
    public void setDiceRoll(int diceRoll) { this.diceRoll = diceRoll; }

    public String getDiceResult() { return diceResult; }
    public void setDiceResult(String diceResult) { this.diceResult = diceResult; }

    public int getFatePoints() { return fatePoints; }
    public void setFatePoints(int fatePoints) { this.fatePoints = fatePoints; }

    public boolean isRequiresFatePoint() { return requiresFatePoint; }
    public void setRequiresFatePoint(boolean requiresFatePoint) { this.requiresFatePoint = requiresFatePoint; }

    public boolean isGameOver() { return gameOver; }
    public void setGameOver(boolean gameOver) { this.gameOver = gameOver; }
}
