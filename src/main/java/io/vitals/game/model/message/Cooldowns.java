package io.vitals.game.model.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Cooldowns {
    private final double qCooldownRemaining;
    private final boolean speedBoostActive;
    private final double speedBoostRemaining;

    @JsonCreator
    public Cooldowns(
        @JsonProperty("qcooldownRemaining") double qCooldownRemaining,
        @JsonProperty("speedBoostActive") boolean speedBoostActive,
        @JsonProperty("speedBoostRemaining") double speedBoostRemaining
    ) {
        this.qCooldownRemaining = qCooldownRemaining;
        this.speedBoostActive = speedBoostActive;
        this.speedBoostRemaining = speedBoostRemaining;
    }

    public double getQCooldownRemaining() {
        return qCooldownRemaining;
    }

    public boolean isSpeedBoostActive() {
        return speedBoostActive;
    }

    public double getSpeedBoostRemaining() {
        return speedBoostRemaining;
    }
}
