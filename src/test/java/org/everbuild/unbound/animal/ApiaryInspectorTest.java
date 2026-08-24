package org.everbuild.unbound.animal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;
import net.minecraft.core.BlockPos;
import org.everbuild.unbound.marker.AreaBounds;
import org.junit.jupiter.api.Test;

class ApiaryInspectorTest {
    private static final AreaBounds BOUNDS = AreaBounds.between(BlockPos.ZERO, new BlockPos(3, 3, 3));
    private static final BlockPos PLAQUE = new BlockPos(1, 1, 1);
    private static final BlockPos HIVE = new BlockPos(2, 1, 1);

    @Test
    void requiresOnePlaqueAndAtLeastOneHive() {
        assertEquals(ApiaryInspection.Status.NO_PLAQUE, inspect(Set.of(), Set.of(HIVE)).status());
        assertEquals(ApiaryInspection.Status.MULTIPLE_PLAQUES,
                inspect(Set.of(PLAQUE, PLAQUE.above()), Set.of(HIVE)).status());
        assertEquals(ApiaryInspection.Status.NO_HIVES, inspect(Set.of(PLAQUE), Set.of()).status());
        assertEquals(ApiaryInspection.Status.VALID, inspect(Set.of(PLAQUE), Set.of(HIVE)).status());
    }

    @Test
    void rejectsUnavailableAreaBeforeScanning() {
        assertEquals(ApiaryInspection.Status.AREA_NOT_LOADED,
                ApiaryInspector.inspect(BOUNDS, false, ignored -> true, ignored -> true).status());
    }

    private static ApiaryInspection inspect(final Set<BlockPos> plaques, final Set<BlockPos> hives) {
        return ApiaryInspector.inspect(BOUNDS, true, plaques::contains, hives::contains);
    }
}
