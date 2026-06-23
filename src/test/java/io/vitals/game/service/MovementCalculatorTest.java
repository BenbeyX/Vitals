package io.vitals.game.service;

import io.vitals.game.model.Position;
import io.vitals.game.model.Velocity;
import io.vitals.game.constants.GameConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

public class MovementCalculatorTest {

    private MovementCalculator calculator;

    @BeforeEach
    public void setUp() {
        calculator = new MovementCalculator();
    }

    @Test
    public void testCalculateNextPosition_MovingRight() {
        Position position = new Position(100, 100);
        Velocity velocity = new Velocity(50, 0);
        double deltaTime = 2.0;

        Position result = calculator.calculateNextPosition(position, velocity, deltaTime);

        assertEquals(200, result.getX(), 0.001, "X should be 200");
        assertEquals(100, result.getY(), 0.001, "Y should remain 100");
    }

    @Test
    public void testCalculateNextPosition_MovingUp() {
        Position position = new Position(100, 100);
        Velocity velocity = new Velocity(0, -30);
        double deltaTime = 2.0;

        Position result = calculator.calculateNextPosition(position, velocity, deltaTime);

        assertEquals(100, result.getX(), 0.001, "X should remain 100");
        assertEquals(40, result.getY(), 0.001, "Y should be 40");
    }

    @Test
    public void testCalculateNextPosition_MovingDiagonally() {
        Position position = new Position(0, 0);
        Velocity velocity = new Velocity(10, 10);
        double deltaTime = 5.0;

        Position result = calculator.calculateNextPosition(position, velocity, deltaTime);

        assertEquals(50, result.getX(), 0.001, "X should be 50");
        assertEquals(50, result.getY(), 0.001, "Y should be 50");
    }

    @Test
    public void testCalculateNextPosition_ZeroVelocity() {
        Position position = new Position(100, 200);
        Velocity velocity = new Velocity(0, 0);
        double deltaTime = 1.0;

        Position result = calculator.calculateNextPosition(position, velocity, deltaTime);

        assertEquals(100, result.getX(), 0.001, "X should remain 100");
        assertEquals(200, result.getY(), 0.001, "Y should remain 200");
    }

    @Test
    public void testApplyBoundaryConstraints_PositionWithinBounds() {
        Position position = new Position(400, 300);
        Velocity velocity = new Velocity(10, 10);

        Position result = calculator.applyBoundaryConstraints(position, velocity, 1.0f);

        assertEquals(400, result.getX(), 0.001, "X should remain 400");
        assertEquals(300, result.getY(), 0.001, "Y should remain 300");
    }

    @Test
    public void testApplyBoundaryConstraints_PositionBelowZeroX() {
        Position position = new Position(-50, 100);

        Position result = calculator.applyBoundaryConstraints(position, new Velocity(0, 0), 1.0f);

        assertEquals(0, result.getX(), 0.001, "X should be clamped to 0");
        assertEquals(100, result.getY(), 0.001, "Y should remain 100");
    }

    @Test
    public void testApplyBoundaryConstraints_PositionBelowZeroY() {
        Position position = new Position(100, -50);

        Position result = calculator.applyBoundaryConstraints(position, new Velocity(0, 0), 1.0f);

        assertEquals(100, result.getX(), 0.001, "X should remain 100");
        assertEquals(0, result.getY(), 0.001, "Y should be clamped to 0");
    }

    @Test
    public void testApplyBoundaryConstraints_PositionExceedsMaxX() {
        Position position = new Position(GameConstants.ARENA_SIZE_X + 100, 100);

        Position result = calculator.applyBoundaryConstraints(position, new Velocity(0, 0), 1.0f);

        assertEquals(GameConstants.ARENA_SIZE_X, result.getX(), 0.001, "X should be clamped to ARENA_SIZE_X");
        assertEquals(100, result.getY(), 0.001, "Y should remain 100");
    }

    @Test
    public void testApplyBoundaryConstraints_PositionExceedsMaxY() {
        Position position = new Position(100, GameConstants.ARENA_SIZE_Y + 100);

        Position result = calculator.applyBoundaryConstraints(position, new Velocity(0, 0), 1.0f);

        assertEquals(100, result.getX(), 0.001, "X should remain 100");
        assertEquals(GameConstants.ARENA_SIZE_Y, result.getY(), 0.001, "Y should be clamped to ARENA_SIZE_Y");
    }

