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
    private MovementType movementType;
    private ReactionType reactionType;
    private Position patrolAnchor;
    private Position patrolEndpoint;
    private boolean movingToEndpoint;
    private double dodgeCooldownRemaining;
    private boolean retaliationActive;
    private boolean active;

    public Opponent(Position position, Velocity velocity, Vital vital, int hp, int maxHp,
                   int cooldown, int maxCooldown, int cooldownRemaining) {
        this(position, velocity, vital, hp, maxHp, cooldown, maxCooldown, cooldownRemaining,
             MovementType.STATIONARY, ReactionType.NONE, null, null, true, 0, false, true);
    }

    public Opponent(Position position, Velocity velocity, Vital vital, int hp, int maxHp,
                   int cooldown, int maxCooldown, int cooldownRemaining,
                   MovementType movementType, ReactionType reactionType,
                   Position patrolAnchor, Position patrolEndpoint, boolean movingToEndpoint,
                   double dodgeCooldownRemaining, boolean retaliationActive, boolean active) {
        this.position = position;
        this.velocity = velocity;
        this.vital = vital;
        this.hp = hp;
        this.maxHp = maxHp;
        this.cooldown = cooldown;
        this.maxCooldown = maxCooldown;
        this.cooldownRemaining = cooldownRemaining;
        this.movementType = movementType;
        this.reactionType = reactionType;
        this.patrolAnchor = patrolAnchor;
        this.patrolEndpoint = patrolEndpoint;
        this.movingToEndpoint = movingToEndpoint;
        this.dodgeCooldownRemaining = dodgeCooldownRemaining;
        this.retaliationActive = retaliationActive;
        this.active = active;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Velocity getVelocity() {
        return velocity;
    }

    public void setVelocity(Velocity velocity) {
        this.velocity = velocity;
    }

    public Vital getVital() {
        return vital;
    }

    public void setVital(Vital vital) {
        this.vital = vital;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = Math.max(0, hp);
    }

    public int getMaxHp() {
        return maxHp;
    }

    public boolean isDefeated() {
        return hp <= 0;
    }

    public int getCooldown() {
        return cooldown;
    }

    public int getMaxCooldown() {
        return maxCooldown;
    }

    public int getCooldownRemaining() {
        return cooldownRemaining;
    }

    public void setCooldownRemaining(int cooldownRemaining) {
        this.cooldownRemaining = cooldownRemaining;
    }

    public MovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(MovementType movementType) {
        this.movementType = movementType;
    }

    public ReactionType getReactionType() {
        return reactionType;
    }

    public void setReactionType(ReactionType reactionType) {
        this.reactionType = reactionType;
    }

    public Position getPatrolAnchor() {
        return patrolAnchor;
    }

    public void setPatrolAnchor(Position patrolAnchor) {
        this.patrolAnchor = patrolAnchor;
    }

    public Position getPatrolEndpoint() {
        return patrolEndpoint;
    }

    public void setPatrolEndpoint(Position patrolEndpoint) {
        this.patrolEndpoint = patrolEndpoint;
    }

    public boolean isMovingToEndpoint() {
        return movingToEndpoint;
    }

    public void setMovingToEndpoint(boolean movingToEndpoint) {
        this.movingToEndpoint = movingToEndpoint;
    }

    public double getDodgeCooldownRemaining() {
        return dodgeCooldownRemaining;
    }

    public void setDodgeCooldownRemaining(double dodgeCooldownRemaining) {
        this.dodgeCooldownRemaining = Math.max(0, dodgeCooldownRemaining);
    }

    public boolean isRetaliationActive() {
        return retaliationActive;
    }

    public void setRetaliationActive(boolean retaliationActive) {
        this.retaliationActive = retaliationActive;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
