package com.example.dungeonsanddragonswebclient.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ChatResponseTest {

    @Test
    void newFieldsDefaultToFalseAndZero() {
        ChatResponse r = new ChatResponse("msg", 10, "Success");
        assertFalse(r.isRequiresFatePoint());
        assertFalse(r.isGameOver());
        assertEquals(3, r.getFatePoints());
    }

    @Test
    void settersAndGettersWork() {
        ChatResponse r = new ChatResponse("msg", 0, "");
        r.setRequiresFatePoint(true);
        r.setGameOver(true);
        r.setFatePoints(2);
        assertTrue(r.isRequiresFatePoint());
        assertTrue(r.isGameOver());
        assertEquals(2, r.getFatePoints());
    }
}
