package io.vitals.game.state;

public class SessionState {
    private double elapsedTime;
    private int score;
    private int vitalsHit;
    private int whiffs;
    private int nonVitalHits;
    private double qCooldownRemaining;
    private boolean speedBoostActive;
    private double speedBoostRemaining;
    private int opponentsSlain;

    public SessionState() {
        this.elapsedTime = 0;
        this.score = 0;
        this.vitalsHit = 0;
        this.whiffs = 0;
        this.nonVitalHits = 0;
        this.qCooldownRemaining = 0;
        this.speedBoostActive = false;
        this.speedBoostRemaining = 0;
        this.opponentsSlain = 0;
    }

    public double getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(double elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getVitalsHit() {
        return vitalsHit;
    }

    public void setVitalsHit(int vitalsHit) {
        this.vitalsHit = vitalsHit;
    }

    public int getWhiffs() {
        return whiffs;
    }

    public void setWhiffs(int whiffs) {
        this.whiffs = whiffs;
    }

    public int getNonVitalHits() {
        return nonVitalHits;
    }

    public void setNonVitalHits(int nonVitalHits) {
        this.nonVitalHits = nonVitalHits;
    }

    public double getQCooldownRemaining() {
        return qCooldownRemaining;
    }

    public void setQCooldownRemaining(double qCooldownRemaining) {
        this.qCooldownRemaining = qCooldownRemaining;
    }

    public boolean isSpeedBoostActive() {
        return speedBoostActive;
    }

    public void setSpeedBoostActive(boolean speedBoostActive) {
        this.speedBoostActive = speedBoostActive;
    }

    public double getSpeedBoostRemaining() {
        return speedBoostRemaining;
    }

    public void setSpeedBoostRemaining(double speedBoostRemaining) {
        this.speedBoostRemaining = speedBoostRemaining;
    }

    public int getOpponentsSlain() {
        return opponentsSlain;
    }

    public void setOpponentsSlain(int opponentsSlain) {
        this.opponentsSlain = opponentsSlain;
    }
}
