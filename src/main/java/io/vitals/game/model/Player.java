package io.vitals.game.model;

public class Player {
    private Position position;
    private Velocity velocity;

    public Player() {
    }
    
    public Player(Position position, Velocity velocity) {
        this.position = position;
        this.velocity = velocity;
    }

    public Position getPosition() {
        return position;
    }

    public Velocity getVelocity() {
        return velocity;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void setVelocity(Velocity velocity) {
        this.velocity = velocity;
    }
}
