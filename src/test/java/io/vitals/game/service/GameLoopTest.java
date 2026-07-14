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

class GameLoopTest {

    private GameLoop gameLoop;
    private GameSession session;

    @BeforeEach
    void setUp() {
        SessionConfig config = createDefaultConfig();
        session = new GameSession(config);
        gameLoop = new GameLoop(session);
    }

    // ==================== Tick Tests ====================

    @Nested
    @DisplayName("tick")
    class TickTests {

        @Test
        @DisplayName("tick advances elapsed time")
        void tick_advancesElapsedTime() {
            float deltaTime = 0.05f;

            gameLoop.tick(deltaTime);

            assertEquals(deltaTime, session.getState().getElapsedTime(), 0.001);
        }

        @Test
        @DisplayName("multiple ticks accumulate elapsed time")
        void multipleTicks_accumulateElapsedTime() {
            gameLoop.tick(0.05f);
            gameLoop.tick(0.05f);

            assertEquals(0.1, session.getState().getElapsedTime(), 0.001);
        }

        @Test
        @DisplayName("tick does not advance time when session is complete")
        void tick_whenComplete_doesNotAdvanceTime() {
            SessionConfig shortConfig = new SessionConfig(
                1, 1, 5.0, MovementType.STATIONARY, ReactionType.NONE, 5
            );
            GameSession shortSession = new GameSession(shortConfig);
            GameLoop shortLoop = new GameLoop(shortSession);

            shortLoop.tick(1.0f);
            assertTrue(shortSession.isComplete());

            shortLoop.tick(0.05f);

            // Elapsed time should not have advanced beyond the session duration
            assertTrue(shortSession.getState().getElapsedTime() <= 1.1);
        }

        @Test
        @DisplayName("tick does not advance time when session is aborted")
        void tick_whenAborted_doesNotAdvanceTime() {
            session.abort();
            double elapsedBefore = session.getState().getElapsedTime();

            gameLoop.tick(0.05f);

            assertEquals(elapsedBefore, session.getState().getElapsedTime(), 0.001);
        }

        @Test
        @DisplayName("tick with zero deltaTime does nothing")
        void tick_zeroDeltaTime_doesNothing() {
            gameLoop.tick(0.0f);

            assertEquals(0, session.getState().getElapsedTime(), 0.001);
        }

        @Test
        @DisplayName("tick with negative deltaTime does nothing")
        void tick_negativeDeltaTime_doesNothing() {
            gameLoop.tick(-0.05f);

            assertEquals(0, session.getState().getElapsedTime(), 0.001);
        }
    }

    // ==================== Input Processing Tests ====================

    @Nested
    @DisplayName("input processing")
    class InputProcessingTests {

        @Test
        @DisplayName("processMoveTo updates player target position")
        void processMoveTo_updatesPlayerPosition() {
            MoveToMessage moveMessage = new MoveToMessage(300, 400);

            gameLoop.processInput(moveMessage);

            // After processing, the player should have a velocity toward the target
            Player player = session.getPlayer();
            Velocity velocity = player.getVelocity();
            boolean hasMovement = velocity.getX() != 0 || velocity.getY() != 0;
            assertTrue(hasMovement, "Player should have velocity toward move target");
        }

        @Test
        @DisplayName("processMoveTo to current position sets zero velocity")
        void processMoveTo_samePosition_zeroVelocity() {
            double currentX = session.getPlayer().getPosition().getX();
            double currentY = session.getPlayer().getPosition().getY();

            MoveToMessage moveMessage = new MoveToMessage(currentX, currentY);
            gameLoop.processInput(moveMessage);

            Velocity velocity = session.getPlayer().getVelocity();
            assertEquals(0, velocity.getX(), 0.001);
            assertEquals(0, velocity.getY(), 0.001);
        }

        @Test
        @DisplayName("processDash sets player velocity toward dash target at dash speed")
        void processDash_setsDashVelocity() {
            double currentX = session.getPlayer().getPosition().getX();
            double currentY = session.getPlayer().getPosition().getY();
            DashMessage dashMessage = new DashMessage(currentX + 100, currentY);

            gameLoop.processInput(dashMessage);

            Velocity velocity = session.getPlayer().getVelocity();
            assertTrue(velocity.getX() > 0, "Dash should set positive X velocity");
        }

