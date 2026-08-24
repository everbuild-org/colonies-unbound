package org.everbuild.unbound.animal;

import java.util.List;
import net.minecraft.core.BlockPos;
import org.everbuild.unbound.marker.AreaBounds;

/** Result of locating one apiary plaque and its vanilla hives. */
public record ApiaryInspection(
        Status status,
        AreaBounds bounds,
        List<BlockPos> plaquePositions,
        List<BlockPos> hivePositions) {
    public ApiaryInspection {
        plaquePositions = List.copyOf(plaquePositions);
        hivePositions = List.copyOf(hivePositions);
    }

    public enum Status {
        VALID,
        NO_PLAQUE,
        MULTIPLE_PLAQUES,
        NO_HIVES,
        AREA_NOT_LOADED,
        AREA_TOO_LARGE
    }
}
