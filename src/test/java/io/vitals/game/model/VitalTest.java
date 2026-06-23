package io.vitals.game.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class VitalTest {
    @Test
    public void testVital() {
        assertEquals(Vital.TOP, Vital.fromString("TOP"), "TOP should be the top vital");
        assertEquals(Vital.RIGHT, Vital.fromString("RIGHT"), "RIGHT should be the right vital");
        assertEquals(Vital.BOTTOM, Vital.fromString("BOTTOM"), "BOTTOM should be the bottom vital");
        assertEquals(Vital.LEFT, Vital.fromString("LEFT"), "LEFT should be the left vital");
        assertEquals(Vital.TOP, Vital.fromString("top"), "top should be the top vital");
        assertEquals(Vital.RIGHT, Vital.fromString("right"), "right should be the right vital");
        assertEquals(Vital.BOTTOM, Vital.fromString("bottom"), "bottom should be the bottom vital");
        assertEquals(Vital.LEFT, Vital.fromString("left"), "left should be the left vital");
    }
}
