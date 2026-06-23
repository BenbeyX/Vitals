package io.vitals.game;

import io.vitals.game.model.Player;
import io.vitals.game.model.Position;
import io.vitals.game.model.Vital;

public class GameContext {
    private final Player player;

    public GameContext(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void movePlayerTo(Position position) {
        player.setPosition(position);
    }

    public void revealAllVitals(){
        throw new UnsupportedOperationException("Not implemented");
    }

    public void revealVital(Vital vital) {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void attackNearestIfInRange(Player player2, int i) {
        throw new UnsupportedOperationException("Not implemented");
    }

}
