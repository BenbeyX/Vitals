package io.vitals.game.model;

public class SessionConfig {
    private final int durationSeconds;
    private final int opponentCount;
    private final double qCooldownSeconds;
    private final MovementType defaultMovementType;
    private final ReactionType defaultReactionType;
    private final int defaultHp;

    public SessionConfig(int durationSeconds, int opponentCount, double qCooldownSeconds,
                         MovementType defaultMovementType, ReactionType defaultReactionType, int defaultHp) {
        this.durationSeconds = durationSeconds;
        this.opponentCount = opponentCount;
        this.qCooldownSeconds = qCooldownSeconds;
        this.defaultMovementType = defaultMovementType;
        this.defaultReactionType = defaultReactionType;
        this.defaultHp = defaultHp;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public int getOpponentCount() {
        return opponentCount;
    }

    public double getQCooldownSeconds() {
        return qCooldownSeconds;
    }

    public MovementType getDefaultMovementType() {
        return defaultMovementType;
    }

    public ReactionType getDefaultReactionType() {
        return defaultReactionType;
    }

    public int getDefaultHp() {
        return defaultHp;
    }
}
