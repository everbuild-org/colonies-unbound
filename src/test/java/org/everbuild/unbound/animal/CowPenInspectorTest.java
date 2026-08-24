package org.everbuild.unbound.animal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;
import net.minecraft.core.BlockPos;
import org.everbuild.unbound.marker.AreaBounds;
import org.junit.jupiter.api.Test;

class CowPenInspectorTest {
    private static final AreaBounds BOUNDS = AreaBounds.between(
            new BlockPos(0, 60, 0), new BlockPos(3, 63, 3));
    private static final BlockPos PLAQUE = new BlockPos(1, 61, 1);
    private static final BlockPos GATE = new BlockPos(0, 61, 1);
    private static final BlockPos PASTURE = new BlockPos(2, 60, 2);

    @Test
    void requiresExactlyOnePlaque() {
        assertEquals(CowPenInspection.Status.NO_PLAQUE, inspect(Set.of(), Set.of(GATE), Set.of(PASTURE)).status());
        assertEquals(
                CowPenInspection.Status.MULTIPLE_PLAQUES,
                inspect(Set.of(PLAQUE, PLAQUE.offset(1, 0, 0)), Set.of(GATE), Set.of(PASTURE)).status());
    }

    @Test
    void requiresGateAndPastureFloor() {
        assertEquals(CowPenInspection.Status.NO_GATE, inspect(Set.of(PLAQUE), Set.of(), Set.of(PASTURE)).status());
        assertEquals(CowPenInspection.Status.NO_PASTURE, inspect(Set.of(PLAQUE), Set.of(GATE), Set.of()).status());
        assertEquals(
                CowPenInspection.Status.VALID,
                inspect(Set.of(PLAQUE), Set.of(GATE), Set.of(PASTURE)).status());
    }

    @Test
    void rejectsUnavailableOrOversizedVolumesBeforeScanning() {
        assertEquals(
                CowPenInspection.Status.AREA_NOT_LOADED,
                CowPenInspector.inspect(BOUNDS, false, ignored -> true, ignored -> true, ignored -> true).status());
        final AreaBounds oversized = AreaBounds.between(BlockPos.ZERO, new BlockPos(127, 127, 127));
        assertEquals(
                CowPenInspection.Status.AREA_TOO_LARGE,
                CowPenInspector.inspect(oversized, true, ignored -> true, ignored -> true, ignored -> true).status());
    }

    private static CowPenInspection inspect(
            final Set<BlockPos> plaques,
            final Set<BlockPos> gates,
            final Set<BlockPos> pasture) {
        return CowPenInspector.inspect(BOUNDS, true, plaques::contains, gates::contains, pasture::contains);
    }
}
