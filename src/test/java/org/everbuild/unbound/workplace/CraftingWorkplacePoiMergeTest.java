package org.everbuild.unbound.workplace;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import net.minecraft.core.BlockPos;
import org.everbuild.unbound.marker.WorksitePoi;
import org.everbuild.unbound.marker.WorksitePoiType;
import org.junit.jupiter.api.Test;

class CraftingWorkplacePoiMergeTest {
    @Test
    void replacesScannerOwnedStationsAndPreservesAuthoredPoints() {
        final WorksitePoi storage = new WorksitePoi(WorksitePoiType.STORAGE, BlockPos.ZERO);
        final WorksitePoi entrance = new WorksitePoi(WorksitePoiType.ENTRANCE, new BlockPos(1, 0, 0));
        final WorksitePoi oldStation = new WorksitePoi(WorksitePoiType.WORKSITE, new BlockPos(2, 0, 0));
        final BlockPos newStation = new BlockPos(3, 0, 0);

        assertEquals(
                List.of(storage, entrance, new WorksitePoi(WorksitePoiType.WORKSITE, newStation)),
                SurvivalCraftingMarkerService.mergeWorkstations(
                        List.of(storage, entrance, oldStation), List.of(newStation)));
    }
}
