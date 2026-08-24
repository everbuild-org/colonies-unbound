package org.everbuild.unbound.marker;

import java.util.Arrays;

/** Semantic type of a committed worksite volume. */
public enum MarkerType {
    RESIDENCE("residence"),
    RESTAURANT("restaurant");

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
