package org.everbuild.unbound.marker;

import java.util.Arrays;

/** Semantic type of a committed worksite volume. */
public enum MarkerType {
    RESIDENCE("residence"),
    RESTAURANT("restaurant"),
    GUARD("guard"),
    ANIMAL_PEN("animal_pen"),
    SHEEP_PEN("sheep_pen"),
    CHICKEN_PEN("chicken_pen"),
    PIG_PEN("pig_pen"),
    RABBIT_HUTCH("rabbit_hutch"),
    STABLE("stable"),
    APIARY("apiary"),
    BLACKSMITH("blacksmith"),
    SAWMILL("sawmill"),
    STONEMASON("stonemason"),
    FLETCHER("fletcher"),
    MECHANIC("mechanic"),
    CONCRETE_MIXER("concrete_mixer"),
    CRUSHER("crusher"),
    SIFTER("sifter");

    private final String id;

    MarkerType(final String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public static MarkerType fromId(final String id) {
        return Arrays.stream(values())
                .filter(type -> type.id.equals(id))
                .findFirst()
                .orElse(RESIDENCE);
    }
}
