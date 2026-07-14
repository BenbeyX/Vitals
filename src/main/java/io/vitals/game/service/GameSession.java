package io.vitals.game.service;

import io.vitals.game.constants.GameConstants;
import io.vitals.game.model.Opponent;
import io.vitals.game.model.Player;
import io.vitals.game.model.Position;
import io.vitals.game.model.SessionConfig;
import io.vitals.game.model.Velocity;
import io.vitals.game.model.Vital;
import io.vitals.game.state.SessionState;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameSession {

    private final SessionConfig config;
    private final Player player;
    private final List<Opponent> opponents;
    private final SessionState state;
    private final VitalManager vitalManager;
    private boolean complete;
    private boolean aborted;

    public GameSession(SessionConfig config) {
        this.config = config;
        this.player = createPlayer();
        this.opponents = createOpponents(config);
        this.state = new SessionState();
        this.vitalManager = new VitalManager();
        this.vitalManager.activateAllVitals();
        this.complete = false;
        this.aborted = false;
    }

    private Player createPlayer() {
        double centerX = GameConstants.ARENA_SIZE_X / 2.0;
        double centerY = GameConstants.ARENA_SIZE_Y / 2.0;
        return new Player(new Position(centerX, centerY), new Velocity(0, 0));
    }

    private List<Opponent> createOpponents(SessionConfig config) {
        List<Opponent> opponents = new ArrayList<>();
        Random random = new Random();
        Vital[] vitals = Vital.values();

        for (int i = 0; i < config.getOpponentCount(); i++) {
            double x = random.nextDouble() * GameConstants.ARENA_SIZE_X;
            double y = random.nextDouble() * GameConstants.ARENA_SIZE_Y;
            Vital vital = vitals[random.nextInt(vitals.length)];

            Opponent opponent = new Opponent(
                new Position(x, y),
                new Velocity(0, 0),
                vital,
                config.getDefaultHp(),
                config.getDefaultHp(),
                0, 0, 0,
                config.getDefaultMovementType(),
                config.getDefaultReactionType(),
                null, null, true, 0, false, true
            );
            opponents.add(opponent);
        }
        return opponents;
    }

    public Player getPlayer() {
        return player;
    }

    public List<Opponent> getOpponents() {
        return opponents;
    }

    public SessionState getState() {
        return state;
    }

    public SessionConfig getConfig() {
        return config;
    }

    public VitalManager getVitalManager() {
        return vitalManager;
    }

    public boolean isComplete() {
        return complete;
    }

    public boolean isAborted() {
        return aborted;
    }

    public void abort() {
        this.aborted = true;
        this.complete = true;
    }

    public void markComplete() {
        this.complete = true;
    }
}