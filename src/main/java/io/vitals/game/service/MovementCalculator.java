package io.vitals.game.service;

import io.vitals.game.model.Position;
import io.vitals.game.model.Velocity;
import io.vitals.game.constants.GameConstants;

public class MovementCalculator {
    
    public Position calculateNextPosition(Position position, Velocity velocity, double deltaTime) {
        return new Position(position.getX() + velocity.getX() * deltaTime, position.getY() + velocity.getY() * deltaTime);
    }

    public Position applyBoundaryConstraints(Position position, Velocity velocity, float deltaTime) {
        if (position.getX() < 0) {
            position.setX(0);
        }
        if (position.getX() > GameConstants.ARENA_SIZE_X) {
            position.setX(GameConstants.ARENA_SIZE_X);
        }
        if (position.getY() < 0) {
            position.setY(0);
        }
        if (position.getY() > GameConstants.ARENA_SIZE_Y) {
            position.setY(GameConstants.ARENA_SIZE_Y);
        }
        return position;
    }

    public boolean checkUnitCollision(Position position1, Position position2, float radius1, float radius2) {
        double distance = Math.sqrt(Math.pow(position1.getX() - position2.getX(), 2) + Math.pow(position1.getY() - position2.getY(), 2));
        return distance <= radius1 + radius2;
    }

    public Position resolveCollision(Position position1, Position position2, float radius1, float radius2) {
        double distance = Math.sqrt(Math.pow(position1.getX() - position2.getX(), 2) + Math.pow(position1.getY() - position2.getY(), 2));
        double overlap = radius1 + radius2 - distance;
        double xOverlap = overlap * (position1.getX() - position2.getX()) / distance;
        double yOverlap = overlap * (position1.getY() - position2.getY()) / distance;
        return new Position(position1.getX() - xOverlap, position1.getY() - yOverlap);
    }
}