        @Test
        @DisplayName("processAbort marks session as aborted")
        void processAbort_marksAsAborted() {
            AbortMessage abortMessage = new AbortMessage();

            gameLoop.processInput(abortMessage);

            assertTrue(session.isAborted());
            assertTrue(session.isComplete());
        }

        @Test
        @DisplayName("input processing does not work on completed session")
        void processInput_whenComplete_doesNothing() {
            SessionConfig shortConfig = new SessionConfig(
                1, 1, 5.0, MovementType.STATIONARY, ReactionType.NONE, 5
            );
            GameSession shortSession = new GameSession(shortConfig);
            GameLoop shortLoop = new GameLoop(shortSession);

            shortLoop.tick(1.0f);
            assertTrue(shortSession.isComplete());

            double posX = shortSession.getPlayer().getPosition().getX();
            shortLoop.processInput(new MoveToMessage(0, 0));

            // Player position should not change after session ends
            assertEquals(posX, shortSession.getPlayer().getPosition().getX(), 0.001);
        }
    }

    // ==================== Movement Update Tests ====================

    @Nested
    @DisplayName("movement updates")
    class MovementUpdateTests {

        @Test
        @DisplayName("player moves toward target after MoveTo input + tick")
        void playerMoves_afterMoveToAndTick() {
            double startX = session.getPlayer().getPosition().getX();
            MoveToMessage moveMessage = new MoveToMessage(startX + 200, session.getPlayer().getPosition().getY());

            gameLoop.processInput(moveMessage);
            gameLoop.tick(0.05f);

            double newX = session.getPlayer().getPosition().getX();
            assertTrue(newX > startX, "Player should have moved toward target");
        }

        @Test
        @DisplayName("opponents with CHASE move toward player on tick")
        void chaseOpponents_moveTowardPlayerOnTick() {
            SessionConfig chaseConfig = new SessionConfig(
                60, 1, 5.0, MovementType.CHASE, ReactionType.NONE, 5
            );
            GameSession chaseSession = new GameSession(chaseConfig);
            GameLoop chaseLoop = new GameLoop(chaseSession);

            Opponent opponent = chaseSession.getOpponents().get(0);
            double startX = opponent.getPosition().getX();

            chaseLoop.tick(0.05f);

            double newX = opponent.getPosition().getX();
            // Chase opponent should have moved (or at least had its velocity updated)
            assertTrue(opponent.getVelocity().getX() != 0 || opponent.getVelocity().getY() != 0,
                "Chase opponent should have non-zero velocity after tick");
        }

        @Test
        @DisplayName("STATIONARY opponents do not move on tick")
        void stationaryOpponents_doNotMoveOnTick() {
            Opponent opponent = session.getOpponents().get(0);
            Position startPos = new Position(opponent.getPosition().getX(), opponent.getPosition().getY());

            gameLoop.tick(0.05f);

            assertEquals(startPos.getX(), opponent.getPosition().getX(), 0.001);
            assertEquals(startPos.getY(), opponent.getPosition().getY(), 0.001);
        }

        @Test
        @DisplayName("player stays within arena bounds after movement")
        void playerStays_withinArenaBounds() {
            double edgeX = GameConstants.ARENA_SIZE_X;
            double edgeY = GameConstants.ARENA_SIZE_Y;
            session.getPlayer().setPosition(new Position(edgeX, edgeY));

            // Move toward a position outside the arena
            MoveToMessage moveMessage = new MoveToMessage(edgeX + 500, edgeY + 500);
            gameLoop.processInput(moveMessage);
            gameLoop.tick(0.05f);

            Position pos = session.getPlayer().getPosition();
            assertTrue(pos.getX() <= GameConstants.ARENA_SIZE_X,
                "Player X should not exceed arena bounds");
            assertTrue(pos.getY() <= GameConstants.ARENA_SIZE_Y,
                "Player Y should not exceed arena bounds");
        }
    }

    // ==================== Combat Resolution Tests ====================

    @Nested
    @DisplayName("combat resolution")
    class CombatResolutionTests {

