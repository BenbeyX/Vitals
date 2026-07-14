package io.vitals.game.service;

import io.vitals.game.constants.GameConstants;
import io.vitals.game.model.Opponent;
import io.vitals.game.model.Position;
import io.vitals.game.state.SessionState;

import java.util.List;

public class CombatResolver {

    private final VitalManager vitalManager;

    public CombatResolver(VitalManager vitalManager) {
        this.vitalManager = vitalManager;
    }

    public Opponent findNearestTarget(Position attacker, List<Opponent> opponents, float range) {
        Opponent nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (Opponent opponent : opponents) {
            if (opponent.isDefeated()) {
                continue;
            }

            double distance = attacker.distanceTo(opponent.getPosition());
            if (distance <= range && distance < nearestDistance) {
                nearestDistance = distance;
                nearest = opponent;
            }
        }

        return nearest;
    }

    public DamageResult applyDamage(Opponent target, boolean isVitalHit) {
        int previousHp = target.getHp();
        int damage = isVitalHit ? GameConstants.VITAL_DAMAGE : GameConstants.NON_VITAL_DAMAGE;
        target.setHp(previousHp - damage);
        int newHp = target.getHp();

        return new DamageResult(previousHp, newHp, isVitalHit, damage);
    }

    public boolean calculateSpeedBoost(boolean vitalHit, SessionState state) {
        if (vitalHit) {
            state.setSpeedBoostRemaining(GameConstants.SPEED_BOOST_DURATION);
            return true;
        }
        return state.getSpeedBoostRemaining() > 0;
    }

    public void tickSpeedBoost(SessionState state, double deltaSeconds) {
        if (state.getSpeedBoostRemaining() > 0) {
            state.setSpeedBoostRemaining(Math.max(0, state.getSpeedBoostRemaining() - deltaSeconds));
        }
    }

    public static class DamageResult {
        private final int previousHp;
        private final int newHp;
        private final boolean vitalHit;
        private final int damage;

        public DamageResult(int previousHp, int newHp, boolean vitalHit, int damage) {
            this.previousHp = previousHp;
            this.newHp = newHp;
            this.vitalHit = vitalHit;
            this.damage = damage;
        }

        public int getPreviousHp() {
            return previousHp;
        }

        public int getNewHp() {
            return newHp;
        }

        public boolean isVitalHit() {
            return vitalHit;
        }

        public int getDamage() {
            return damage;
        }
    }
}
