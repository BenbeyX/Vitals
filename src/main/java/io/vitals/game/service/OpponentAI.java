package io.vitals.game.service;

import io.vitals.game.constants.GameConstants;
import io.vitals.game.model.MovementType;
import io.vitals.game.model.Opponent;
import io.vitals.game.model.Position;
import io.vitals.game.model.ReactionType;
import io.vitals.game.model.Velocity;

public class OpponentAI {

    private static final double ARRIVAL_THRESHOLD = 1.0;

    public Velocity calculateMovement(Opponent opponent, Position playerPos, float deltaTime) {
        MovementType movementType = opponent.getMovementType();

        Velocity velocity = calculateMovementByType(opponent, playerPos, movementType);

        opponent.setVelocity(velocity);
        return velocity;
    }

    private Velocity calculateMovementByType(Opponent opponent, Position playerPos,
                                             MovementType movementType) {
        switch (movementType) {
            case STATIONARY:
                return new Velocity(0, 0);
            case PATROL:
                return calculatePatrolMovement(opponent);
            case CHASE:
                return calculateChaseMovement(opponent, playerPos);
            case FLEE:
                return calculateFleeMovement(opponent, playerPos);
            default:
                return new Velocity(0, 0);
        }
    }

    private Velocity calculatePatrolMovement(Opponent opponent) {
        Position current = opponent.getPosition();
        Position target = opponent.isMovingToEndpoint()
            ? opponent.getPatrolEndpoint()
            : opponent.getPatrolAnchor();

        if (target == null) {
            return new Velocity(0, 0);
        }

        double distance = current.distanceTo(target);
        if (distance <= ARRIVAL_THRESHOLD) {
            opponent.setMovingToEndpoint(!opponent.isMovingToEndpoint());
            target = opponent.isMovingToEndpoint()
                ? opponent.getPatrolEndpoint()
                : opponent.getPatrolAnchor();
            if (target == null) {
                return new Velocity(0, 0);
            }
        }

        return velocityToward(current, target, GameConstants.MOVEMENT_SPEED);
    }

    private Velocity calculateChaseMovement(Opponent opponent, Position playerPos) {
        Position current = opponent.getPosition();

        if (!isPlayerInProximity(current, playerPos)) {
            return new Velocity(0, 0);
        }

        return velocityToward(current, playerPos, GameConstants.MOVEMENT_SPEED);
    }

    private Velocity calculateFleeMovement(Opponent opponent, Position playerPos) {
        Position current = opponent.getPosition();

        if (!isPlayerInProximity(current, playerPos)) {
            return new Velocity(0, 0);
        }

        double dx = current.getX() - playerPos.getX();
        double dy = current.getY() - playerPos.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance == 0) {
            return new Velocity(0, 0);
        }

        double speed = GameConstants.MOVEMENT_SPEED;
        double dirX = (dx / distance) * speed;
        double dirY = (dy / distance) * speed;

        double nextX = current.getX() + dirX;
        if (nextX > GameConstants.ARENA_SIZE_X || nextX < 0) {
            dirX = clampToArenaBounds(dirX, current.getX(), GameConstants.ARENA_SIZE_X);
        }
        double nextY = current.getY() + dirY;
        if (nextY > GameConstants.ARENA_SIZE_Y || nextY < 0) {
            dirY = clampToArenaBounds(dirY, current.getY(), GameConstants.ARENA_SIZE_Y);
        }

        return new Velocity(dirX, dirY);
    }

    private double clampToArenaBounds(double velocityComponent, double currentPos, int arenaMax) {
        double nextPos = currentPos + velocityComponent;
        if (nextPos > arenaMax) {
            return Math.max(0, arenaMax - currentPos);
        }
        if (nextPos < 0) {
            return Math.min(0, -currentPos);
        }
        return velocityComponent;
    }

    public void processReaction(Opponent opponent, Position playerPos, boolean playerDashing) {
        ReactionType reactionType = opponent.getReactionType();

        switch (reactionType) {
            case RETALIATE:
                processRetaliate(opponent);
                break;
            case DODGE:
                processDodge(opponent, playerPos, playerDashing);
                break;
            case PROXIMITY_TRIGGER:
                processProximityTrigger(opponent, playerPos);
                break;
            case NONE:
            default:
                break;
        }
    }

    private void processRetaliate(Opponent opponent) {
        opponent.setRetaliationActive(true);
    }

    private void processDodge(Opponent opponent, Position playerPos, boolean playerDashing) {
        if (!playerDashing) {
            return;
        }
        if (opponent.getDodgeCooldownRemaining() > 0) {
            return;
        }

        Position current = opponent.getPosition();
        double dx = playerPos.getX() - current.getX();
        double dy = playerPos.getY() - current.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance == 0) {
            return;
        }

        double speed = GameConstants.MOVEMENT_SPEED;
        double perpX = (-dy / distance) * speed;
        double perpY = (dx / distance) * speed;

        opponent.setVelocity(new Velocity(perpX, perpY));
        opponent.setDodgeCooldownRemaining(GameConstants.DODGE_COOLDOWN);
    }

    private void processProximityTrigger(Opponent opponent, Position playerPos) {
        if (opponent.isActive()) {
            return;
        }
        if (opponent.getMovementType() == MovementType.STATIONARY) {
            return;
        }
        if (!isPlayerInProximity(opponent.getPosition(), playerPos)) {
            return;
        }

        opponent.setActive(true);
    }

    private boolean isPlayerInProximity(Position opponentPos, Position playerPos) {
        double distance = opponentPos.distanceTo(playerPos);
        return distance <= GameConstants.PROXIMITY_TRIGGER_RANGE;
    }

    private Velocity velocityToward(Position from, Position to, double speed) {
        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance == 0) {
            return new Velocity(0, 0);
        }

        double dirX = (dx / distance) * speed;
        double dirY = (dy / distance) * speed;

        return new Velocity(dirX, dirY);
    }
}
