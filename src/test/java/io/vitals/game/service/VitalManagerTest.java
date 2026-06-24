package io.vitals.game.service;

import io.vitals.game.model.Position;
import io.vitals.game.model.Vital;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class VitalManagerTest {

    private VitalManager vitalManager;
    private MockRandom mockRandom;

    @BeforeEach
    void setUp() {
        mockRandom = new MockRandom();
        vitalManager = new VitalManager(mockRandom);
    }

    @Test
    @DisplayName("selectNextVital returns null when no vitals are active")
    void selectNextVital_noActiveVitals_returnsNull() {
        Vital result = vitalManager.selectNextVital(Vital.TOP);
        
        assertNull(result);
    }

    @Test
    @DisplayName("selectNextVital alternates from Group A to Group B")
    void selectNextVital_fromGroupA_selectsFromGroupB() {
        vitalManager.activateVital(Vital.BOTTOM);
        mockRandom.setNextInt(0);
        
        Vital result = vitalManager.selectNextVital(Vital.TOP);
        
        assertEquals(Vital.BOTTOM, result);
    }

    @Test
    @DisplayName("selectNextVital alternates from Group B to Group A")
    void selectNextVital_fromGroupB_selectsFromGroupA() {
        vitalManager.activateVital(Vital.TOP);
        mockRandom.setNextInt(0);
        
        Vital result = vitalManager.selectNextVital(Vital.BOTTOM);
        
        assertEquals(Vital.TOP, result);
    }

    @Test
    @DisplayName("selectNextVital randomly selects between available vitals in group")
    void selectNextVital_randomSelection() {
        vitalManager.activateVital(Vital.TOP);
        vitalManager.activateVital(Vital.RIGHT);
        vitalManager.activateVital(Vital.BOTTOM);
        
        mockRandom.setNextInt(0);
        Vital first = vitalManager.selectNextVital(Vital.BOTTOM);
        
        mockRandom.setNextInt(1);
        Vital second = vitalManager.selectNextVital(Vital.LEFT);
        
        assertEquals(Vital.TOP, first);
        assertEquals(Vital.RIGHT, second);
    }

    @Test
    @DisplayName("activateAllVitals activates all four vitals")
    void activateAllVitals_activatesAll() {
        vitalManager.activateAllVitals();
        
        assertTrue(vitalManager.getActiveVitals().contains(Vital.TOP));
        assertTrue(vitalManager.getActiveVitals().contains(Vital.RIGHT));
        assertTrue(vitalManager.getActiveVitals().contains(Vital.BOTTOM));
        assertTrue(vitalManager.getActiveVitals().contains(Vital.LEFT));
        assertEquals(4, vitalManager.getActiveVitals().size());
    }

    @Test
    @DisplayName("deactivateVital removes vital from active set")
    void deactivateVital_removesVital() {
        vitalManager.activateAllVitals();
        vitalManager.deactivateVital(Vital.TOP);
        
        assertFalse(vitalManager.getActiveVitals().contains(Vital.TOP));
        assertEquals(3, vitalManager.getActiveVitals().size());
    }

    @Test
    @DisplayName("clearActiveVitals removes all vitals")
    void clearActiveVitals_removesAll() {
        vitalManager.activateAllVitals();
        vitalManager.clearActiveVitals();
        
        assertTrue(vitalManager.getActiveVitals().isEmpty());
        assertFalse(vitalManager.hasActiveVitals());
    }

    @Test
    @DisplayName("hasActiveVitals returns false when empty")
    void hasActiveVitals_whenEmpty_returnsFalse() {
        assertFalse(vitalManager.hasActiveVitals());
    }

    @Test
    @DisplayName("hasActiveVitals returns true when vitals active")
    void hasActiveVitals_whenActive_returnsTrue() {
        vitalManager.activateVital(Vital.TOP);
        assertTrue(vitalManager.hasActiveVitals());
    }

    @ParameterizedTest
    @CsvSource({
        "0, 0, 100, 0, 0.0",
        "0, 0, 0, 100, 90.0",
        "0, 0, -100, 0, 180.0",
        "0, 0, 0, -100, 270.0",
        "0, 0, 100, 100, 45.0",
        "0, 0, -100, 100, 135.0",
        "0, 0, -100, -100, 225.0",
        "0, 0, 100, -100, 315.0"
    })
    @DisplayName("calculateAngle computes correct angle")
    void calculateAngle_computesCorrectly(double fromX, double fromY, double toX, double toY, double expectedAngle) {
        Position from = new Position(fromX, fromY);
        Position to = new Position(toX, toY);
        
        double angle = vitalManager.calculateAngle(from, to);
        
        assertEquals(expectedAngle, angle, 0.1);
    }

    @Test
    @DisplayName("isVitalHit returns true for TOP vital at 0 degrees")
    void isVitalHit_topAtZeroDegrees_returnsTrue() {
        Position attacker = new Position(0, 0);
        Position target = new Position(100, 0);
        
        assertTrue(vitalManager.isVitalHit(attacker, target, Vital.TOP));
    }

    @Test
    @DisplayName("isVitalHit returns true for TOP vital at 315 degrees")
    void isVitalHit_topAt315Degrees_returnsTrue() {
        Position attacker = new Position(0, 0);
        Position target = new Position(100, -100);
        
        assertTrue(vitalManager.isVitalHit(attacker, target, Vital.TOP));
    }

    @Test
    @DisplayName("isVitalHit returns true for TOP vital at 44 degrees")
    void isVitalHit_topAt44Degrees_returnsTrue() {
        Position attacker = new Position(0, 0);
        Position target = new Position(100, 96);
        
        assertTrue(vitalManager.isVitalHit(attacker, target, Vital.TOP));
    }

    @Test
    @DisplayName("isVitalHit returns false for TOP vital at 45 degrees")
    void isVitalHit_topAt45Degrees_returnsFalse() {
        Position attacker = new Position(0, 0);
        Position target = new Position(100, 100);
        
        boolean result = vitalManager.isVitalHit(attacker, target, Vital.RIGHT);
        
        assertTrue(result);
        assertFalse(vitalManager.isVitalHit(attacker, target, Vital.TOP));
    }

    @Test
    @DisplayName("isVitalHit returns true for RIGHT vital at 90 degrees")
    void isVitalHit_rightAt90Degrees_returnsTrue() {
        Position attacker = new Position(0, 0);
        Position target = new Position(0, 100);
        
        assertTrue(vitalManager.isVitalHit(attacker, target, Vital.RIGHT));
    }

    @Test
    @DisplayName("isVitalHit returns true for BOTTOM vital at 180 degrees")
    void isVitalHit_bottomAt180Degrees_returnsTrue() {
        Position attacker = new Position(0, 0);
        Position target = new Position(-100, 0);
        
        assertTrue(vitalManager.isVitalHit(attacker, target, Vital.BOTTOM));
    }

    @Test
    @DisplayName("isVitalHit returns true for LEFT vital at 270 degrees")
    void isVitalHit_leftAt270Degrees_returnsTrue() {
        Position attacker = new Position(0, 0);
        Position target = new Position(0, -100);
        
        assertTrue(vitalManager.isVitalHit(attacker, target, Vital.LEFT));
    }

    @Test
    @DisplayName("isVitalHit returns false for miss outside cone")
    void isVitalHit_missOutsideCone_returnsFalse() {
        Position attacker = new Position(0, 0);
        Position target = new Position(50, 100);
        
        assertFalse(vitalManager.isVitalHit(attacker, target, Vital.TOP));
        assertFalse(vitalManager.isVitalHit(attacker, target, Vital.LEFT));
        assertFalse(vitalManager.isVitalHit(attacker, target, Vital.BOTTOM));
    }

    @Test
    @DisplayName("isVitalHit handles cone boundaries correctly")
    void isVitalHit_coneBoundaries() {
        Position attacker = new Position(0, 0);
        
        // 0° is in TOP cone
        Position topCone = new Position(100, 0);
        assertTrue(vitalManager.isVitalHit(attacker, topCone, Vital.TOP));
        assertFalse(vitalManager.isVitalHit(attacker, topCone, Vital.RIGHT));
        
        // 90° is in RIGHT cone
        Position rightCone = new Position(0, 100);
        assertTrue(vitalManager.isVitalHit(attacker, rightCone, Vital.RIGHT));
        assertFalse(vitalManager.isVitalHit(attacker, rightCone, Vital.BOTTOM));
    }

    private static class MockRandom extends Random {
        private int nextIntValue;

        void setNextInt(int value) {
            this.nextIntValue = value;
        }

        @Override
        public int nextInt(int bound) {
            return nextIntValue;
        }
    }
}