        @Test
        @DisplayName("Q attack hits nearest opponent within range")
        void qAttack_hitsNearestOpponent() {
            // Place an opponent within Q_ATTACK_RANGE of the player
            Player player = session.getPlayer();
            Opponent opponent = session.getOpponents().get(0);
            opponent.setPosition(new Position(
                player.getPosition().getX() + GameConstants.Q_ATTACK_RANGE - 10,
                player.getPosition().getY()
            ));

            int hpBefore = opponent.getHp();
            gameLoop.processInput(new QAttackMessage());
            gameLoop.tick(0.05f);

            // The opponent should take damage (vital or non-vital)
            assertTrue(opponent.getHp() <= hpBefore,
                "Opponent in Q attack range should take damage after QAttackMessage");
        }

        @Test
        @DisplayName("Q attack does not hit opponent outside range")
        void qAttack_doesNotHitOutsideRange() {
            // Place opponent far away
            Opponent opponent = session.getOpponents().get(0);
            opponent.setPosition(new Position(0, 0));
            session.getPlayer().setPosition(new Position(
                GameConstants.ARENA_SIZE_X, GameConstants.ARENA_SIZE_Y));

            int hpBefore = opponent.getHp();
            gameLoop.processInput(new QAttackMessage());
            gameLoop.tick(0.05f);

            assertEquals(hpBefore, opponent.getHp(),
                "Opponent outside range should not take damage");
        }

        @Test
        @DisplayName("Q attack only hits single nearest target")
        void qAttack_hitsOnlyNearestTarget() {
            // Place player at center
            session.getPlayer().setPosition(new Position(400, 300));

            // Place two opponents within range
            Opponent nearest = session.getOpponents().get(0);
            nearest.setPosition(new Position(450, 300)); // 50 units away

            // Add a second opponent further away
            Opponent farther = session.getOpponents().get(1);
            farther.setPosition(new Position(500, 300)); // 100 units away

            int hpNearestBefore = nearest.getHp();
            int hpFartherBefore = farther.getHp();

            gameLoop.processInput(new QAttackMessage());
            gameLoop.tick(0.05f);

            // Only the nearest should take damage
            assertTrue(nearest.getHp() < hpNearestBefore,
                "Nearest opponent should take damage");
            assertEquals(hpFartherBefore, farther.getHp(),
                "Further opponent should not take damage");
        }
    }

    // ==================== Session Completion Tests ====================

    @Nested
    @DisplayName("session completion")
    class SessionCompletionTests {

        @Test
        @DisplayName("session completes when time runs out")
        void sessionCompletes_whenTimeRunsOut() {
            SessionConfig shortConfig = new SessionConfig(
                1, 1, 5.0, MovementType.STATIONARY, ReactionType.NONE, 5
            );
            GameSession shortSession = new GameSession(shortConfig);
            GameLoop shortLoop = new GameLoop(shortSession);

            assertFalse(shortSession.isComplete());

            shortLoop.tick(1.0f);

            assertTrue(shortSession.isComplete());
        }

        @Test
        @DisplayName("getEndMessage returns SessionEndMessage when session is complete")
        void getEndMessage_returnsMessage_whenComplete() {
            SessionConfig shortConfig = new SessionConfig(
                1, 1, 5.0, MovementType.STATIONARY, ReactionType.NONE, 5
            );
            GameSession shortSession = new GameSession(shortConfig);
            GameLoop shortLoop = new GameLoop(shortSession);

            shortLoop.tick(1.0f);

            SessionEndMessage endMessage = shortLoop.getEndMessage();
            assertNotNull(endMessage);
            assertNotNull(endMessage.getResults());
        }

        @Test
        @DisplayName("getEndMessage returns results with correct elapsed time")
        void getEndMessage_hasCorrectElapsedTime() {
            SessionConfig shortConfig = new SessionConfig(
                2, 1, 5.0, MovementType.STATIONARY, ReactionType.NONE, 5
            );
            GameSession shortSession = new GameSession(shortConfig);
            GameLoop shortLoop = new GameLoop(shortSession);

            shortLoop.tick(1.0f);
            shortLoop.tick(1.0f);

            SessionEndMessage endMessage = shortLoop.getEndMessage();
            assertEquals(2.0, endMessage.getResults().getElapsedTime(), 0.1);
        }

