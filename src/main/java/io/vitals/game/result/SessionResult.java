package io.vitals.game.result;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SessionResult {
    private final int score;
    private final int vitalsHit;
    private final int opponentsSlain;
    private final int nonVitalHits;
    private final int whiffs;
    private final double elapsedTime;
    private final double accuracy;

    @JsonCreator
    public SessionResult(
        @JsonProperty("score") int score,
        @JsonProperty("vitalsHit") int vitalsHit,
        @JsonProperty("opponentsSlain") int opponentsSlain,
        @JsonProperty("nonVitalHits") int nonVitalHits,
        @JsonProperty("whiffs") int whiffs,
        @JsonProperty("elapsedTime") double elapsedTime,
        @JsonProperty("accuracy") double accuracy
    ) {
        this.score = score;
        this.vitalsHit = vitalsHit;
        this.opponentsSlain = opponentsSlain;
        this.nonVitalHits = nonVitalHits;
        this.whiffs = whiffs;
        this.elapsedTime = elapsedTime;
        this.accuracy = accuracy;
    }

    public int getScore() {
        return score;
    }

    public int getVitalsHit() {
        return vitalsHit;
    }

    public int getOpponentsSlain() {
        return opponentsSlain;
    }

    public int getNonVitalHits() {
        return nonVitalHits;
    }

    public int getWhiffs() {
        return whiffs;
    }

    public double getElapsedTime() {
        return elapsedTime;
    }

    public double getAccuracy() {
        return accuracy;
    }
}
