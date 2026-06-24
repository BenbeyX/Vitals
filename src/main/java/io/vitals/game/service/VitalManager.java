package io.vitals.game.service;

import io.vitals.game.model.Position;
import io.vitals.game.model.Vital;

import java.util.*;

public class VitalManager {

    private static final List<Vital> GROUP_A = Arrays.asList(Vital.TOP, Vital.RIGHT);
    private static final List<Vital> GROUP_B = Arrays.asList(Vital.BOTTOM, Vital.LEFT);

    private final Random random;
    private final Set<Vital> activeVitals;

    public VitalManager() {
        this(new Random());
    }

    public VitalManager(Random random) {
        this.random = random;
        this.activeVitals = new HashSet<>();
    }

    public void activateVital(Vital vital) {
        activeVitals.add(vital);
    }

    public void activateAllVitals() {
        activeVitals.addAll(Arrays.asList(Vital.values()));
    }

    public void deactivateVital(Vital vital) {
        activeVitals.remove(vital);
    }

    public void clearActiveVitals() {
        activeVitals.clear();
    }

    public boolean hasActiveVitals() {
        return !activeVitals.isEmpty();
    }

    public Set<Vital> getActiveVitals() {
        return Collections.unmodifiableSet(activeVitals);
    }

    public Vital selectNextVital(Vital current) {
        List<Vital> targetGroup = getOpposingGroup(current);
        
        List<Vital> availableVitals = new ArrayList<>();
        for (Vital vital : targetGroup) {
            if (activeVitals.contains(vital)) {
                availableVitals.add(vital);
            }
        }

        if (availableVitals.isEmpty()) {
            return null;
        }

        return availableVitals.get(random.nextInt(availableVitals.size()));
    }

    private List<Vital> getOpposingGroup(Vital vital) {
        if (GROUP_A.contains(vital)) {
            return GROUP_B;
        }
        return GROUP_A;
    }

    public boolean isVitalHit(Position attackerPos, Position targetPos, Vital vital) {
        double angle = calculateAngle(attackerPos, targetPos);
        return isAngleInCone(angle, vital);
    }

    double calculateAngle(Position from, Position to) {
        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();
        
        double angleRadians = Math.atan2(dy, dx);
        double angleDegrees = Math.toDegrees(angleRadians);
        
        if (angleDegrees < 0) {
            angleDegrees += 360;
        }
        
        return angleDegrees;
    }

    private boolean isAngleInCone(double angle, Vital vital) {
        switch (vital) {
            case TOP:
                return angle >= 315 || angle < 45;
            case RIGHT:
                return angle >= 45 && angle < 135;
            case BOTTOM:
                return angle >= 135 && angle < 225;
            case LEFT:
                return angle >= 225 && angle < 315;
            default:
                return false;
        }
    }
}
