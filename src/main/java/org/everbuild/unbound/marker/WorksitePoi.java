package org.everbuild.unbound.marker;

import net.minecraft.core.BlockPos;

/** One precisely discovered POI inside a committed worksite volume. */
public record WorksitePoi(WorksitePoiType type, BlockPos position) {
    public WorksitePoi {
        position = position.immutable();
    }
}
