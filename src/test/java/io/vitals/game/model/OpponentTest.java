package io.vitals.game.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

public class OpponentTest {
    @Test
    @DisplayName("Opponent position is set correctly")
    public void testOpponentPosition() {
        Opponent opponent = new Opponent(
            new Position(150, 250), new Velocity(0, 0),
            Vital.TOP, 5, 5, 0, 0, 0
        );
        assertEquals(150, opponent.getPosition().getX(), "X should be 150");
        assertEquals(250, opponent.getPosition().getY(), "Y should be 250");
    }

    @Test
    @DisplayName("Opponent velocity is set correctly")
    public void testOpponentVelocity() {
        Opponent opponent = new Opponent(
            new Position(0, 0), new Velocity(30, 40),
            Vital.TOP, 5, 5, 0, 0, 0
        );
        assertEquals(30, opponent.getVelocity().getX(), "X velocity should be 30");
        assertEquals(40, opponent.getVelocity().getY(), "Y velocity should be 40");
    }

    @Test
    @DisplayName("Opponent vital is set correctly")
    public void testOpponentVital() {
        Opponent opponent = new Opponent(
            new Position(0, 0), new Velocity(0, 0),
            Vital.RIGHT, 5, 5, 0, 0, 0
        );
        assertEquals(Vital.RIGHT, opponent.getVital());
    }

    @Test
    @DisplayName("Opponent hp and maxHp are set correctly")
    public void testOpponentHp() {
        Opponent opponent = new Opponent(
            new Position(0, 0), new Velocity(0, 0),
            Vital.TOP, 3, 5, 0, 0, 0
        );
        assertEquals(3, opponent.getHp());
        assertEquals(5, opponent.getMaxHp());
    }

    @Test
    @DisplayName("Opponent setHp updates hp value")
    public void testOpponentSetHp() {
        Opponent opponent = new Opponent(
            new Position(0, 0), new Velocity(0, 0),
            Vital.TOP, 5, 5, 0, 0, 0
        );
        opponent.setHp(3);
        assertEquals(3, opponent.getHp());
    }

    @Test
    @DisplayName("Opponent setHp does not go below zero")
    public void testOpponentSetHp_doesNotGoBelowZero() {
        Opponent opponent = new Opponent(
            new Position(0, 0), new Velocity(0, 0),
            Vital.TOP, 1, 5, 0, 0, 0
        );
        opponent.setHp(-5);
        assertEquals(0, opponent.getHp());
    }

    @Test
    @DisplayName("isDefeated returns false when hp is above zero")
    public void testIsDefeated_hpAboveZero_returnsFalse() {
        Opponent opponent = new Opponent(
            new Position(0, 0), new Velocity(0, 0),
            Vital.TOP, 3, 5, 0, 0, 0
        );
        assertFalse(opponent.isDefeated());
    }

    @Test
    @DisplayName("isDefeated returns true when hp is zero")
    public void testIsDefeated_hpZero_returnsTrue() {
        Opponent opponent = new Opponent(
            new Position(0, 0), new Velocity(0, 0),
            Vital.TOP, 0, 5, 0, 0, 0
        );
        assertTrue(opponent.isDefeated());
    }

    @Test
    @DisplayName("isDefeated returns true after taking fatal damage")
    public void testIsDefeated_afterFatalDamage_returnsTrue() {
        Opponent opponent = new Opponent(
            new Position(0, 0), new Velocity(0, 0),
            Vital.TOP, 1, 5, 0, 0, 0
        );
        opponent.setHp(0);
        assertTrue(opponent.isDefeated());
    }
}
