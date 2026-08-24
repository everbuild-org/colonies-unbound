package org.everbuild.unbound.marker;

import java.util.Arrays;

/** Accurately assigned point-of-interest categories that can be shown on a committed mark. */
public enum WorksitePoiType {
    BED("bed"),
    STORAGE("storage"),
    WORKSITE("worksite"),
    ENTRANCE("entrance"),
    INTERACTION("interaction");

    private final String id;

    WorksitePoiType(final String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public static WorksitePoiType fromId(final String id) {
        return Arrays.stream(values())
                .filter(type -> type.id.equals(id))
                .findFirst()
                .orElse(null);
    }
}
