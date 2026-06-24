package io.vitals.game.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerTest {
    @Test
    @DisplayName("Player position is set correctly")
    public void testPlayerPosition() {
        Player player = new Player(new Position(100, 200), new Velocity(0, 0));
        assertEquals(100, player.getPosition().getX(), "X should be 100");
        assertEquals(200, player.getPosition().getY(), "Y should be 200");
    }

    @Test
    @DisplayName("Player velocity is set correctly")
    public void testPlayerVelocity() {
        Player player = new Player(new Position(0, 0), new Velocity(50, 75));
        assertEquals(50, player.getVelocity().getX(), "X velocity should be 50");
        assertEquals(75, player.getVelocity().getY(), "Y velocity should be 75");
    }

    @Test
    @DisplayName("Player position can be updated")
    public void testPlayerSetPosition() {
        Player player = new Player(new Position(0, 0), new Velocity(0, 0));
        player.setPosition(new Position(300, 400));
        assertEquals(300, player.getPosition().getX(), "X should be 300");
        assertEquals(400, player.getPosition().getY(), "Y should be 400");
    }
}
