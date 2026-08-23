package org.everbuild.unbound.residence;

import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import org.everbuild.unbound.marker.AreaBounds;

/** Persistent ownership and inspection data for one survival-defined Residence. */
public record SurvivalResidenceMarker(
        UUID id,
        int colonyId,
        UUID ownerId,
        AreaBounds bounds,
        List<BlockPos> bedHeads,
        long inspectedAt) {
    public SurvivalResidenceMarker {
        bedHeads = List.copyOf(bedHeads);
    }
}
