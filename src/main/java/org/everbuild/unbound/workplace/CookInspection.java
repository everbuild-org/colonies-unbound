package org.everbuild.unbound.workplace;

import java.util.List;
import net.minecraft.core.BlockPos;
import org.everbuild.unbound.marker.AreaBounds;

/** Result of locating a survival Cook plaque inside a candidate volume. */
public record CookInspection(Status status, AreaBounds bounds, List<BlockPos> plaquePositions) {
    public CookInspection {
        plaquePositions = List.copyOf(plaquePositions);
    }

    public enum Status {
        VALID,
        AREA_NOT_LOADED,
        AREA_TOO_LARGE,
        NO_PLAQUE,
        MULTIPLE_PLAQUES
    }
}
