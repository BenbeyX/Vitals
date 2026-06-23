package io.vitals.game;

import io.vitals.game.model.Player;
import io.vitals.game.model.Position;
import io.vitals.game.model.Velocity;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameContextTest {
    @Test
    public void testGameContextPlayer() {
        Player player = new Player(new Position(400, 300), new Velocity(0, 0));
        GameContext context = new GameContext(player);
        assertEquals(400, context.getPlayer().getPosition().getX(), "Player X should be 400");
        assertEquals(300, context.getPlayer().getPosition().getY(), "Player Y should be 300");
    }

    @Test
    public void testGameContextMovePlayer() {
        Player player = new Player(new Position(0, 0), new Velocity(0, 0));
        GameContext context = new GameContext(player);
        context.movePlayerTo(new Position(500, 400));
        assertEquals(500, context.getPlayer().getPosition().getX(), "Player X should be 500");
        assertEquals(400, context.getPlayer().getPosition().getY(), "Player Y should be 400");
    }
}