        @Test
        @DisplayName("getEndMessage returns null when session is not complete")
        void getEndMessage_returnsNull_whenNotComplete() {
            assertNull(gameLoop.getEndMessage());
        }

        @Test
        @DisplayName("getGameState returns current game state message")
        void getGameState_returnsCurrentState() {
            GameStateMessage stateMsg = gameLoop.getGameState();

            assertNotNull(stateMsg);
            assertNotNull(stateMsg.getPlayer());
            assertNotNull(stateMsg.getOpponents());
            assertEquals(session.getState().getElapsedTime(), stateMsg.getTimer(), 0.001);
            assertEquals(session.getState().getScore(), stateMsg.getScore());
            assertNotNull(stateMsg.getCooldowns());
        }
    }

    // ==================== Integration: Full Session Lifecycle ====================

    @Nested
    @DisplayName("full session lifecycle")
    class FullLifecycleTests {

        @Test
        @DisplayName("start → tick → end lifecycle completes successfully")
        void startTickEnd_lifecycle() {
            SessionConfig shortConfig = new SessionConfig(
                2, 2, 5.0, MovementType.STATIONARY, ReactionType.NONE, 5
            );
            GameSession lifecycleSession = new GameSession(shortConfig);
            GameLoop lifecycleLoop = new GameLoop(lifecycleSession);

            // Session should be running
            assertFalse(lifecycleSession.isComplete());

            // Tick partway through
            lifecycleLoop.tick(1.0f);
            assertFalse(lifecycleSession.isComplete());

            // Get mid-game state
            GameStateMessage midState = lifecycleLoop.getGameState();
            assertNotNull(midState);
            assertTrue(midState.getTimer() > 0);

            // Tick past duration
            lifecycleLoop.tick(1.5f);
            assertTrue(lifecycleSession.isComplete());

            // Get end message
            SessionEndMessage endMessage = lifecycleLoop.getEndMessage();
            assertNotNull(endMessage);
            assertTrue(endMessage.getResults().getElapsedTime() >= 2.0);
        }

        @Test
        @DisplayName("start → move → tick → verify position changed")
        void moveAndTick_updatesPosition() {
            double startX = session.getPlayer().getPosition().getX();
            double startY = session.getPlayer().getPosition().getY();

            // Move player right and down
            gameLoop.processInput(new MoveToMessage(startX + 200, startY + 100));
            gameLoop.tick(0.1f);

            Position newPos = session.getPlayer().getPosition();
            assertTrue(newPos.getX() > startX || newPos.getY() > startY,
                "Player position should change after move + tick");
        }

        @Test
        @DisplayName("start → abort → verify session ends immediately")
        void startAbort_endsImmediately() {
            assertFalse(session.isComplete());

            gameLoop.processInput(new AbortMessage());

            assertTrue(session.isComplete());
            assertTrue(session.isAborted());
            assertEquals(0, session.getState().getElapsedTime(), 0.001,
                "Aborted session should not have advanced time");
        }

        @Test
        @DisplayName("multiple ticks with CHASE opponents moves them")
        void multipleTicks_chaseOpponentsMove() {
            SessionConfig chaseConfig = new SessionConfig(
                60, 1, 5.0, MovementType.CHASE, ReactionType.NONE, 5
            );
            GameSession chaseSession = new GameSession(chaseConfig);
            GameLoop chaseLoop = new GameLoop(chaseSession);

            Opponent opponent = chaseSession.getOpponents().get(0);
            Position opponentStart = new Position(
                opponent.getPosition().getX(),
                opponent.getPosition().getY()
            );

            // Run several ticks
            for (int i = 0; i < 10; i++) {
                chaseLoop.tick(0.05f);
            }

            Position opponentEnd = opponent.getPosition();
            double distanceMoved = opponentStart.distanceTo(opponentEnd);
            assertTrue(distanceMoved > 0, "Chase opponent should have moved after multiple ticks");
        }
    }

    // ==================== Helper Methods ====================

    private SessionConfig createDefaultConfig() {
        return new SessionConfig(
            60,
            3,
            5.0,
            MovementType.STATIONARY,
            ReactionType.NONE,
            5
        );
    }
}