package org.everbuild.unbound.marker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;

class AreaBoundsTest {
    @Test
    void normalizesCornersAndCountsInclusiveBlocks() {
        final AreaBounds bounds = AreaBounds.between(new BlockPos(5, 8, 3), new BlockPos(2, 4, 9));

        assertEquals(new BlockPos(2, 4, 3), bounds.min());
        assertEquals(new BlockPos(5, 8, 9), bounds.max());
        assertEquals(4, bounds.sizeX());
        assertEquals(5, bounds.sizeY());
        assertEquals(7, bounds.sizeZ());
        assertEquals(140L, bounds.volume());
    }

    @Test
    void singleBlockHasUnitDimensionsAndAabb() {
        final AreaBounds bounds = AreaBounds.between(BlockPos.ZERO, BlockPos.ZERO);

        assertEquals(1L, bounds.volume());
        assertEquals(0.0, bounds.asAabb().minX);
        assertEquals(1.0, bounds.asAabb().maxX);
        assertEquals(1.0, bounds.asAabb().maxY);
        assertEquals(1.0, bounds.asAabb().maxZ);
    }

    @Test
    void axisLimitIsInclusive() {
        assertTrue(AreaBounds.between(BlockPos.ZERO, new BlockPos(127, 0, 0)).fitsWithin(128));
        assertFalse(AreaBounds.between(BlockPos.ZERO, new BlockPos(128, 0, 0)).fitsWithin(128));
    }

    @Test
    void detectsContainedPositionsAndIntersectingChunks() {
        final AreaBounds bounds = AreaBounds.between(new BlockPos(15, 60, 15), new BlockPos(20, 70, 20));

        assertTrue(bounds.contains(new BlockPos(15, 60, 15)));
        assertTrue(bounds.contains(new BlockPos(20, 70, 20)));
        assertFalse(bounds.contains(new BlockPos(21, 65, 20)));
        assertTrue(bounds.intersectsChunk(0, 0, 15, 15));
        assertTrue(bounds.intersectsChunk(16, 16, 31, 31));
        assertFalse(bounds.intersectsChunk(32, 16, 47, 31));
    }
}
