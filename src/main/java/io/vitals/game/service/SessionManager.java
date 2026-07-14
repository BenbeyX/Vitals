package io.vitals.game.service;

import io.vitals.game.constants.GameConstants;
import io.vitals.game.model.SessionConfig;
import io.vitals.game.result.SessionResult;
import io.vitals.game.state.SessionState;

public class SessionManager {

    public void tick(SessionState state, float deltaTime) {
        if (deltaTime <= 0) {
            return;
        }

        state.setElapsedTime(state.getElapsedTime() + deltaTime);

        tickQCooldown(state, deltaTime);
        tickSpeedBoost(state, deltaTime);
    }

    private void tickQCooldown(SessionState state, float deltaTime) {
        double remaining = state.getQCooldownRemaining();
        if (remaining > 0) {
            state.setQCooldownRemaining(Math.max(0, remaining - deltaTime));
        }
    }

    private void tickSpeedBoost(SessionState state, float deltaTime) {
        double remaining = state.getSpeedBoostRemaining();
        if (remaining > 0) {
            double newRemaining = Math.max(0, remaining - deltaTime);
            state.setSpeedBoostRemaining(newRemaining);
            if (newRemaining <= 0) {
                state.setSpeedBoostActive(false);
            }
        }
    }

    public void recordVitalHit(SessionState state) {
        state.setVitalsHit(state.getVitalsHit() + 1);
        state.setScore(state.getScore() + GameConstants.VITAL_HIT_SCORE);
        state.setSpeedBoostRemaining(GameConstants.SPEED_BOOST_DURATION);
        state.setSpeedBoostActive(true);
    }

    public void recordOpponentSlain(SessionState state) {
        state.setOpponentsSlain(state.getOpponentsSlain() + 1);
        state.setScore(state.getScore() + GameConstants.OPPONENT_SLAIN_SCORE);
    }

    public void recordWhiff(SessionState state) {
        state.setWhiffs(state.getWhiffs() + 1);
    }

    public void recordNonVitalHit(SessionState state) {
        state.setNonVitalHits(state.getNonVitalHits() + 1);
    }

    public boolean isSessionComplete(SessionState state, SessionConfig config) {
        return state.getElapsedTime() >= config.getDurationSeconds();
    }

    public SessionResult getResults(SessionState state) {
        int totalAttacks = state.getVitalsHit() + state.getNonVitalHits() + state.getWhiffs();
        double accuracy = totalAttacks > 0 ? (double) state.getVitalsHit() / totalAttacks : 0.0;

        return new SessionResult(
            state.getScore(),
            state.getVitalsHit(),
            state.getOpponentsSlain(),
            state.getNonVitalHits(),
            state.getWhiffs(),
            state.getElapsedTime(),
            accuracy
        );
    }
}
