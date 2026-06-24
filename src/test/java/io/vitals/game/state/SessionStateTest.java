package io.vitals.game.state;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SessionStateTest {
    @Test
    @DisplayName("SessionState initializes with default values")
    public void testSessionStateInitialValues() {
        SessionState state = new SessionState();
        assertEquals(0, state.getElapsedTime(), "Elapsed time should be 0");
        assertEquals(0, state.getScore(), "Score should be 0");
        assertEquals(0, state.getVitalsHit(), "Vitals hit should be 0");
        assertEquals(0, state.getWhiffs(), "Whiffs should be 0");
        assertEquals(0, state.getNonVitalHits(), "Non-vital hits should be 0");
        assertEquals(0, state.getQCooldownRemaining(), "Q cooldown should be 0");
        assertFalse(state.isSpeedBoostActive(), "Speed boost should not be active");
    }

    @Test
    @DisplayName("SessionState setters update values correctly")
    public void testSessionStateSetters() {
        SessionState state = new SessionState();
        state.setElapsedTime(45.5);
        state.setScore(10);
        state.setVitalsHit(8);
        state.setWhiffs(2);
        state.setNonVitalHits(1);
        state.setQCooldownRemaining(3.2);
        state.setSpeedBoostActive(true);

        assertEquals(45.5, state.getElapsedTime(), "Elapsed time should be 45.5");
        assertEquals(10, state.getScore(), "Score should be 10");
        assertEquals(8, state.getVitalsHit(), "Vitals hit should be 8");
        assertEquals(2, state.getWhiffs(), "Whiffs should be 2");
        assertEquals(1, state.getNonVitalHits(), "Non-vital hits should be 1");
        assertEquals(3.2, state.getQCooldownRemaining(), "Q cooldown should be 3.2");
        assertTrue(state.isSpeedBoostActive(), "Speed boost should be active");
    }
}
