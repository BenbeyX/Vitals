package io.vitals.game.service;

import io.vitals.game.constants.GameConstants;
import io.vitals.game.model.*;
import io.vitals.game.model.message.*;
import io.vitals.game.result.SessionResult;
import io.vitals.game.state.SessionState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameSessionTest {

    private GameSession gameSession;
    private SessionConfig config;

    @BeforeEach
    void setUp() {
        config = createDefaultConfig();
        gameSession = new GameSession(config);
    }

    // ==================== Initialization Tests ====================

    @Nested
    @DisplayName("initialization")
    class InitializationTests {

        @Test
        @DisplayName("creates session with correct player position")
        void createsSession_withCorrectPlayerPosition() {
            Player player = gameSession.getPlayer();

            assertNotNull(player);
            assertEquals(GameConstants.ARENA_SIZE_X / 2.0, player.getPosition().getX(), 0.1);
            assertEquals(GameConstants.ARENA_SIZE_Y / 2.0, player.getPosition().getY(), 0.1);
        }

        @Test
        @DisplayName("creates session with correct number of opponents")
        void createsSession_withCorrectOpponentCount() {
            List<Opponent> opponents = gameSession.getOpponents();

            assertEquals(config.getOpponentCount(), opponents.size());
        }

        @Test
        @DisplayName("creates opponents with default movement type from config")
        void createsOpponents_withDefaultMovementType() {
            List<Opponent> opponents = gameSession.getOpponents();

            for (Opponent opponent : opponents) {
                assertEquals(config.getDefaultMovementType(), opponent.getMovementType());
            }
        }

        @Test
        @DisplayName("creates opponents with default reaction type from config")
        void createsOpponents_withDefaultReactionType() {
            List<Opponent> opponents = gameSession.getOpponents();

            for (Opponent opponent : opponents) {
                assertEquals(config.getDefaultReactionType(), opponent.getReactionType());
            }
        }

        @Test
        @DisplayName("creates opponents with default HP from config")
        void createsOpponents_withDefaultHp() {
            List<Opponent> opponents = gameSession.getOpponents();

            for (Opponent opponent : opponents) {
                assertEquals(config.getDefaultHp(), opponent.getHp());
            }
        }

        @Test
        @DisplayName("creates session with fresh session state")
        void createsSession_withFreshSessionState() {
            SessionState state = gameSession.getState();

            assertNotNull(state);
            assertEquals(0, state.getElapsedTime(), 0.001);
            assertEquals(0, state.getScore());
            assertEquals(0, state.getVitalsHit());
        }

        @Test
        @DisplayName("creates session with vital manager")
        void createsSession_withVitalManager() {
            VitalManager vitalManager = gameSession.getVitalManager();

            assertNotNull(vitalManager);
        }

        @Test
        @DisplayName("session starts in running state")
        void sessionStarts_running() {
            assertFalse(gameSession.isComplete());
            assertFalse(gameSession.isAborted());
        }
    }

    // ==================== State Access Tests ====================

    @Nested
    @DisplayName("state access")
    class StateAccessTests {

        @Test
        @DisplayName("getSessionConfig returns the configuration")
        void getConfig_returnsConfiguration() {
            assertEquals(config, gameSession.getConfig());
        }

        @Test
        @DisplayName("getState returns mutable session state")
        void getState_returnsMutableState() {
            SessionState state = gameSession.getState();
            state.setScore(100);

            assertEquals(100, gameSession.getState().getScore());
        }
    }

    // ==================== Abort Tests ====================

    @Nested
    @DisplayName("abort")
    class AbortTests {

        @Test
        @DisplayName("abort marks session as aborted")
        void abort_marksAsAborted() {
            gameSession.abort();

            assertTrue(gameSession.isAborted());
        }

        @Test
        @DisplayName("abort marks session as complete")
        void abort_marksAsComplete() {
            gameSession.abort();

            assertTrue(gameSession.isComplete());
        }

        @Test
        @DisplayName("isAborted returns false before abort")
        void isAborted_falseBeforeAbort() {
            assertFalse(gameSession.isAborted());
        }
    }

    // ==================== Helper Methods ====================

    private SessionConfig createDefaultConfig() {
        return new SessionConfig(
            60,                    // durationSeconds
            3,                     // opponentCount
            5.0,                   // qCooldownSeconds
            MovementType.STATIONARY,
            ReactionType.NONE,
            5                      // defaultHp
        );
    }
}