package org.everbuild.unbound.workplace;

import java.util.List;
import net.minecraft.core.BlockPos;
import org.everbuild.unbound.marker.AreaBounds;

public record CraftingWorkplaceInspection(
        Status status,
        AreaBounds bounds,
        List<BlockPos> plaquePositions,
        List<BlockPos> workstationPositions,
        CraftingWorkplaceDefinition definition) {
    public CraftingWorkplaceInspection {
        plaquePositions = List.copyOf(plaquePositions);
        workstationPositions = List.copyOf(workstationPositions);
    }

    public enum Status { VALID, NO_PLAQUE, MULTIPLE_PLAQUES, NO_WORKSTATION, AREA_NOT_LOADED, AREA_TOO_LARGE }
}
