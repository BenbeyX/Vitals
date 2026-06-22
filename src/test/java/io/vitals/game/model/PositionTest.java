package io.vitals.game.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class PositionTest {
    @Test
    public void testPosition() {
        Position position = new Position(10, 20);
        assertEquals(position.getX(), 10, "X should be 10");
        assertEquals(position.getY(), 20, "Y should be 20");
    }

    @Test
    public void testPositionEquals() {
        Position position1 = new Position(10, 20);
        Position position2 = new Position(10, 20);
        assertEquals(position1.equals(position2), true, "Positions should be equal");
    }

    @Test
    public void testPositionNotEquals() {
        Position position1 = new Position(10, 20);
        Position position2 = new Position(10, 30);
        assertNotEquals(position1.equals(position2), true, "Positions should not be equal");
    }
}
