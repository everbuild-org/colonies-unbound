package org.everbuild.unbound.animal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import net.minecraft.core.BlockPos;
import org.everbuild.unbound.marker.AreaBounds;
import org.everbuild.unbound.marker.WorksitePoi;
import org.everbuild.unbound.marker.WorksitePoiType;
import org.junit.jupiter.api.Test;

class CowPenPoiMergeTest {
    @Test
    void replacesScannedGateAndPastureWhilePreservingStorage() {
        final WorksitePoi oldGate = new WorksitePoi(WorksitePoiType.ENTRANCE, new BlockPos(0, 64, 0));
        final WorksitePoi oldPasture = new WorksitePoi(WorksitePoiType.PASTURE, new BlockPos(1, 63, 1));
        final WorksitePoi storage = new WorksitePoi(WorksitePoiType.STORAGE, new BlockPos(2, 64, 2));
        final BlockPos newGate = new BlockPos(3, 64, 3);
        final BlockPos newPasture = new BlockPos(4, 63, 4);
        final CowPenInspection inspection = new CowPenInspection(
                CowPenInspection.Status.VALID,
                AreaBounds.between(BlockPos.ZERO, new BlockPos(5, 5, 5)),
                List.of(new BlockPos(2, 64, 1)),
                List.of(newGate),
                List.of(newPasture, newPasture.offset(1, 0, 0)));

        assertEquals(
                List.of(
                        storage,
                        new WorksitePoi(WorksitePoiType.ENTRANCE, newGate),
                        new WorksitePoi(WorksitePoiType.PASTURE, newPasture)),
                SurvivalCowPenMarkerService.mergeScannedPois(
                        List.of(oldGate, oldPasture, storage), inspection));
    }
}
