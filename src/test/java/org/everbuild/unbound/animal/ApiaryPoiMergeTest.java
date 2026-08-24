package org.everbuild.unbound.animal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import net.minecraft.core.BlockPos;
import org.everbuild.unbound.marker.WorksitePoi;
import org.everbuild.unbound.marker.WorksitePoiType;
import org.junit.jupiter.api.Test;

class ApiaryPoiMergeTest {
    @Test
    void replacesScannedHivesAndPreservesStorage() {
        final WorksitePoi storage = new WorksitePoi(WorksitePoiType.STORAGE, BlockPos.ZERO);
        final WorksitePoi oldHive = new WorksitePoi(WorksitePoiType.HIVE, new BlockPos(1, 0, 0));
        final BlockPos newHive = new BlockPos(2, 0, 0);

        assertEquals(
                List.of(storage, new WorksitePoi(WorksitePoiType.HIVE, newHive)),
                SurvivalApiaryMarkerService.mergeHivePois(List.of(storage, oldHive), List.of(newHive)));
    }
}
