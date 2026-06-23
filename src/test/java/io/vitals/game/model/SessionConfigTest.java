package io.vitals.game.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SessionConfigTest {
    @Test
    public void testSessionConfig() {
        SessionConfig config = new SessionConfig(120, 3, 5.0, MovementType.CHASE, ReactionType.DODGE, 5);
        assertEquals(120, config.getDurationSeconds(), "Duration should be 120");
        assertEquals(3, config.getOpponentCount(), "Opponent count should be 3");
        assertEquals(5.0, config.getQCooldownSeconds(), "Q cooldown should be 5.0");
        assertEquals(MovementType.CHASE, config.getDefaultMovementType(), "Movement type should be CHASE");
        assertEquals(ReactionType.DODGE, config.getDefaultReactionType(), "Reaction type should be DODGE");
        assertEquals(5, config.getDefaultHp(), "Default HP should be 5");
    }
}
