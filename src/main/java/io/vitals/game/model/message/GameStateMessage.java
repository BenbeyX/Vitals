package io.vitals.game.model.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.vitals.game.model.Opponent;
import io.vitals.game.model.Player;

import java.util.List;

public class GameStateMessage {
    private final Player player;
    private final List<Opponent> opponents;
    private final double timer;
    private final int score;
    private final Cooldowns cooldowns;

    @JsonCreator
    public GameStateMessage(
        @JsonProperty("player") Player player,
        @JsonProperty("opponents") List<Opponent> opponents,
        @JsonProperty("timer") double timer,
        @JsonProperty("score") int score,
        @JsonProperty("cooldowns") Cooldowns cooldowns
    ) {
        this.player = player;
        this.opponents = opponents;
        this.timer = timer;
        this.score = score;
        this.cooldowns = cooldowns;
    }

    public Player getPlayer() {
        return player;
    }

    public List<Opponent> getOpponents() {
        return opponents;
    }

    public double getTimer() {
        return timer;
    }

    public int getScore() {
        return score;
    }

    public Cooldowns getCooldowns() {
        return cooldowns;
    }
}
