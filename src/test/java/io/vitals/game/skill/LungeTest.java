package io.vitals.game.skill;

import io.vitals.game.model.Player;
import io.vitals.game.model.Position;
import io.vitals.game.model.Velocity;
import io.vitals.game.skill.Lunge;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LungeTest {
    @Test
    public void testLungeProperties() {
        Lunge lunge = new Lunge();
        assertEquals("Lunge", lunge.getName(), "Name should be Lunge");
        assertEquals(5.0, lunge.getCooldownDuration(), "Cooldown should be 5.0");
        assertEquals(150.0, lunge.getRange(), "Range should be 150.0");
    }
}
