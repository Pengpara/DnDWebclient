package com.example.dungeonsanddragonswebclient.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ChatRequestTest {

    @Test
    void newFieldsDefaultToFalse() {
        ChatRequest r = new ChatRequest("hello");
        assertFalse(r.isSpendFatePoint());
        assertFalse(r.isAcceptDeath());
    }

    @Test
    void settersWork() {
        ChatRequest r = new ChatRequest("hello");
        r.setSpendFatePoint(true);
        r.setAcceptDeath(true);
        assertTrue(r.isSpendFatePoint());
        assertTrue(r.isAcceptDeath());
    }
}