    @Test
    public void testCheckUnitCollision_UnitsColliding() {
        Position position1 = new Position(0, 0);
        Position position2 = new Position(10, 0);
        float radius1 = 5;
        float radius2 = 5;

        boolean result = calculator.checkUnitCollision(position1, position2, radius1, radius2);

        assertTrue(result, "Units should be colliding");
    }

    @Test
    public void testCheckUnitCollision_UnitsTouching() {
        Position position1 = new Position(0, 0);
        Position position2 = new Position(20, 0);
        float radius1 = 10;
        float radius2 = 10;

        boolean result = calculator.checkUnitCollision(position1, position2, radius1, radius2);

        assertTrue(result, "Units touching at edges should count as collision");
    }

    @Test
    public void testCheckUnitCollision_UnitsNotColliding() {
        Position position1 = new Position(0, 0);
        Position position2 = new Position(100, 0);
        float radius1 = 10;
        float radius2 = 10;

        boolean result = calculator.checkUnitCollision(position1, position2, radius1, radius2);

        assertFalse(result, "Units should not be colliding");
    }

    @Test
    public void testCheckUnitCollision_DiagonalDistance() {
        Position position1 = new Position(0, 0);
        Position position2 = new Position(30, 40);
        float radius1 = 25;
        float radius2 = 25;

        boolean result = calculator.checkUnitCollision(position1, position2, radius1, radius2);

        assertTrue(result, "Units should be colliding with diagonal distance 50");
    }

    @Test
    public void testResolveCollision_OverlappingUnits() {
        Position position1 = new Position(10, 0);
        Position position2 = new Position(0, 0);
        float radius1 = 10;
        float radius2 = 10;

        Position result = calculator.resolveCollision(position1, position2, radius1, radius2);

        // distance = 10, overlap = 20 - 10 = 10
        // xOverlap = 10 * (10 - 0) / 10 = 10
        // result = (10 - 10, 0 - 0) = (0, 0)
        assertEquals(0, result.getX(), 0.001, "X should be 0 after collision resolution");
        assertEquals(0, result.getY(), 0.001, "Y should remain 0");
    }

    @Test
    public void testResolveCollision_PartialOverlap() {
        Position position1 = new Position(5, 0);
        Position position2 = new Position(0, 0);
        float radius1 = 10;
        float radius2 = 10;

        Position result = calculator.resolveCollision(position1, position2, radius1, radius2);

        // distance = 5, overlap = 20 - 5 = 15
        // xOverlap = 15 * (5 - 0) / 5 = 15
        // result = (5 - 15, 0 - 0) = (-10, 0)
        assertEquals(-10, result.getX(), 0.001, "X should be -10 after collision resolution");
        assertEquals(0, result.getY(), 0.001, "Y should remain 0");
    }

    @Test
    public void testResolveCollision_DiagonalCollision() {
        Position position1 = new Position(1, 1);
        Position position2 = new Position(0, 0);
        float radius1 = 5;
        float radius2 = 5;

        Position result = calculator.resolveCollision(position1, position2, radius1, radius2);

        // distance = sqrt(2) ≈ 1.414
        // overlap = 10 - sqrt(2) ≈ 8.586
        // xOverlap = 8.586 * 1 / 1.414 ≈ 6.07
        // yOverlap = 8.586 * 1 / 1.414 ≈ 6.07
        // result = (1 - 6.07, 1 - 6.07) ≈ (-5.07, -5.07)
        assertEquals(-5.071, result.getX(), 0.01, "X should be approximately -5.071");
        assertEquals(-5.071, result.getY(), 0.01, "Y should be approximately -5.071");
    }

    @Test
    public void testResolveCollision_NoOverlap() {
        Position position1 = new Position(100, 0);
        Position position2 = new Position(0, 0);
        float radius1 = 10;
        float radius2 = 10;

        Position result = calculator.resolveCollision(position1, position2, radius1, radius2);

        // distance = 100, overlap = 20 - 100 = -80 (no actual overlap)
        // xOverlap = -80 * 100 / 100 = -80
        // result = (100 - (-80), 0 - 0) = (180, 0)
        assertEquals(180, result.getX(), 0.001, "X should be 180");
        assertEquals(0, result.getY(), 0.001, "Y should remain 0");
    }
}
