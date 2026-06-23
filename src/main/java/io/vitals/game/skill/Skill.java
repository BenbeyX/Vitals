package io.vitals.game.skill;
import io.vitals.game.model.Player;
import io.vitals.game.model.Position;
import io.vitals.game.GameContext;

public abstract class Skill {
    protected double cooldownDuration;
    protected double remainingCooldown;

    public abstract void activate(Player player, Position target, GameContext gameContext);
    
    public boolean isOnCooldown() { return remainingCooldown > 0; };
    
    public void tickCooldown(double deltaTime) { remainingCooldown = Math.max(0, remainingCooldown - deltaTime); };
    
    public double getCooldownDuration() {
        return cooldownDuration;
    }
    
    public void setRemainingCooldown(double remainingCooldown) {
            this.remainingCooldown = remainingCooldown;
    }

    public void setCooldownDuration(double cooldownDuration) {
        this.cooldownDuration = cooldownDuration;
    }

    public double getRemainingCooldown() {
        return remainingCooldown;
    }

    
}
