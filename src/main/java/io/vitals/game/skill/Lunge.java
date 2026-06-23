package io.vitals.game.skill;

import io.vitals.game.model.Player;
import io.vitals.game.model.Position;
import io.vitals.game.GameContext;

public class Lunge extends Skill {

    private final int range = 150;

    public Lunge() {
        this.cooldownDuration = 1.0;
    }    

    @Override
    public void activate(Player player, Position target, GameContext context) {        
            context.movePlayerTo(target);
            context.attackNearestIfInRange(player, range); // 150 = range
        }

}




