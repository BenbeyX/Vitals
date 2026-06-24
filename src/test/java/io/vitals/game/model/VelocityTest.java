package io.vitals.game.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class VelocityTest {
    @Test
    @DisplayName("Velocity with Y component only")
    public void testYVelocity() {
        Velocity velocity = new Velocity(0,10);
        assertEquals(0, velocity.getX(), "X should be 0");
        assertEquals(10, velocity.getY(), "Y should be 10");
    }

    @Test
    @DisplayName("Velocity with X component only")
    public void testXVelocity() {
        Velocity velocity = new Velocity(10,0);
        assertEquals(10, velocity.getX(), "X should be 10");
        assertEquals(0, velocity.getY(), "Y should be 0");
    }

    @Test
    @DisplayName("Velocity with negative components")
    public void testNegativeVelocity() {
        Velocity velocity = new Velocity(-10,-10);
        assertEquals(-10, velocity.getX(), "X should be -10");
        assertEquals(-10, velocity.getY(), "Y should be -10");
    }

    
}
