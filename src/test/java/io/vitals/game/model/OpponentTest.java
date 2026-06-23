package io.vitals.game.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class OpponentTest {
    @Test
    public void testOpponentPosition() {
        Opponent opponent = new Opponent(
            new Position(150, 250), new Velocity(0, 0),
            Vital.TOP, 5, 5, 0, 0, 0
        );
        assertEquals(150, opponent.getPosition().getX(), "X should be 150");
        assertEquals(250, opponent.getPosition().getY(), "Y should be 250");
    }

    @Test
    public void testOpponentVelocity() {
        Opponent opponent = new Opponent(
            new Position(0, 0), new Velocity(30, 40),
            Vital.TOP, 5, 5, 0, 0, 0
        );
        assertEquals(30, opponent.getVelocity().getX(), "X velocity should be 30");
        assertEquals(40, opponent.getVelocity().getY(), "Y velocity should be 40");
    }
}
