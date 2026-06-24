package io.vitals.game.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReactionTypeTest {
    @Test
    @DisplayName("ReactionType enum contains all expected values")
    public void testReactionTypes() {
        assertEquals(ReactionType.NONE, ReactionType.valueOf("NONE"), "NONE should exist");
        assertEquals(ReactionType.RETALIATE, ReactionType.valueOf("RETALIATE"), "RETALIATE should exist");
        assertEquals(ReactionType.DODGE, ReactionType.valueOf("DODGE"), "DODGE should exist");
        assertEquals(ReactionType.PROXIMITY_TRIGGER, ReactionType.valueOf("PROXIMITY_TRIGGER"), "PROXIMITY_TRIGGER should exist");
    }
}
