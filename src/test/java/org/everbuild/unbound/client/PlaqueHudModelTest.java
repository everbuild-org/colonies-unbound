package org.everbuild.unbound.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import org.everbuild.unbound.marker.AreaBounds;
import org.everbuild.unbound.marker.CommittedWorksiteMark;
import org.everbuild.unbound.marker.MarkerType;
import org.everbuild.unbound.marker.WorksitePoi;
import org.everbuild.unbound.marker.WorksitePoiType;
import org.junit.jupiter.api.Test;

class PlaqueHudModelTest {
    private static final AreaBounds BOUNDS = AreaBounds.between(BlockPos.ZERO, new BlockPos(4, 2, 5));

    @Test
    void unconfiguredPlaqueExplainsItsMissingRequirement() {
        final PlaqueHudModel model = PlaqueHudModel.from(MarkerType.RESIDENCE, null);

        assertEquals(PlaqueHudModel.State.UNCONFIGURED, model.state());
        assertEquals(0, model.satisfiedRequiredCount());
        assertEquals(1, model.requiredCount());
        assertFalse(model.requirements().getFirst().satisfied());
    }

    @Test
    void residenceBecomesActiveWithAnAutomaticallyScannedBed() {
        final PlaqueHudModel model = PlaqueHudModel.from(MarkerType.RESIDENCE, mark(
                MarkerType.RESIDENCE,
                new WorksitePoi(WorksitePoiType.BED, BlockPos.ZERO)));

        assertEquals(PlaqueHudModel.State.ACTIVE, model.state());
        assertEquals(1, model.satisfiedRequiredCount());
    }

    @Test
    void diningHallShowsRequiredProgressAndOptionalSeats() {
        final PlaqueHudModel model = PlaqueHudModel.from(MarkerType.RESTAURANT, mark(
                MarkerType.RESTAURANT,
                new WorksitePoi(WorksitePoiType.STORAGE, BlockPos.ZERO),
                new WorksitePoi(WorksitePoiType.WORKSITE, BlockPos.ZERO.offset(1, 0, 0)),
                new WorksitePoi(WorksitePoiType.INTERACTION, BlockPos.ZERO.offset(2, 0, 0))));

        assertEquals(PlaqueHudModel.State.DRAFT, model.state());
        assertEquals(2, model.satisfiedRequiredCount());
        assertEquals(3, model.requiredCount());
        assertTrue(model.requirements().getLast().optional());
        assertEquals(1, model.requirements().getLast().count());
    }

    @Test
    void guardTowerIsActiveWithoutAnOptionalPatrolRoute() {
        final PlaqueHudModel model = PlaqueHudModel.from(
                MarkerType.GUARD,
                mark(MarkerType.GUARD));

        assertEquals(PlaqueHudModel.State.ACTIVE, model.state());
        assertEquals(0, model.requiredCount());
        assertTrue(model.requirements().getFirst().optional());
    }

    @Test
    void cowPenRequiresStorageGateAndPasture() {
        final PlaqueHudModel draft = PlaqueHudModel.from(MarkerType.ANIMAL_PEN, mark(
                MarkerType.ANIMAL_PEN,
                new WorksitePoi(WorksitePoiType.ENTRANCE, BlockPos.ZERO),
                new WorksitePoi(WorksitePoiType.PASTURE, BlockPos.ZERO.offset(1, 0, 0))));
        final PlaqueHudModel active = PlaqueHudModel.from(MarkerType.ANIMAL_PEN, mark(
                MarkerType.ANIMAL_PEN,
                new WorksitePoi(WorksitePoiType.STORAGE, BlockPos.ZERO),
                new WorksitePoi(WorksitePoiType.ENTRANCE, BlockPos.ZERO.offset(1, 0, 0)),
                new WorksitePoi(WorksitePoiType.PASTURE, BlockPos.ZERO.offset(2, 0, 0))));

        assertEquals(PlaqueHudModel.State.DRAFT, draft.state());
        assertEquals(2, draft.satisfiedRequiredCount());
        assertEquals(PlaqueHudModel.State.ACTIVE, active.state());
        assertEquals(3, active.requiredCount());
    }

    private static CommittedWorksiteMark mark(final MarkerType type, final WorksitePoi... points) {
        return new CommittedWorksiteMark(UUID.randomUUID(), type, BOUNDS, List.of(points));
    }
}
