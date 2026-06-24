package io.vitals.game.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MovementTypeTest {
    @Test
    @DisplayName("MovementType enum contains all expected values")
    public void testMovementTypes() {
        assertEquals(MovementType.STATIONARY, MovementType.valueOf("STATIONARY"), "STATIONARY should exist");
        assertEquals(MovementType.PATROL, MovementType.valueOf("PATROL"), "PATROL should exist");
        assertEquals(MovementType.CHASE, MovementType.valueOf("CHASE"), "CHASE should exist");
        assertEquals(MovementType.FLEE, MovementType.valueOf("FLEE"), "FLEE should exist");
    }
}
