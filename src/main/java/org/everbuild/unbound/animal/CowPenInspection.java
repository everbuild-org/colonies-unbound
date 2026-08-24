package org.everbuild.unbound.animal;

import java.util.List;
import net.minecraft.core.BlockPos;
import org.everbuild.unbound.marker.AreaBounds;

/** Result of inspecting a survival Cow Pen volume. */
public record CowPenInspection(
        Status status,
        AreaBounds bounds,
        List<BlockPos> plaquePositions,
        List<BlockPos> gatePositions,
        List<BlockPos> pasturePositions) {
    public CowPenInspection {
        plaquePositions = List.copyOf(plaquePositions);
        gatePositions = List.copyOf(gatePositions);
        pasturePositions = List.copyOf(pasturePositions);
    }

    public enum Status {
        VALID,
        NO_PLAQUE,
        MULTIPLE_PLAQUES,
        NO_GATE,
        NO_PASTURE,
        AREA_NOT_LOADED,
        AREA_TOO_LARGE
    }
}
