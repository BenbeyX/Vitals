package io.vitals.game.service;

import io.vitals.game.constants.GameConstants;
import io.vitals.game.model.MovementType;
import io.vitals.game.model.ReactionType;
import io.vitals.game.model.SessionConfig;
import io.vitals.game.result.SessionResult;
import io.vitals.game.state.SessionState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SessionManagerTest {

    private SessionManager sessionManager;
    private SessionState sessionState;

    @BeforeEach
    void setUp() {
        sessionManager = new SessionManager();
        sessionState = new SessionState();
    }

    // ==================== Timer Progression Tests ====================

    @Nested
    @DisplayName("tick() - Timer Progression")
    class TickTimerTests {

        @Test
        @DisplayName("increases elapsedTime by deltaTime")
        void tick_increasesElapsedTime() {
            float deltaTime = 0.5f;
            sessionManager.tick(sessionState, deltaTime);

            assertEquals(deltaTime, sessionState.getElapsedTime(), 0.001);
        }

        @Test
        @DisplayName("accumulates elapsed time across multiple ticks")
        void tick_multipleTicks_accumulates() {
            sessionManager.tick(sessionState, 0.3f);
            sessionManager.tick(sessionState, 0.4f);

            assertEquals(0.7, sessionState.getElapsedTime(), 0.001);
        }

        @Test
        @DisplayName("ignores zero deltaTime")
        void tick_zeroDelta_noChange() {
            sessionManager.tick(sessionState, 0.0f);

            assertEquals(0.0, sessionState.getElapsedTime(), 0.001);
        }

        @Test
        @DisplayName("ignores negative deltaTime")
        void tick_negativeDelta_ignored() {
            sessionManager.tick(sessionState, -0.5f);

            assertEquals(0.0, sessionState.getElapsedTime(), 0.001);
        }
    }

    // ==================== Q Cooldown Tests ====================

    @Nested
    @DisplayName("tick() - Q Cooldown")
    class TickQCooldownTests {

        @Test
        @DisplayName("decrements Q cooldown")
        void tick_decrementsQCooldown() {
            sessionState.setQCooldownRemaining(GameConstants.DEFAULT_Q_COOLDOWN);

            sessionManager.tick(sessionState, 1.0f);

            assertEquals(GameConstants.DEFAULT_Q_COOLDOWN - 1.0,
                    sessionState.getQCooldownRemaining(), 0.001);
        }

        @Test
        @DisplayName("does not go below zero")
        void tick_cooldownFloorAtZero() {
            sessionState.setQCooldownRemaining(0.5);

            sessionManager.tick(sessionState, 1.0f);

            assertEquals(0.0, sessionState.getQCooldownRemaining(), 0.001);
        }

        @Test
        @DisplayName("no change when cooldown already zero")
        void tick_noCooldown_noChange() {
            sessionManager.tick(sessionState, 1.0f);

            assertEquals(0.0, sessionState.getQCooldownRemaining(), 0.001);
        }
    }

    // ==================== Speed Boost Tests ====================

    @Nested
    @DisplayName("tick() - Speed Boost")
    class TickSpeedBoostTests {

        @Test
        @DisplayName("decrements speed boost remaining")
        void tick_decrementsSpeedBoost() {
            sessionState.setSpeedBoostActive(true);
            sessionState.setSpeedBoostRemaining(GameConstants.SPEED_BOOST_DURATION);

            sessionManager.tick(sessionState, 0.5f);

            assertEquals(GameConstants.SPEED_BOOST_DURATION - 0.5,
                    sessionState.getSpeedBoostRemaining(), 0.001);
            assertTrue(sessionState.isSpeedBoostActive());
        }

        @Test
        @DisplayName("deactivates speed boost when remaining reaches zero")
        void tick_speedBoostExpires_deactivates() {
            sessionState.setSpeedBoostActive(true);
            sessionState.setSpeedBoostRemaining(GameConstants.SPEED_BOOST_DURATION);

            sessionManager.tick(sessionState, (float) GameConstants.SPEED_BOOST_DURATION);

            assertEquals(0.0, sessionState.getSpeedBoostRemaining(), 0.001);
            assertFalse(sessionState.isSpeedBoostActive());
        }

        @Test
        @DisplayName("handles overflow - sets remaining to zero and deactivates")
        void tick_speedBoostOverflow_clampsToZero() {
            sessionState.setSpeedBoostActive(true);
            sessionState.setSpeedBoostRemaining(GameConstants.SPEED_BOOST_DURATION);

            sessionManager.tick(sessionState, 5.0f);

            assertEquals(0.0, sessionState.getSpeedBoostRemaining(), 0.001);
            assertFalse(sessionState.isSpeedBoostActive());
        }

        @Test
        @DisplayName("no change when speed boost not active")
        void tick_noSpeedBoost_noChange() {
            sessionManager.tick(sessionState, 1.0f);

            assertFalse(sessionState.isSpeedBoostActive());
            assertEquals(0.0, sessionState.getSpeedBoostRemaining(), 0.001);
        }
    }

    // ==================== recordVitalHit Tests ====================

    @Nested
    @DisplayName("recordVitalHit()")
    class RecordVitalHitTests {

        @Test
        @DisplayName("increments vitalsHit count")
        void recordVitalHit_incrementsCount() {
            sessionManager.recordVitalHit(sessionState);

            assertEquals(1, sessionState.getVitalsHit());
        }

        @Test
        @DisplayName("increments vitalsHit count multiple times")
        void recordVitalHit_multipleHits() {
            sessionManager.recordVitalHit(sessionState);
            sessionManager.recordVitalHit(sessionState);
            sessionManager.recordVitalHit(sessionState);

            assertEquals(3, sessionState.getVitalsHit());
        }

        @Test
        @DisplayName("adds VITAL_HIT_SCORE to score")
        void recordVitalHit_addsScore() {
            sessionManager.recordVitalHit(sessionState);

            assertEquals(GameConstants.VITAL_HIT_SCORE, sessionState.getScore());
        }

        @Test
        @DisplayName("accumulates score across multiple hits")
        void recordVitalHit_accumulatesScore() {
            sessionManager.recordVitalHit(sessionState);
            sessionManager.recordVitalHit(sessionState);

            assertEquals(GameConstants.VITAL_HIT_SCORE * 2, sessionState.getScore());
        }

        @Test
        @DisplayName("refreshes speed boost duration")
        void recordVitalHit_refreshesSpeedBoost() {
            sessionState.setSpeedBoostRemaining(0.5);

            sessionManager.recordVitalHit(sessionState);

            assertEquals(GameConstants.SPEED_BOOST_DURATION,
                    sessionState.getSpeedBoostRemaining(), 0.001);
            assertTrue(sessionState.isSpeedBoostActive());
        }

        @Test
        @DisplayName("activates speed boost when not previously active")
        void recordVitalHit_activatesSpeedBoost() {
            sessionManager.recordVitalHit(sessionState);

            assertTrue(sessionState.isSpeedBoostActive());
            assertEquals(GameConstants.SPEED_BOOST_DURATION,
                    sessionState.getSpeedBoostRemaining(), 0.001);
        }
    }

    // ==================== recordOpponentSlain Tests ====================

    @Nested
    @DisplayName("recordOpponentSlain()")
    class RecordOpponentSlainTests {

        @Test
        @DisplayName("increments opponentsSlain count")
        void recordOpponentSlain_incrementsCount() {
            sessionManager.recordOpponentSlain(sessionState);

            assertEquals(1, sessionState.getOpponentsSlain());
        }

        @Test
        @DisplayName("increments count multiple times")
        void recordOpponentSlain_multipleSlays() {
            sessionManager.recordOpponentSlain(sessionState);
            sessionManager.recordOpponentSlain(sessionState);

            assertEquals(2, sessionState.getOpponentsSlain());
        }

        @Test
        @DisplayName("adds OPPONENT_SLAIN_SCORE to score")
        void recordOpponentSlain_addsScore() {
            sessionManager.recordOpponentSlain(sessionState);

            assertEquals(GameConstants.OPPONENT_SLAIN_SCORE, sessionState.getScore());
        }

        @Test
        @DisplayName("accumulates score across multiple slays")
        void recordOpponentSlain_accumulatesScore() {
            sessionManager.recordOpponentSlain(sessionState);
            sessionManager.recordOpponentSlain(sessionState);

            assertEquals(GameConstants.OPPONENT_SLAIN_SCORE * 2, sessionState.getScore());
        }
    }

    // ==================== recordWhiff Tests ====================

    @Nested
    @DisplayName("recordWhiff()")
    class RecordWhiffTests {

        @Test
        @DisplayName("increments whiffs count")
        void recordWhiff_incrementsCount() {
            sessionManager.recordWhiff(sessionState);

            assertEquals(1, sessionState.getWhiffs());
        }

        @Test
        @DisplayName("increments count multiple times")
        void recordWhiff_multipleWhiffs() {
            sessionManager.recordWhiff(sessionState);
            sessionManager.recordWhiff(sessionState);
            sessionManager.recordWhiff(sessionState);

            assertEquals(3, sessionState.getWhiffs());
        }

        @Test
        @DisplayName("does not change score")
        void recordWhiff_noScoreChange() {
            sessionManager.recordWhiff(sessionState);

            assertEquals(0, sessionState.getScore());
        }
    }

    // ==================== recordNonVitalHit Tests ====================

    @Nested
    @DisplayName("recordNonVitalHit()")
    class RecordNonVitalHitTests {

        @Test
        @DisplayName("increments nonVitalHits count")
        void recordNonVitalHit_incrementsCount() {
            sessionManager.recordNonVitalHit(sessionState);

            assertEquals(1, sessionState.getNonVitalHits());
        }

        @Test
        @DisplayName("increments count multiple times")
        void recordNonVitalHit_multipleHits() {
            sessionManager.recordNonVitalHit(sessionState);
            sessionManager.recordNonVitalHit(sessionState);

            assertEquals(2, sessionState.getNonVitalHits());
        }

        @Test
        @DisplayName("does not change score")
        void recordNonVitalHit_noScoreChange() {
            sessionManager.recordNonVitalHit(sessionState);

            assertEquals(0, sessionState.getScore());
        }
    }

    // ==================== isSessionComplete Tests ====================

    @Nested
    @DisplayName("isSessionComplete()")
    class IsSessionCompleteTests {

        private SessionConfig createConfig(int durationSeconds) {
            return new SessionConfig(durationSeconds, 3, GameConstants.DEFAULT_Q_COOLDOWN,
                    MovementType.CHASE, ReactionType.DODGE, GameConstants.DEFAULT_HP);
        }

        @Test
        @DisplayName("returns false at session start")
        void isSessionComplete_atStart_returnsFalse() {
            SessionConfig config = createConfig(120);

            assertFalse(sessionManager.isSessionComplete(sessionState, config));
        }

        @Test
        @DisplayName("returns false during session")
        void isSessionComplete_duringSession_returnsFalse() {
            sessionState.setElapsedTime(60.0);
            SessionConfig config = createConfig(120);

            assertFalse(sessionManager.isSessionComplete(sessionState, config));
        }

        @Test
        @DisplayName("returns true exactly at duration")
        void isSessionComplete_exactlyAtDuration_returnsTrue() {
            sessionState.setElapsedTime(120.0);
            SessionConfig config = createConfig(120);

            assertTrue(sessionManager.isSessionComplete(sessionState, config));
        }

        @Test
        @DisplayName("returns true past duration")
        void isSessionComplete_pastDuration_returnsTrue() {
            sessionState.setElapsedTime(120.5);
            SessionConfig config = createConfig(120);

            assertTrue(sessionManager.isSessionComplete(sessionState, config));
        }

        @Test
        @DisplayName("returns false just before duration")
        void isSessionComplete_justBeforeDuration_returnsFalse() {
            sessionState.setElapsedTime(119.9999);
            SessionConfig config = createConfig(120);

            assertFalse(sessionManager.isSessionComplete(sessionState, config));
        }
    }

    // ==================== getResults Tests ====================

    @Nested
    @DisplayName("getResults()")
    class GetResultsTests {

        @Test
        @DisplayName("returns all tracked statistics")
        void getResults_returnsAllStats() {
            sessionState.setScore(70);
            sessionState.setVitalsHit(2);
            sessionState.setOpponentsSlain(1);
            sessionState.setNonVitalHits(3);
            sessionState.setWhiffs(1);
            sessionState.setElapsedTime(45.5);

            SessionResult result = sessionManager.getResults(sessionState);

            assertEquals(70, result.getScore());
            assertEquals(2, result.getVitalsHit());
            assertEquals(1, result.getOpponentsSlain());
            assertEquals(3, result.getNonVitalHits());
            assertEquals(1, result.getWhiffs());
            assertEquals(45.5, result.getElapsedTime(), 0.001);
        }

        @Test
        @DisplayName("calculates accuracy correctly")
        void getResults_calculatesAccuracy() {
            sessionState.setVitalsHit(5);
            sessionState.setNonVitalHits(2);
            sessionState.setWhiffs(3);

            SessionResult result = sessionManager.getResults(sessionState);

            // 5 / (5 + 2 + 3) = 0.5 = 50%
            assertEquals(5.0 / 10.0, result.getAccuracy(), 0.001);
        }

        @Test
        @DisplayName("handles zero accuracy (no hits)")
        void getResults_zeroAccuracy() {
            sessionState.setVitalsHit(0);
            sessionState.setNonVitalHits(0);
            sessionState.setWhiffs(0);

            SessionResult result = sessionManager.getResults(sessionState);

            assertEquals(0.0, result.getAccuracy(), 0.001);
        }

        @Test
        @DisplayName("handles perfect accuracy")
        void getResults_perfectAccuracy() {
            sessionState.setVitalsHit(10);
            sessionState.setNonVitalHits(0);
            sessionState.setWhiffs(0);

            SessionResult result = sessionManager.getResults(sessionState);

            assertEquals(1.0, result.getAccuracy(), 0.001);
        }
    }

    // ==================== Integration Tests ====================

    @Nested
    @DisplayName("Integration - Full Session Flow")
    class IntegrationTests {

        @Test
        @DisplayName("simulates a typical session with mixed outcomes")
        void fullSessionFlow() {
            SessionConfig config = new SessionConfig(60, 3, GameConstants.DEFAULT_Q_COOLDOWN,
                    MovementType.CHASE, ReactionType.DODGE, GameConstants.DEFAULT_HP);

            // Hit 3 vitals
            sessionManager.recordVitalHit(sessionState);
            sessionManager.recordVitalHit(sessionState);
            sessionManager.recordVitalHit(sessionState);

            // Kill 2 opponents
            sessionManager.recordOpponentSlain(sessionState);
            sessionManager.recordOpponentSlain(sessionState);

            // Miss a few times
            sessionManager.recordWhiff(sessionState);
            sessionManager.recordWhiff(sessionState);

            // Hit some non-vitals
            sessionManager.recordNonVitalHit(sessionState);
            sessionManager.recordNonVitalHit(sessionState);

            // Tick time to end of session
            sessionState.setElapsedTime(60.0);

            assertTrue(sessionManager.isSessionComplete(sessionState, config));

            SessionResult result = sessionManager.getResults(sessionState);
            int expectedScore = (GameConstants.VITAL_HIT_SCORE * 3) +
                               (GameConstants.OPPONENT_SLAIN_SCORE * 2);
            assertEquals(expectedScore, result.getScore());
            assertEquals(3, result.getVitalsHit());
            assertEquals(2, result.getOpponentsSlain());
            assertEquals(2, result.getWhiffs());
            assertEquals(2, result.getNonVitalHits());
            // Accuracy: 3 / (3 + 2 + 2) = 3/7
            assertEquals(3.0 / 7.0, result.getAccuracy(), 0.001);
        }
    }
}
