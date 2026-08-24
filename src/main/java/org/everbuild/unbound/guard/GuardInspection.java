package org.everbuild.unbound.guard;

import java.util.List;
import net.minecraft.core.BlockPos;
import org.everbuild.unbound.marker.AreaBounds;

/** Result of locating a survival Guard Tower plaque inside a candidate volume. */
public record GuardInspection(Status status, AreaBounds bounds, List<BlockPos> plaquePositions) {
    public GuardInspection {
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
