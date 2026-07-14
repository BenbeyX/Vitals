package io.vitals.game.service;

import io.vitals.game.constants.GameConstants;
import io.vitals.game.model.MovementType;
import io.vitals.game.model.Opponent;
import io.vitals.game.model.Position;
import io.vitals.game.model.ReactionType;
import io.vitals.game.model.Velocity;
import io.vitals.game.model.Vital;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpponentAITest {

    private OpponentAI opponentAI;

    @BeforeEach
    void setUp() {
        opponentAI = new OpponentAI();
    }

    // ==================== calculateMovement Tests ====================

    @Nested
    @DisplayName("calculateMovement")
    class CalculateMovementTests {

        @Test
        @DisplayName("STATIONARY returns zero velocity")
        void stationary_returnsZeroVelocity() {
            Opponent opponent = createOpponent(400, 300, MovementType.STATIONARY);
            Position playerPos = new Position(0, 0);

            Velocity result = opponentAI.calculateMovement(opponent, playerPos, 0.016f);

            assertEquals(0, result.getX(), 0.001);
            assertEquals(0, result.getY(), 0.001);
        }

        @Test
        @DisplayName("STATIONARY returns zero velocity regardless of player position")
        void stationary_playerNearby_returnsZeroVelocity() {
            Opponent opponent = createOpponent(400, 300, MovementType.STATIONARY);
            Position playerPos = new Position(401, 300);

            Velocity result = opponentAI.calculateMovement(opponent, playerPos, 0.016f);

            assertEquals(0, result.getX(), 0.001);
            assertEquals(0, result.getY(), 0.001);
        }

        @Test
        @DisplayName("PATROL moves toward patrol endpoint")
        void patrol_movesTowardEndpoint() {
            Opponent opponent = createPatrolOpponent(0, 0, 100, 0);
            Position playerPos = new Position(500, 500);

            Velocity result = opponentAI.calculateMovement(opponent, playerPos, 1.0f);

            assertTrue(result.getX() > 0, "Should move in positive X toward endpoint");
            assertEquals(0, result.getY(), 0.001, "Should not move in Y");
        }

        @Test
        @DisplayName("PATROL reverses direction when reaching endpoint")
        void patrol_reversesAtEndpoint() {
            Opponent opponent = createPatrolOpponent(100, 0, 0, 0);
            opponent.setMovingToEndpoint(false);
            Position playerPos = new Position(500, 500);

            Velocity result = opponentAI.calculateMovement(opponent, playerPos, 1.0f);

            assertTrue(result.getX() < 0, "Should move in negative X back toward anchor");
            assertEquals(0, result.getY(), 0.001);
        }

        @Test
        @DisplayName("PATROL updates movingToEndpoint flag when arriving at endpoint")
        void patrol_arrivesAtEndpoint_flipsFlag() {
            Opponent opponent = createPatrolOpponent(99.5, 0, 100, 0);
            opponent.setMovingToEndpoint(true);
            Position playerPos = new Position(500, 500);

            opponentAI.calculateMovement(opponent, playerPos, 0.05f);

            assertFalse(opponent.isMovingToEndpoint(), "Should flip to false after reaching endpoint");
        }

        @Test
        @DisplayName("PATROL updates movingToEndpoint flag when arriving at anchor")
        void patrol_arrivesAtAnchor_flipsFlag() {
            Opponent opponent = createPatrolOpponent(0.5, 0, 100, 0);
            opponent.setMovingToEndpoint(false);
            Position playerPos = new Position(500, 500);

            opponentAI.calculateMovement(opponent, playerPos, 0.05f);

            assertTrue(opponent.isMovingToEndpoint(), "Should flip to true after reaching anchor");
        }

        @Test
        @DisplayName("PATROL speed matches MOVEMENT_SPEED")
        void patrol_speedMatchesMovementSpeed() {
            Opponent opponent = createPatrolOpponent(0, 0, 100, 0);
            Position playerPos = new Position(500, 500);

            Velocity result = opponentAI.calculateMovement(opponent, playerPos, 1.0f);

            double speed = Math.sqrt(result.getX() * result.getX() + result.getY() * result.getY());
            assertEquals(GameConstants.MOVEMENT_SPEED, speed, 0.001);
        }

        @Test
        @DisplayName("PATROL moves in both X and Y when endpoint is diagonal")
        void patrol_diagonalEndpoint_movesBothAxes() {
            Opponent opponent = createPatrolOpponent(0, 0, 100, 100);
            Position playerPos = new Position(500, 500);

            Velocity result = opponentAI.calculateMovement(opponent, playerPos, 1.0f);

            assertTrue(result.getX() > 0, "Should move in positive X");
            assertTrue(result.getY() > 0, "Should move in positive Y");
        }

        @Test
        @DisplayName("CHASE moves toward player when within proximity range")
        void chase_withinRange_movesTowardPlayer() {
            Opponent opponent = createOpponent(0, 0, MovementType.CHASE);
            Position playerPos = new Position(100, 0);

            Velocity result = opponentAI.calculateMovement(opponent, playerPos, 1.0f);

            assertTrue(result.getX() > 0, "Should move toward player in positive X");
            assertEquals(0, result.getY(), 0.001);
        }

        @Test
        @DisplayName("CHASE returns zero velocity when player outside proximity range")
        void chase_outsideRange_returnsZeroVelocity() {
            Opponent opponent = createOpponent(0, 0, MovementType.CHASE);
            Position playerPos = new Position(500, 0);

            Velocity result = opponentAI.calculateMovement(opponent, playerPos, 1.0f);

            assertEquals(0, result.getX(), 0.001);
            assertEquals(0, result.getY(), 0.001);
        }

        @Test
        @DisplayName("CHASE activates when player exactly at proximity range boundary")
        void chase_atBoundary_movesTowardPlayer() {
            Opponent opponent = createOpponent(0, 0, MovementType.CHASE);
            Position playerPos = new Position(GameConstants.PROXIMITY_TRIGGER_RANGE, 0);

            Velocity result = opponentAI.calculateMovement(opponent, playerPos, 1.0f);

            assertTrue(result.getX() > 0, "Should move toward player at boundary");
        }

        @Test
        @DisplayName("CHASE speed matches MOVEMENT_SPEED")
        void chase_speedMatchesMovementSpeed() {
            Opponent opponent = createOpponent(0, 0, MovementType.CHASE);
            Position playerPos = new Position(100, 0);

            Velocity result = opponentAI.calculateMovement(opponent, playerPos, 1.0f);

            double speed = Math.sqrt(result.getX() * result.getX() + result.getY() * result.getY());
            assertEquals(GameConstants.MOVEMENT_SPEED, speed, 0.001);
        }

        @Test
        @DisplayName("CHASE moves diagonally toward player")
        void chase_diagonal_movesBothAxes() {
            Opponent opponent = createOpponent(0, 0, MovementType.CHASE);
            Position playerPos = new Position(100, 100);

            Velocity result = opponentAI.calculateMovement(opponent, playerPos, 1.0f);

            assertTrue(result.getX() > 0, "Should move in positive X");
            assertTrue(result.getY() > 0, "Should move in positive Y");
        }

        @Test
        @DisplayName("FLEE moves away from player when within proximity range")
        void flee_withinRange_movesAwayFromPlayer() {
            Opponent opponent = createOpponent(200, 200, MovementType.FLEE);
            Position playerPos = new Position(100, 200);

            Velocity result = opponentAI.calculateMovement(opponent, playerPos, 1.0f);

            assertTrue(result.getX() > 0, "Should move away from player in positive X");
            assertEquals(0, result.getY(), 0.001);
        }

        @Test
        @DisplayName("FLEE returns zero velocity when player outside proximity range")
        void flee_outsideRange_returnsZeroVelocity() {
            Opponent opponent = createOpponent(0, 0, MovementType.FLEE);
            Position playerPos = new Position(500, 0);

            Velocity result = opponentAI.calculateMovement(opponent, playerPos, 1.0f);

            assertEquals(0, result.getX(), 0.001);
            assertEquals(0, result.getY(), 0.001);
        }

        @Test
        @DisplayName("FLEE speed matches MOVEMENT_SPEED")
        void flee_speedMatchesMovementSpeed() {
            Opponent opponent = createOpponent(200, 200, MovementType.FLEE);
            Position playerPos = new Position(100, 200);

            Velocity result = opponentAI.calculateMovement(opponent, playerPos, 1.0f);

            double speed = Math.sqrt(result.getX() * result.getX() + result.getY() * result.getY());
            assertEquals(GameConstants.MOVEMENT_SPEED, speed, 0.001);
        }

        @Test
        @DisplayName("FLEE moves diagonally away from player")
        void flee_diagonal_movesBothAxes() {
            Opponent opponent = createOpponent(200, 200, MovementType.FLEE);
            Position playerPos = new Position(100, 100);

            Velocity result = opponentAI.calculateMovement(opponent, playerPos, 1.0f);

            assertTrue(result.getX() > 0, "Should move away in positive X");
            assertTrue(result.getY() > 0, "Should move away in positive Y");
        }

        @Test
        @DisplayName("FLEE does not overshoot arena boundary on X axis")
        void flee_atArenaBoundary_clampsX() {
            Opponent opponent = createOpponent(
                GameConstants.ARENA_SIZE_X - 1, 300, MovementType.FLEE);
            Position playerPos = new Position(
                GameConstants.ARENA_SIZE_X - 200, 300);

            Velocity result = opponentAI.calculateMovement(opponent, playerPos, 1.0f);

            assertTrue(result.getX() >= 0, "Should not flee beyond arena boundary");
        }

        @Test
        @DisplayName("FLEE does not overshoot arena boundary on Y axis")
        void flee_atArenaBoundary_clampsY() {
            Opponent opponent = createOpponent(
                400, GameConstants.ARENA_SIZE_Y - 1, MovementType.FLEE);
            Position playerPos = new Position(
                400, GameConstants.ARENA_SIZE_Y - 200);

            Velocity result = opponentAI.calculateMovement(opponent, playerPos, 1.0f);

            assertTrue(result.getY() >= 0, "Should not flee beyond arena boundary");
        }

        @Test
        @DisplayName("calculateMovement updates opponent velocity")
        void calculateMovement_updatesOpponentVelocity() {
            Opponent opponent = createOpponent(0, 0, MovementType.CHASE);
            Position playerPos = new Position(100, 0);

            opponentAI.calculateMovement(opponent, playerPos, 1.0f);

            Velocity opponentVelocity = opponent.getVelocity();
            assertTrue(opponentVelocity.getX() > 0, "Opponent velocity should be updated");
        }
    }

    // ==================== processReaction Tests ====================

    @Nested
    @DisplayName("processReaction")
    class ProcessReactionTests {

        @Test
        @DisplayName("RETALIATE sets retaliationActive to true")
        void retaliate_setsRetaliationActive() {
            Opponent opponent = createReactionOpponent(ReactionType.RETALIATE);
            Position playerPos = new Position(100, 0);

            opponentAI.processReaction(opponent, playerPos, false);

            assertTrue(opponent.isRetaliationActive(), "Retaliation should be active");
        }

        @Test
        @DisplayName("RETALIATE does not deactivate if already active")
        void retaliate_alreadyActive_staysActive() {
            Opponent opponent = createReactionOpponent(ReactionType.RETALIATE);
            opponent.setRetaliationActive(true);
            Position playerPos = new Position(100, 0);

            opponentAI.processReaction(opponent, playerPos, false);

            assertTrue(opponent.isRetaliationActive(), "Retaliation should remain active");
        }

        @Test
        @DisplayName("DODGE triggers when cooldown ready and player is dashing")
        void dodge_cooldownReady_playerDashing_triggers() {
            Opponent opponent = createReactionOpponent(ReactionType.DODGE);
            opponent.setDodgeCooldownRemaining(0);
            Position playerPos = new Position(100, 0);

            opponentAI.processReaction(opponent, playerPos, true);

            assertEquals(GameConstants.DODGE_COOLDOWN,
                opponent.getDodgeCooldownRemaining(), 0.001,
                "Dodge cooldown should be set after dodge");
        }

        @Test
        @DisplayName("DODGE does not trigger when cooldown not ready")
        void dodge_cooldownNotReady_doesNotTrigger() {
            Opponent opponent = createReactionOpponent(ReactionType.DODGE);
            opponent.setDodgeCooldownRemaining(2.0);
            Position playerPos = new Position(100, 0);

            opponentAI.processReaction(opponent, playerPos, true);

            assertEquals(2.0, opponent.getDodgeCooldownRemaining(), 0.001,
                "Cooldown should not change if already on cooldown");
        }

        @Test
        @DisplayName("DODGE does not trigger when player not dashing")
        void dodge_playerNotDashing_doesNotTrigger() {
            Opponent opponent = createReactionOpponent(ReactionType.DODGE);
            opponent.setDodgeCooldownRemaining(0);
            Position playerPos = new Position(100, 0);

            opponentAI.processReaction(opponent, playerPos, false);

            assertEquals(0, opponent.getDodgeCooldownRemaining(), 0.001,
                "Cooldown should remain zero when no dodge occurs");
        }

        @Test
        @DisplayName("DODGE sets velocity perpendicular to dash direction")
        void dodge_setsPerpendicularVelocity() {
            Opponent opponent = createReactionOpponent(ReactionType.DODGE);
            opponent.setDodgeCooldownRemaining(0);
            Position playerPos = new Position(0, 0);

            opponentAI.processReaction(opponent, playerPos, true);

            Velocity velocity = opponent.getVelocity();
            assertNotEquals(0,
                Math.abs(velocity.getX()) + Math.abs(velocity.getY()),
                0.001,
                "Dodge should set non-zero velocity");
        }

        @Test
        @DisplayName("DODGE can dodge again after cooldown expires")
        void dodge_canDodgeAfterCooldownExpires() {
            Opponent opponent = createReactionOpponent(ReactionType.DODGE);
            Position playerPos = new Position(100, 0);

            opponentAI.processReaction(opponent, playerPos, true);
            double cooldownAfterFirstDodge = opponent.getDodgeCooldownRemaining();
            assertTrue(cooldownAfterFirstDodge > 0, "First dodge should set cooldown");

            opponent.setDodgeCooldownRemaining(0);

            opponentAI.processReaction(opponent, playerPos, true);
            assertTrue(opponent.getDodgeCooldownRemaining() > 0,
                "Should dodge again after cooldown reset");
        }

        @Test
        @DisplayName("PROXIMITY_TRIGGER activates movement when player enters range")
        void proximityTrigger_playerInRange_activatesMovement() {
            Opponent opponent = createReactionOpponent(ReactionType.PROXIMITY_TRIGGER);
            opponent.setActive(false);
            opponent.setMovementType(MovementType.CHASE);
            Position playerPos = new Position(100, 0);

            opponentAI.processReaction(opponent, playerPos, false);

            assertTrue(opponent.isActive(), "Should activate when player in range");
        }

        @Test
        @DisplayName("PROXIMITY_TRIGGER does not activate when player outside range")
        void proximityTrigger_playerOutOfRange_doesNotActivate() {
            Opponent opponent = createReactionOpponent(ReactionType.PROXIMITY_TRIGGER);
            opponent.setActive(false);
            opponent.setMovementType(MovementType.CHASE);
            Position playerPos = new Position(500, 0);

            opponentAI.processReaction(opponent, playerPos, false);

            assertFalse(opponent.isActive(), "Should not activate when player out of range");
        }

        @Test
        @DisplayName("PROXIMITY_TRIGGER activates at exact boundary")
        void proximityTrigger_atBoundary_activates() {
            Opponent opponent = createReactionOpponent(ReactionType.PROXIMITY_TRIGGER);
            opponent.setActive(false);
            opponent.setMovementType(MovementType.CHASE);
            Position playerPos = new Position(GameConstants.PROXIMITY_TRIGGER_RANGE, 0);

            opponentAI.processReaction(opponent, playerPos, false);

            assertTrue(opponent.isActive(), "Should activate at boundary");
        }

        @Test
        @DisplayName("PROXIMITY_TRIGGER stays active once activated")
        void proximityTrigger_alreadyActive_staysActive() {
            Opponent opponent = createReactionOpponent(ReactionType.PROXIMITY_TRIGGER);
            opponent.setActive(true);
            Position playerPos = new Position(500, 0);

            opponentAI.processReaction(opponent, playerPos, false);

            assertTrue(opponent.isActive(), "Should remain active once activated");
        }

        @Test
        @DisplayName("PROXIMITY_TRIGGER does not activate a STATIONARY opponent")
        void proximityTrigger_stationary_doesNotActivate() {
            Opponent opponent = createReactionOpponent(ReactionType.PROXIMITY_TRIGGER);
            opponent.setActive(false);
            opponent.setMovementType(MovementType.STATIONARY);
            Position playerPos = new Position(100, 0);

            opponentAI.processReaction(opponent, playerPos, false);

            assertFalse(opponent.isActive(), "STATIONARY opponent should not activate on proximity");
        }

        @Test
        @DisplayName("NONE reaction does nothing")
        void noneReaction_doesNothing() {
            Opponent opponent = createReactionOpponent(ReactionType.NONE);
            opponent.setActive(false);
            Position playerPos = new Position(100, 0);

            opponentAI.processReaction(opponent, playerPos, true);

            assertFalse(opponent.isActive());
            assertFalse(opponent.isRetaliationActive());
            assertEquals(0, opponent.getDodgeCooldownRemaining(), 0.001);
        }
    }

    // ==================== Helper Methods ====================

    private Opponent createOpponent(double x, double y, MovementType movementType) {
        return new Opponent(
            new Position(x, y),
            new Velocity(0, 0),
            Vital.TOP,
            5,
            5,
            0,
            0,
            0,
            movementType,
            ReactionType.NONE,
            null,
            null,
            true,
            0,
            false,
            true
        );
    }

    private Opponent createPatrolOpponent(double x, double y, double endX, double endY) {
        return new Opponent(
            new Position(x, y),
            new Velocity(0, 0),
            Vital.TOP,
            5,
            5,
            0,
            0,
            0,
            MovementType.PATROL,
            ReactionType.NONE,
            new Position(x, y),
            new Position(endX, endY),
            true,
            0,
            false,
            true
        );
    }

    private Opponent createReactionOpponent(ReactionType reactionType) {
        return new Opponent(
            new Position(200, 200),
            new Velocity(0, 0),
            Vital.TOP,
            5,
            5,
            0,
            0,
            0,
            MovementType.STATIONARY,
            reactionType,
            null,
            null,
            true,
            0,
            false,
            true
        );
    }
}
