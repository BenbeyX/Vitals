package io.vitals.game.service;

import io.vitals.game.model.Opponent;
import io.vitals.game.model.Position;
import io.vitals.game.model.Velocity;
import io.vitals.game.model.Vital;
import io.vitals.game.state.SessionState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CombatResolverTest {

    private CombatResolver combatResolver;
    private SessionState sessionState;

    @BeforeEach
    void setUp() {
        VitalManager vitalManager = new VitalManager();
        combatResolver = new CombatResolver(vitalManager);
        sessionState = new SessionState();
    }

    // ==================== findNearestTarget Tests ====================

    @Test
    @DisplayName("findNearestTarget returns null when opponent list is empty")
    void findNearestTarget_emptyList_returnsNull() {
        Position attacker = new Position(0, 0);
        List<Opponent> opponents = Collections.emptyList();

        Opponent result = combatResolver.findNearestTarget(attacker, opponents, 100);

        assertNull(result);
    }

    @Test
    @DisplayName("findNearestTarget returns null when no opponents in range")
    void findNearestTarget_noneInRange_returnsNull() {
        Position attacker = new Position(0, 0);
        Opponent opponent = createOpponent(500, 500, Vital.TOP, 5);
        List<Opponent> opponents = Arrays.asList(opponent);

        Opponent result = combatResolver.findNearestTarget(attacker, opponents, 100);

        assertNull(result);
    }

    @Test
    @DisplayName("findNearestTarget returns nearest opponent when single in range")
    void findNearestTarget_singleInRange_returnsOpponent() {
        Position attacker = new Position(0, 0);
        Opponent opponent = createOpponent(50, 50, Vital.TOP, 5);
        List<Opponent> opponents = Arrays.asList(opponent);

        Opponent result = combatResolver.findNearestTarget(attacker, opponents, 100);

        assertNotNull(result);
        assertEquals(opponent, result);
    }

    @Test
    @DisplayName("findNearestTarget returns nearest opponent when multiple in range")
    void findNearestTarget_multipleInRange_returnsNearest() {
        Position attacker = new Position(0, 0);
        Opponent nearOpponent = createOpponent(30, 30, Vital.TOP, 5);
        Opponent farOpponent = createOpponent(80, 80, Vital.RIGHT, 5);
        List<Opponent> opponents = Arrays.asList(farOpponent, nearOpponent);

        Opponent result = combatResolver.findNearestTarget(attacker, opponents, 100);

        assertNotNull(result);
        assertEquals(nearOpponent, result);
    }

    @Test
    @DisplayName("findNearestTarget ignores defeated opponents")
    void findNearestTarget_defeatedOpponents_ignoresThem() {
        Position attacker = new Position(0, 0);
        Opponent defeated = createOpponent(20, 20, Vital.TOP, 0);
        Opponent alive = createOpponent(50, 50, Vital.RIGHT, 5);
        List<Opponent> opponents = Arrays.asList(defeated, alive);

        Opponent result = combatResolver.findNearestTarget(attacker, opponents, 100);

        assertNotNull(result);
        assertEquals(alive, result);
    }

    @Test
    @DisplayName("findNearestTarget returns null when all opponents defeated")
    void findNearestTarget_allDefeated_returnsNull() {
        Position attacker = new Position(0, 0);
        Opponent defeated1 = createOpponent(20, 20, Vital.TOP, 0);
        Opponent defeated2 = createOpponent(30, 30, Vital.RIGHT, 0);
        List<Opponent> opponents = Arrays.asList(defeated1, defeated2);

        Opponent result = combatResolver.findNearestTarget(attacker, opponents, 100);

        assertNull(result);
    }

    @Test
    @DisplayName("findNearestTarget respects range boundary - exactly at range")
    void findNearestTarget_exactlyAtRange_returnsOpponent() {
        Position attacker = new Position(0, 0);
        Opponent opponent = createOpponent(100, 0, Vital.TOP, 5);
        List<Opponent> opponents = Arrays.asList(opponent);

        Opponent result = combatResolver.findNearestTarget(attacker, opponents, 100);

        assertNotNull(result);
        assertEquals(opponent, result);
    }

    @Test
    @DisplayName("findNearestTarget excludes opponent just beyond range")
    void findNearestTarget_beyondRange_returnsNull() {
        Position attacker = new Position(0, 0);
        Opponent opponent = createOpponent(100.1, 0, Vital.TOP, 5);
        List<Opponent> opponents = Arrays.asList(opponent);

        Opponent result = combatResolver.findNearestTarget(attacker, opponents, 100);

        assertNull(result);
    }

    // ==================== applyDamage Tests ====================

    @Test
    @DisplayName("applyDamage deals VITAL_DAMAGE on vital hit")
    void applyDamage_vitalHit_dealsVitalDamage() {
        Opponent target = createOpponent(0, 0, Vital.TOP, 5);

        CombatResolver.DamageResult result = combatResolver.applyDamage(target, true);

        assertEquals(5, result.getPreviousHp());
        assertEquals(0, result.getNewHp());
        assertTrue(result.isVitalHit());
        assertEquals(5, result.getDamage());
        assertTrue(target.isDefeated());
    }

    @Test
    @DisplayName("applyDamage deals NON_VITAL_DAMAGE on non-vital hit")
    void applyDamage_nonVitalHit_dealsNonVitalDamage() {
        Opponent target = createOpponent(0, 0, Vital.TOP, 5);

        CombatResolver.DamageResult result = combatResolver.applyDamage(target, false);

        assertEquals(5, result.getPreviousHp());
        assertEquals(4, result.getNewHp());
        assertFalse(result.isVitalHit());
        assertEquals(1, result.getDamage());
        assertFalse(target.isDefeated());
    }

    @Test
    @DisplayName("applyDamage vital hit one-shots opponent at default HP")
    void applyDamage_vitalHitOneShots_defaultHp() {
        Opponent target = createOpponent(0, 0, Vital.TOP, 5);

        combatResolver.applyDamage(target, true);

        assertTrue(target.isDefeated());
        assertEquals(0, target.getHp());
    }

    @Test
    @DisplayName("applyDamage non-vital hit does not defeat opponent at full HP")
    void applyDamage_nonVitalHit_doesNotDefeat() {
        Opponent target = createOpponent(0, 0, Vital.TOP, 5);

        combatResolver.applyDamage(target, false);

        assertFalse(target.isDefeated());
        assertEquals(4, target.getHp());
    }

    @Test
    @DisplayName("applyDamage non-vital hit defeats low HP opponent")
    void applyDamage_nonVitalHit_defeatsLowHpOpponent() {
        Opponent target = createOpponent(0, 0, Vital.TOP, 1);

        CombatResolver.DamageResult result = combatResolver.applyDamage(target, false);

        assertTrue(target.isDefeated());
        assertEquals(0, target.getHp());
        assertEquals(1, result.getDamage());
    }

    @Test
    @DisplayName("applyDamage does not go below 0 HP")
    void applyDamage_alreadyDefeated_hpStaysZero() {
        Opponent target = createOpponent(0, 0, Vital.TOP, 0);

        CombatResolver.DamageResult result = combatResolver.applyDamage(target, true);

        assertEquals(0, result.getPreviousHp());
        assertEquals(0, result.getNewHp());
        assertEquals(0, target.getHp());
    }

    // ==================== calculateSpeedBoost Tests ====================

    @Test
    @DisplayName("calculateSpeedBoost activates on vital hit")
    void calculateSpeedBoost_vitalHit_activates() {
        boolean result = combatResolver.calculateSpeedBoost(true, sessionState);

        assertTrue(result);
        assertTrue(sessionState.getSpeedBoostRemaining() > 0);
    }

    @Test
    @DisplayName("calculateSpeedBoost does not activate on non-vital hit with no active boost")
    void calculateSpeedBoost_nonVitalHit_noActiveBoost_returnsFalse() {
        boolean result = combatResolver.calculateSpeedBoost(false, sessionState);

        assertFalse(result);
        assertEquals(0, sessionState.getSpeedBoostRemaining(), 0.001);
    }

    @Test
    @DisplayName("calculateSpeedBoost persists through non-vital hit while active")
    void calculateSpeedBoost_nonVitalHit_whileActive_staysActive() {
        combatResolver.calculateSpeedBoost(true, sessionState);

        boolean result = combatResolver.calculateSpeedBoost(false, sessionState);

        assertTrue(result);
    }

    @Test
    @DisplayName("calculateSpeedBoost refreshes duration on consecutive vital hits")
    void calculateSpeedBoost_consecutiveVitalHits_refreshes() {
        combatResolver.calculateSpeedBoost(true, sessionState);
        double remainingAfterFirst = sessionState.getSpeedBoostRemaining();

        combatResolver.tickSpeedBoost(sessionState, 0.5);
        double remainingAfterTick = sessionState.getSpeedBoostRemaining();
        assertTrue(remainingAfterTick < remainingAfterFirst);

        combatResolver.calculateSpeedBoost(true, sessionState);

        assertEquals(1.5, sessionState.getSpeedBoostRemaining(), 0.001);
    }

    @Test
    @DisplayName("calculateSpeedBoost expires after duration ticks down")
    void calculateSpeedBoost_expiresAfterDuration() {
        combatResolver.calculateSpeedBoost(true, sessionState);

        combatResolver.tickSpeedBoost(sessionState, 1.5);

        boolean result = combatResolver.calculateSpeedBoost(false, sessionState);
        assertFalse(result);
        assertEquals(0, sessionState.getSpeedBoostRemaining(), 0.001);
    }

    @Test
    @DisplayName("calculateSpeedBoost remains active if duration has not fully expired")
    void calculateSpeedBoost_partialTick_staysActive() {
        combatResolver.calculateSpeedBoost(true, sessionState);

        combatResolver.tickSpeedBoost(sessionState, 1.0);

        boolean result = combatResolver.calculateSpeedBoost(false, sessionState);
        assertTrue(result);
        assertEquals(0.5, sessionState.getSpeedBoostRemaining(), 0.001);
    }

    @Test
    @DisplayName("tickSpeedBoost does not go below zero")
    void tickSpeedBoost_doesNotGoBelowZero() {
        combatResolver.calculateSpeedBoost(true, sessionState);

        combatResolver.tickSpeedBoost(sessionState, 5.0);

        assertEquals(0, sessionState.getSpeedBoostRemaining(), 0.001);
    }

    @Test
    @DisplayName("tickSpeedBoost does nothing when no boost is active")
    void tickSpeedBoost_noActiveBoost_noChange() {
        combatResolver.tickSpeedBoost(sessionState, 1.0);

        assertEquals(0, sessionState.getSpeedBoostRemaining(), 0.001);
    }

    // ==================== Helper Methods ====================

    private Opponent createOpponent(double x, double y, Vital vital, int hp) {
        return new Opponent(
            new Position(x, y),
            new Velocity(0, 0),
            vital,
            hp,
            5,
            0,
            0,
            0
        );
    }
}

