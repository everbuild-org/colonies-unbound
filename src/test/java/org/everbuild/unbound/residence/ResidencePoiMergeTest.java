package org.everbuild.unbound.residence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import net.minecraft.core.BlockPos;
import org.everbuild.unbound.marker.WorksitePoi;
import org.everbuild.unbound.marker.WorksitePoiType;
import org.junit.jupiter.api.Test;

class ResidencePoiMergeTest {
    @Test
    void replacesScannerOwnedBedsAndPreservesManualPoints() {
        final WorksitePoi oldBed = new WorksitePoi(WorksitePoiType.BED, new BlockPos(1, 64, 1));
        final WorksitePoi storage = new WorksitePoi(WorksitePoiType.STORAGE, new BlockPos(2, 64, 2));
        final BlockPos newBed = new BlockPos(3, 64, 3);

        assertEquals(
                List.of(storage, new WorksitePoi(WorksitePoiType.BED, newBed)),
                ResidenceMarkerService.mergeBedPois(List.of(oldBed, storage), List.of(newBed)));
    }
}
