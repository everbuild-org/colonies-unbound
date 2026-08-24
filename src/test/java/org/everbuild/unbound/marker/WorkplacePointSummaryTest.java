package org.everbuild.unbound.marker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;

class WorkplacePointSummaryTest {
    @Test
    void requiresStorageWorksiteAndEntranceWhileIgnoringOtherPois() {
        final List<WorksitePoi> incomplete = List.of(
                poi(WorksitePoiType.BED, 1),
                poi(WorksitePoiType.STORAGE, 2),
                poi(WorksitePoiType.WORKSITE, 3));
        final WorkplacePointSummary incompleteSummary = WorkplacePointSummary.from(incomplete);

        assertFalse(incompleteSummary.isReady());
        assertEquals(1, incompleteSummary.storagePoints());
        assertEquals(1, incompleteSummary.worksitePoints());
        assertEquals(0, incompleteSummary.entrancePoints());

        final WorkplacePointSummary ready = WorkplacePointSummary.from(List.of(
                poi(WorksitePoiType.STORAGE, 2),
                poi(WorksitePoiType.WORKSITE, 3),
                poi(WorksitePoiType.ENTRANCE, 4),
                poi(WorksitePoiType.INTERACTION, 5)));
        assertTrue(ready.isReady());
    }

    private static WorksitePoi poi(final WorksitePoiType type, final int x) {
        return new WorksitePoi(type, new BlockPos(x, 64, 0));
    }
}
