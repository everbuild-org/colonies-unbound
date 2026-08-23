package org.everbuild.unbound.residence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.core.BlockPos;
import org.everbuild.unbound.marker.AreaBounds;
import org.junit.jupiter.api.Test;

class ResidenceInspectorTest {
    @Test
    void discoversOnlyReportedBedHeads() {
        final BlockPos firstBed = new BlockPos(1, 1, 1);
        final BlockPos secondBed = new BlockPos(2, 1, 2);
        final Set<BlockPos> beds = Set.of(firstBed, secondBed);
        final AreaBounds bounds = AreaBounds.between(BlockPos.ZERO, new BlockPos(3, 2, 3));

        final ResidenceInspection inspection = ResidenceInspector.inspect(
                bounds, true, beds::contains, BlockPos.ZERO::equals);

        assertEquals(ResidenceInspection.Status.VALID, inspection.status());
        assertEquals(2, inspection.capacity());
        assertEquals(beds, Set.copyOf(inspection.bedHeads()));
        assertTrue(inspection.isValid());
    }

    @Test
    void reportsMissingBeds() {
        final AreaBounds bounds = AreaBounds.between(BlockPos.ZERO, new BlockPos(2, 2, 2));

        final ResidenceInspection inspection = ResidenceInspector.inspect(
                bounds, true, ignored -> false, BlockPos.ZERO::equals);

        assertEquals(ResidenceInspection.Status.NO_BEDS, inspection.status());
        assertEquals(0, inspection.capacity());
        assertFalse(inspection.isValid());
    }

    @Test
    void refusesUnloadedAreaBeforeReadingBlocks() {
        final AtomicBoolean accessed = new AtomicBoolean();
        final AreaBounds bounds = AreaBounds.between(BlockPos.ZERO, new BlockPos(2, 2, 2));

        final ResidenceInspection inspection = ResidenceInspector.inspect(bounds, false, ignored -> {
            accessed.set(true);
            return false;
        }, ignored -> false);

        assertEquals(ResidenceInspection.Status.AREA_NOT_LOADED, inspection.status());
        assertFalse(accessed.get());
    }

    @Test
    void refusesExcessiveVolumeBeforeReadingBlocks() {
        final AtomicBoolean accessed = new AtomicBoolean();
        final AreaBounds bounds = AreaBounds.between(BlockPos.ZERO, new BlockPos(127, 127, 127));

        final ResidenceInspection inspection = ResidenceInspector.inspect(bounds, true, ignored -> {
            accessed.set(true);
            return false;
        }, ignored -> false);

        assertEquals(ResidenceInspection.Status.AREA_TOO_LARGE, inspection.status());
        assertFalse(accessed.get());
    }

    @Test
    void requiresExactlyOneResidencePlaque() {
        final AreaBounds bounds = AreaBounds.between(BlockPos.ZERO, new BlockPos(2, 2, 2));

        final ResidenceInspection missing = ResidenceInspector.inspect(
                bounds, true, BlockPos.ZERO::equals, ignored -> false);
        final ResidenceInspection multiple = ResidenceInspector.inspect(
                bounds,
                true,
                BlockPos.ZERO::equals,
                Set.of(BlockPos.ZERO, new BlockPos(1, 0, 0))::contains);

        assertEquals(ResidenceInspection.Status.NO_PLAQUE, missing.status());
        assertEquals(ResidenceInspection.Status.MULTIPLE_PLAQUES, multiple.status());
    }
}
