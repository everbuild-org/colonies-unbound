package org.everbuild.unbound.workplace;

import java.util.List;
import net.minecraft.core.BlockPos;
import org.everbuild.unbound.marker.AreaBounds;

/** Result of locating a survival Cook plaque and accurately discoverable appliances. */
public record CookInspection(
        Status status,
        AreaBounds bounds,
        List<BlockPos> plaquePositions,
        List<BlockPos> furnacePositions) {
    public CookInspection {
        plaquePositions = List.copyOf(plaquePositions);
        furnacePositions = List.copyOf(furnacePositions);
    }

    public enum Status {
        VALID,
        AREA_NOT_LOADED,
        AREA_TOO_LARGE,
        NO_PLAQUE,
        MULTIPLE_PLAQUES
    }
}
