package io.vitals.game.skill;

import io.vitals.game.skill.Lunge;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LungeTest {
    @Test
    @DisplayName("Lunge skill has correct name, cooldown, and range")
    public void testLungeProperties() {
        Lunge lunge = new Lunge();
        assertEquals("Lunge", lunge.getName(), "Name should be Lunge");
        assertEquals(5.0, lunge.getCooldownDuration(), "Cooldown should be 5.0");
        assertEquals(150.0, lunge.getRange(), "Range should be 150.0");
    }
}
