package org.everbuild.unbound.marker;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;

class SurvivalBuildingLevelsTest {
    private static final AreaBounds BOUNDS = AreaBounds.between(BlockPos.ZERO, BlockPos.ZERO);

    @Test
    void readyBuildingUsesConfiguredLevel() {
        final CommittedWorksiteMark mark = mark(
                MarkerType.BLACKSMITH,
                4,
                new WorksitePoi(WorksitePoiType.STORAGE, BlockPos.ZERO),
                new WorksitePoi(WorksitePoiType.WORKSITE, BlockPos.ZERO));

        assertEquals(4, mark.effectiveLevel());
    }

    @Test
    void missingRequirementsDeactivateWithoutForgettingConfiguredLevel() {
        final CommittedWorksiteMark mark = mark(
                MarkerType.BLACKSMITH,
                4,
                new WorksitePoi(WorksitePoiType.STORAGE, BlockPos.ZERO));

        assertEquals(4, mark.configuredLevel());
        assertEquals(0, mark.effectiveLevel());
        assertEquals(4, mark.withPois(List.of(
                new WorksitePoi(WorksitePoiType.STORAGE, BlockPos.ZERO),
                new WorksitePoi(WorksitePoiType.WORKSITE, BlockPos.ZERO))).effectiveLevel());
    }

    @Test
    void clampsNormalBuildingsAndFixedInfrastructure() {
        assertEquals(1, mark(MarkerType.GUARD, -20).configuredLevel());
        assertEquals(5, mark(MarkerType.GUARD, 20).configuredLevel());
        assertEquals(1, mark(MarkerType.POST_BOX, 5).configuredLevel());
    }

    private static CommittedWorksiteMark mark(
            final MarkerType type,
            final int level,
            final WorksitePoi... points) {
        return new CommittedWorksiteMark(UUID.randomUUID(), type, BOUNDS, List.of(points), level);
    }
}
