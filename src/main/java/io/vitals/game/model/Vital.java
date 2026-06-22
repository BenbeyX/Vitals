package io.vitals.game.model;

public enum Vital {
    TOP,
    RIGHT,
    BOTTOM,
    LEFT;

    public static Vital fromString(String string) {
        return Vital.valueOf(string.toUpperCase());
    }
}
