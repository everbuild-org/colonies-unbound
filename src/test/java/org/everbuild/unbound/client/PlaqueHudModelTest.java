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

    @Test
    void sheepPenUsesTheSharedLivestockRequirements() {
        final PlaqueHudModel model = PlaqueHudModel.from(MarkerType.SHEEP_PEN, mark(
                MarkerType.SHEEP_PEN,
                new WorksitePoi(WorksitePoiType.STORAGE, BlockPos.ZERO),
                new WorksitePoi(WorksitePoiType.ENTRANCE, BlockPos.ZERO.offset(1, 0, 0)),
                new WorksitePoi(WorksitePoiType.PASTURE, BlockPos.ZERO.offset(2, 0, 0))));

        assertEquals(PlaqueHudModel.State.ACTIVE, model.state());
        assertEquals(3, model.requiredCount());
    }

    @Test
    void stableAlsoRequiresAnAuthoredStall() {
        final PlaqueHudModel model = PlaqueHudModel.from(MarkerType.STABLE, mark(
                MarkerType.STABLE,
                new WorksitePoi(WorksitePoiType.STORAGE, BlockPos.ZERO),
                new WorksitePoi(WorksitePoiType.ENTRANCE, BlockPos.ZERO.offset(1, 0, 0)),
                new WorksitePoi(WorksitePoiType.PASTURE, BlockPos.ZERO.offset(2, 0, 0))));

        assertEquals(PlaqueHudModel.State.DRAFT, model.state());
        assertEquals(4, model.requiredCount());
    }

    @Test
    void apiaryRequiresStorageAndAScannedHive() {
        final PlaqueHudModel model = PlaqueHudModel.from(MarkerType.APIARY, mark(
                MarkerType.APIARY,
                new WorksitePoi(WorksitePoiType.STORAGE, BlockPos.ZERO),
                new WorksitePoi(WorksitePoiType.HIVE, BlockPos.ZERO.offset(1, 0, 0))));

        assertEquals(PlaqueHudModel.State.ACTIVE, model.state());
        assertEquals(2, model.requiredCount());
    }

    @Test
    void craftingWorkplaceRequiresStorageAndScannedWorkstation() {
        final PlaqueHudModel draft = PlaqueHudModel.from(MarkerType.BLACKSMITH, mark(
                MarkerType.BLACKSMITH,
                new WorksitePoi(WorksitePoiType.WORKSITE, BlockPos.ZERO)));
        final PlaqueHudModel active = PlaqueHudModel.from(MarkerType.SIFTER, mark(
                MarkerType.SIFTER,
                new WorksitePoi(WorksitePoiType.WORKSITE, BlockPos.ZERO),
                new WorksitePoi(WorksitePoiType.STORAGE, new BlockPos(1, 0, 0))));

        assertEquals(PlaqueHudModel.State.DRAFT, draft.state());
        assertEquals(2, draft.requiredCount());
        assertEquals(PlaqueHudModel.State.ACTIVE, active.state());
    }

    @Test
    void waveThreeWorkplacesUseSharedCraftingRequirements() {
        for (final MarkerType type : List.of(
                MarkerType.BAKERY, MarkerType.KITCHEN, MarkerType.SMELTERY,
                MarkerType.STONE_SMELTER, MarkerType.GLASSBLOWER, MarkerType.DYER,
                MarkerType.ALCHEMIST)) {
            final PlaqueHudModel model = PlaqueHudModel.from(type, mark(
                    type,
                    new WorksitePoi(WorksitePoiType.WORKSITE, BlockPos.ZERO),
                    new WorksitePoi(WorksitePoiType.STORAGE, new BlockPos(1, 0, 0))));

            assertEquals(PlaqueHudModel.State.ACTIVE, model.state(), type::name);
            assertEquals(2, model.requiredCount(), type::name);
        }
    }

    @Test
    void waveFourNaturalWorkplacesUseResourceAndStorageRequirements() {
        for (final MarkerType type : List.of(
                MarkerType.FARMER, MarkerType.PLANTATION, MarkerType.FISHERMAN,
                MarkerType.LUMBERJACK, MarkerType.FLORIST, MarkerType.COMPOSTER)) {
            final PlaqueHudModel draft = PlaqueHudModel.from(type, mark(
                    type, new WorksitePoi(WorksitePoiType.WORKSITE, BlockPos.ZERO)));
            final PlaqueHudModel active = PlaqueHudModel.from(type, mark(
                    type,
                    new WorksitePoi(WorksitePoiType.WORKSITE, BlockPos.ZERO),
                    new WorksitePoi(WorksitePoiType.STORAGE, new BlockPos(1, 0, 0))));

            assertEquals(PlaqueHudModel.State.DRAFT, draft.state(), type::name);
            assertEquals(PlaqueHudModel.State.ACTIVE, active.state(), type::name);
            assertEquals(2, active.requiredCount(), type::name);
        }
    }

    @Test
    void waveFiveServicesUseTheirSpecificScannedResourceAndStorage() {
        for (final MarkerType type : List.of(
                MarkerType.HOSPITAL, MarkerType.SCHOOL, MarkerType.LIBRARY,
                MarkerType.UNIVERSITY, MarkerType.TAVERN, MarkerType.GRAVEYARD,
                MarkerType.ENCHANTER, MarkerType.NETHER_WORKER)) {
            final PlaqueHudModel draft = PlaqueHudModel.from(type, mark(
                    type, new WorksitePoi(WorksitePoiType.WORKSITE, BlockPos.ZERO)));
            final PlaqueHudModel active = PlaqueHudModel.from(type, mark(
                    type,
                    new WorksitePoi(WorksitePoiType.WORKSITE, BlockPos.ZERO),
                    new WorksitePoi(WorksitePoiType.STORAGE, new BlockPos(1, 0, 0))));

            assertEquals(PlaqueHudModel.State.DRAFT, draft.state(), type::name);
            assertEquals(PlaqueHudModel.State.ACTIVE, active.state(), type::name);
            assertEquals(2, active.requiredCount(), type::name);
            assertFalse(active.requirements().getLast().translationKey()
                    .equals("hud.coloniesunbound.plaque.workstations"), type::name);
        }
    }

    @Test
    void waveSixStructuralWorkplacesUseSpecificPhysicalRequirements() {
        for (final MarkerType type : List.of(
                MarkerType.ARCHERY, MarkerType.COMBAT_ACADEMY, MarkerType.BARRACKS_TOWER,
                MarkerType.GATE_HOUSE, MarkerType.BUILDER, MarkerType.MINER)) {
            final PlaqueHudModel active = PlaqueHudModel.from(type, mark(
                    type,
                    new WorksitePoi(WorksitePoiType.WORKSITE, BlockPos.ZERO),
                    new WorksitePoi(WorksitePoiType.STORAGE, new BlockPos(1, 0, 0))));
            assertEquals(PlaqueHudModel.State.ACTIVE, active.state(), type::name);
            assertEquals(2, active.requiredCount(), type::name);
        }
    }

    @Test
    void waveSixNetworkAndSupportReadinessMatchesNativeTopology() {
        final PlaqueHudModel warehouse = PlaqueHudModel.from(MarkerType.WAREHOUSE, mark(
                MarkerType.WAREHOUSE, new WorksitePoi(WorksitePoiType.WORKSITE, BlockPos.ZERO)));
        assertEquals(PlaqueHudModel.State.ACTIVE, warehouse.state());
        assertEquals(1, warehouse.requiredCount());

        for (final MarkerType type : List.of(MarkerType.DELIVERYMAN, MarkerType.BARRACKS)) {
            final PlaqueHudModel active = PlaqueHudModel.from(type, mark(
                    type, new WorksitePoi(WorksitePoiType.STORAGE, BlockPos.ZERO)));
            assertEquals(PlaqueHudModel.State.ACTIVE, active.state(), type::name);
            assertEquals(1, active.requiredCount(), type::name);
        }

        for (final MarkerType type : List.of(
                MarkerType.POST_BOX, MarkerType.SIMPLE_QUARRY, MarkerType.MEDIUM_QUARRY,
                MarkerType.TOWN_HALL, MarkerType.STASH, MarkerType.MYSTICAL_SITE)) {
            final PlaqueHudModel active = PlaqueHudModel.from(type, mark(type));
            assertEquals(PlaqueHudModel.State.ACTIVE, active.state(), type::name);
            assertEquals(0, active.requiredCount(), type::name);
        }
    }

    private static CommittedWorksiteMark mark(final MarkerType type, final WorksitePoi... points) {
        return new CommittedWorksiteMark(UUID.randomUUID(), type, BOUNDS, List.of(points));
    }
}
