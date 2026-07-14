package io.vitals.game.model.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MoveToMessage {
    private final double x;
    private final double y;

    @JsonCreator
    public MoveToMessage(
        @JsonProperty("x") double x,
        @JsonProperty("y") double y
    ) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
