package org.everbuild.unbound.workplace;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import net.minecraft.core.BlockPos;
import org.everbuild.unbound.marker.WorksitePoi;
import org.everbuild.unbound.marker.WorksitePoiType;
import org.junit.jupiter.api.Test;

class CookPoiMergeTest {
    @Test
    void replacesScannerOwnedFurnacesAndPreservesManualPoints() {
        final WorksitePoi oldFurnace = new WorksitePoi(WorksitePoiType.WORKSITE, new BlockPos(1, 64, 1));
        final WorksitePoi storage = new WorksitePoi(WorksitePoiType.STORAGE, new BlockPos(2, 64, 2));
        final WorksitePoi entrance = new WorksitePoi(WorksitePoiType.ENTRANCE, new BlockPos(3, 64, 3));
        final BlockPos newFurnace = new BlockPos(4, 64, 4);

        assertEquals(
                List.of(storage, entrance, new WorksitePoi(WorksitePoiType.WORKSITE, newFurnace)),
                SurvivalCookMarkerService.mergeFurnacePois(
                        List.of(oldFurnace, storage, entrance), List.of(newFurnace)));
    }
}
