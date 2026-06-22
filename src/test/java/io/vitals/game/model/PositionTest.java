package io.vitals.game.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class PositionTest {
    @Test
    public void testPosition() {
        Position position = new Position(10, 20);
        assertEquals(10, position.getX());
        assertEquals(20, position.getY());
    }

    @Test
    public void testPositionEquals() {
        Position position1 = new Position(10, 20);
        Position position2 = new Position(10, 20);
        assertEquals(position1, position2);
    }

    @Test
    public void testPositionNotEquals() {
        Position position1 = new Position(10, 20);
        Position position2 = new Position(10, 30);
        assertNotEquals(position1, position2);
    }
}
