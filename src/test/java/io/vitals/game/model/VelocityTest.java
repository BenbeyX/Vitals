package io.vitals.game.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class VelocityTest {
    @Test
    public void testYVelocity() {
        Velocity velocity = new Velocity(0,10);
        assertEquals(0, velocity.getX());
        assertEquals(10, velocity.getY());
    }

    @Test
    public void testXVelocity() {
        Velocity velocity = new Velocity(10,0);
        assertEquals(10, velocity.getX());
        assertEquals(0, velocity.getY());
    }

    @Test
    public void testNegativeVelocity() {
        Velocity velocity = new Velocity(-10,-10);
        assertEquals(-10, velocity.getX());
        assertEquals(-10, velocity.getY());
    }

    
}
