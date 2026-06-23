package io.vitals.game.model;

public class Opponent {
    private Position position;
    private Velocity velocity;
    private Vital vital;
    private int hp;
    private int maxHp;
    private int cooldown;
    private int maxCooldown;
    private int cooldownRemaining;

    public Opponent(Position position, Velocity velocity, Vital vital, int hp, int maxHp, int cooldown, int maxCooldown, int cooldownRemaining) {
        this.position = position;
        this.velocity = velocity;
        this.vital = vital;
        this.hp = hp;
        this.maxHp = maxHp;
        this.cooldown = cooldown;
        this.maxCooldown = maxCooldown;
        this.cooldownRemaining = cooldownRemaining;
    }

    public Position getPosition() {
        return position;
    }

    public Velocity getVelocity() {
        return velocity;
    }

}
