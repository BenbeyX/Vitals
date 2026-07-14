package io.vitals.game.service;

import io.vitals.game.constants.GameConstants;
import io.vitals.game.model.Opponent;
import io.vitals.game.model.Player;
import io.vitals.game.model.Position;
import io.vitals.game.model.Velocity;
import io.vitals.game.model.message.*;
import io.vitals.game.result.SessionResult;
import io.vitals.game.state.SessionState;

public class GameLoop {

    private final GameSession session;
    private final SessionManager sessionManager;
    private final MovementCalculator movementCalculator;
    private final CombatResolver combatResolver;
    private final OpponentAI opponentAI;

    private boolean qAttackQueued;

    public GameLoop(GameSession session) {
        this.session = session;
        this.sessionManager = new SessionManager();
        this.movementCalculator = new MovementCalculator();
        this.combatResolver = new CombatResolver(session.getVitalManager());
        this.opponentAI = new OpponentAI();
        this.qAttackQueued = false;
    }

    public void tick(float deltaTime) {
        if (session.isComplete()) {
            return;
        }
        if (deltaTime <= 0) {
            return;
        }

        // 1. Update session time and cooldowns
        sessionManager.tick(session.getState(), deltaTime);

        // 2. Update player position based on velocity
        Player player = session.getPlayer();
        Position currentPlayerPos = player.getPosition();
        Velocity playerVelocity = player.getVelocity();

        Position newPlayerPos = movementCalculator.calculateNextPosition(
            currentPlayerPos, playerVelocity, deltaTime
        );
        player.setPosition(newPlayerPos);

        // Apply boundary constraints to player
        movementCalculator.applyBoundaryConstraints(player.getPosition(), playerVelocity, deltaTime);

        // 3. Update opponent positions and reactions
        for (Opponent opponent : session.getOpponents()) {
            if (opponent.isDefeated()) {
                continue;
            }

            // Calculate movement for this opponent
            Velocity opponentVelocity = opponentAI.calculateMovement(
                opponent, player.getPosition(), deltaTime
            );
            opponent.setVelocity(opponentVelocity);

            Position newOpponentPos = movementCalculator.calculateNextPosition(
                opponent.getPosition(), opponentVelocity, deltaTime
            );
            opponent.setPosition(newOpponentPos);

            // Apply boundary constraints
            movementCalculator.applyBoundaryConstraints(
                opponent.getPosition(), opponentVelocity, deltaTime
            );

            // Process reaction (dodge, proximity trigger, etc.)
            boolean playerDashing = isPlayerDashing();
            opponentAI.processReaction(opponent, player.getPosition(), playerDashing);
        }

        // 4. Process Q attack if queued
        if (qAttackQueued) {
            processQAttack();
            qAttackQueued = false;
        }

        // 5. Check session completion
        if (sessionManager.isSessionComplete(session.getState(), session.getConfig())) {
            session.markComplete();
        }
    }

    private boolean isPlayerDashing() {
        Velocity velocity = session.getPlayer().getVelocity();
        double speed = Math.sqrt(velocity.getX() * velocity.getX() + velocity.getY() * velocity.getY());
        return speed >= GameConstants.DASH_SPEED;
    }

    private void processQAttack() {
        Player player = session.getPlayer();
        Opponent target = combatResolver.findNearestTarget(
            player.getPosition(),
            session.getOpponents(),
            GameConstants.Q_ATTACK_RANGE
        );

        if (target == null) {
            // No target in range - this is a whiff
            sessionManager.recordWhiff(session.getState());
            return;
        }

        // Check if it's a vital hit
        boolean isVitalHit = session.getVitalManager().isVitalHit(
            player.getPosition(),
            target.getPosition(),
            target.getVital()
        );

        // Apply damage
        combatResolver.applyDamage(target, isVitalHit);

        // Record the hit
        if (isVitalHit) {
            sessionManager.recordVitalHit(session.getState());
            if (target.isDefeated()) {
                sessionManager.recordOpponentSlain(session.getState());
            }
        } else {
            sessionManager.recordNonVitalHit(session.getState());
            if (target.isDefeated()) {
                sessionManager.recordOpponentSlain(session.getState());
            }
        }
    }

    public void processInput(MoveToMessage message) {
        if (session.isComplete()) {
            return;
        }

        Player player = session.getPlayer();
        Position currentPos = player.getPosition();
        Position targetPos = new Position(message.getX(), message.getY());

        // Calculate velocity toward target
        double dx = targetPos.getX() - currentPos.getX();
        double dy = targetPos.getY() - currentPos.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance == 0) {
            player.setVelocity(new Velocity(0, 0));
            return;
        }

        // Apply speed boost if active
        double speed = GameConstants.MOVEMENT_SPEED;
        if (session.getState().isSpeedBoostActive()) {
            speed += GameConstants.SPEED_BOOST;
        }

        double vx = (dx / distance) * speed;
        double vy = (dy / distance) * speed;

        player.setVelocity(new Velocity(vx, vy));
    }

    public void processInput(DashMessage message) {
        if (session.isComplete()) {
            return;
        }

        Player player = session.getPlayer();
        Position currentPos = player.getPosition();
        Position targetPos = new Position(message.getTargetX(), message.getTargetY());

        // Calculate direction toward target
        double dx = targetPos.getX() - currentPos.getX();
        double dy = targetPos.getY() - currentPos.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance == 0) {
            return;
        }

        // Dash at DASH_SPEED
        double vx = (dx / distance) * GameConstants.DASH_SPEED;
        double vy = (dy / distance) * GameConstants.DASH_SPEED;

        player.setVelocity(new Velocity(vx, vy));
    }

    public void processInput(AbortMessage message) {
        if (session.isComplete()) {
            return;
        }
        session.abort();
    }

    public void processInput(QAttackMessage message) {
        if (session.isComplete()) {
            return;
        }
        qAttackQueued = true;
    }

    public GameStateMessage getGameState() {
        Player player = session.getPlayer();
        SessionState state = session.getState();

        Cooldowns cooldowns = new Cooldowns(
            state.getQCooldownRemaining(),
            state.isSpeedBoostActive(),
            state.getSpeedBoostRemaining()
        );

        return new GameStateMessage(
            player,
            session.getOpponents(),
            state.getElapsedTime(),
            state.getScore(),
            cooldowns
        );
    }

    public SessionEndMessage getEndMessage() {
        if (!session.isComplete()) {
            return null;
        }

        SessionResult results = sessionManager.getResults(session.getState());
        return new SessionEndMessage(results);
    }
}