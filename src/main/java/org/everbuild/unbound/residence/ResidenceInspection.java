package org.everbuild.unbound.residence;

import java.util.List;
import net.minecraft.core.BlockPos;
import org.everbuild.unbound.marker.AreaBounds;

/** Immutable output from inspecting a candidate survival residence. */
public record ResidenceInspection(
        Status status, AreaBounds bounds, List<BlockPos> bedHeads, List<BlockPos> plaquePositions) {
    public ResidenceInspection {
        bedHeads = List.copyOf(bedHeads);
        plaquePositions = List.copyOf(plaquePositions);
    }

    public int capacity() {
        return bedHeads.size();
    }

    public boolean isValid() {
        return status == Status.VALID;
    }

    public enum Status {
        VALID,
        NO_BEDS,
        AREA_NOT_LOADED,
        AREA_TOO_LARGE,
        NO_PLAQUE,
        MULTIPLE_PLAQUES
    }
}
