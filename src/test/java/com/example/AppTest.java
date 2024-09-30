package com.example;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class AppTest {
    private App app;

    @Before
    public void setUp() {
        app = new App();
    }

    @Test
    public void testGetMessage() {
        assertEquals("Hello, World!", app.getMessage());
    }

    @Test
    public void testGetMessageNotNull() {
        assertNotNull(app.getMessage());
    }

    @Test
    public void testGetMessageLength() {
        assertTrue(app.getMessage().length() > 0);
    }
}