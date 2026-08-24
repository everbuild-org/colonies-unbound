package org.everbuild.unbound.animal;

import java.util.List;
import net.minecraft.core.BlockPos;
import org.everbuild.unbound.marker.AreaBounds;

/** Result of inspecting the shared physical requirements of a survival animal pen. */
public record AnimalPenInspection(
        Status status,
        AreaBounds bounds,
        List<BlockPos> plaquePositions,
        List<BlockPos> gatePositions,
        List<BlockPos> pasturePositions) {
    public AnimalPenInspection {
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
