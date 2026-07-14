package io.vitals.game.model.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DashMessage {
    private final double targetX;
    private final double targetY;

    @JsonCreator
    public DashMessage(
        @JsonProperty("targetX") double targetX,
        @JsonProperty("targetY") double targetY
    ) {
        this.targetX = targetX;
        this.targetY = targetY;
    }

    public double getTargetX() {
        return targetX;
    }

    public double getTargetY() {
        return targetY;
    }
}
