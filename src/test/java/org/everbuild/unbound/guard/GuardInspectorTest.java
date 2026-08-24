package org.everbuild.unbound.guard;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;
import net.minecraft.core.BlockPos;
import org.everbuild.unbound.marker.AreaBounds;
import org.junit.jupiter.api.Test;

class GuardInspectorTest {
    private static final AreaBounds BOUNDS = AreaBounds.between(
            new BlockPos(0, 60, 0), new BlockPos(3, 63, 3));

    @Test
    void requiresExactlyOnePlaque() {
        assertEquals(GuardInspection.Status.NO_PLAQUE, inspect(Set.of()).status());
        assertEquals(GuardInspection.Status.VALID, inspect(Set.of(new BlockPos(1, 61, 1))).status());
        assertEquals(
                GuardInspection.Status.MULTIPLE_PLAQUES,
                inspect(Set.of(new BlockPos(1, 61, 1), new BlockPos(2, 61, 2))).status());
    }

    @Test
    void rejectsUnavailableVolumesBeforeScanning() {
        assertEquals(
                GuardInspection.Status.AREA_NOT_LOADED,
                GuardInspector.inspect(BOUNDS, false, ignored -> true).status());
    }

    private static GuardInspection inspect(final Set<BlockPos> plaques) {
        return GuardInspector.inspect(BOUNDS, true, plaques::contains);
    }
}
