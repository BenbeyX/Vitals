package io.vitals.game.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class PositionTest {
    @Test
    @DisplayName("Position stores X and Y coordinates correctly")
    public void testPosition() {
        Position position = new Position(10, 20);
        assertEquals(position.getX(), 10, "X should be 10");
        assertEquals(position.getY(), 20, "Y should be 20");
    }

    @Test
    @DisplayName("Equal positions have same coordinates")
    public void testPositionEquals() {
        Position position1 = new Position(10, 20);
        Position position2 = new Position(10, 20);
        assertEquals(position1.equals(position2), true, "Positions should be equal");
    }

    @Test
    @DisplayName("Different positions have different coordinates")
    public void testPositionNotEquals() {
        Position position1 = new Position(10, 20);
        Position position2 = new Position(10, 30);
        assertNotEquals(position1.equals(position2), true, "Positions should not be equal");
    }

    @Test
    @DisplayName("distanceTo calculates correct distance between positions")
    public void testDistanceTo_samePosition_returnsZero() {
        Position position = new Position(10, 20);
        assertEquals(0, position.distanceTo(position), 0.001, "Distance to self should be 0");
    }

    @Test
    @DisplayName("distanceTo calculates correct horizontal distance")
    public void testDistanceTo_horizontalDistance() {
        Position position1 = new Position(0, 0);
        Position position2 = new Position(100, 0);
        assertEquals(100, position1.distanceTo(position2), 0.001, "Horizontal distance should be 100");
    }

    @Test
    @DisplayName("distanceTo calculates correct vertical distance")
    public void testDistanceTo_verticalDistance() {
        Position position1 = new Position(0, 0);
        Position position2 = new Position(0, 100);
        assertEquals(100, position1.distanceTo(position2), 0.001, "Vertical distance should be 100");
    }

    @Test
    @DisplayName("distanceTo calculates correct diagonal distance")
    public void testDistanceTo_diagonalDistance() {
        Position position1 = new Position(0, 0);
        Position position2 = new Position(30, 40);
        assertEquals(50, position1.distanceTo(position2), 0.001, "Diagonal distance should be 50");
    }
}
