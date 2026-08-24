package org.everbuild.unbound.workplace;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;
import net.minecraft.core.BlockPos;
import org.everbuild.unbound.marker.AreaBounds;
import org.junit.jupiter.api.Test;

class CookInspectorTest {
    private static final AreaBounds BOUNDS = AreaBounds.between(
            new BlockPos(0, 60, 0), new BlockPos(3, 63, 3));

    @Test
    void requiresExactlyOnePlaque() {
        assertEquals(
                CookInspection.Status.NO_PLAQUE,
                inspect(Set.of()).status());
        assertEquals(
                CookInspection.Status.VALID,
                inspect(Set.of(new BlockPos(1, 61, 1))).status());
        assertEquals(
                CookInspection.Status.MULTIPLE_PLAQUES,
                inspect(Set.of(new BlockPos(1, 61, 1), new BlockPos(2, 61, 2))).status());
    }

    @Test
    void rejectsUnavailableOrOversizedVolumesBeforeScanning() {
        assertEquals(
                CookInspection.Status.AREA_NOT_LOADED,
                CookInspector.inspect(BOUNDS, false, ignored -> true).status());
        final AreaBounds oversized = AreaBounds.between(
                BlockPos.ZERO, new BlockPos(127, 127, 127));
        assertEquals(
                CookInspection.Status.AREA_TOO_LARGE,
                CookInspector.inspect(oversized, true, ignored -> true).status());
    }

    private static CookInspection inspect(final Set<BlockPos> plaques) {
        return CookInspector.inspect(BOUNDS, true, plaques::contains);
    }
}
