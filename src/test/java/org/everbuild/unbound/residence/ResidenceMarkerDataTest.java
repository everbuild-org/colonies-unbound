package org.everbuild.unbound.residence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import org.everbuild.unbound.marker.AreaBounds;
import org.junit.jupiter.api.Test;

class ResidenceMarkerDataTest {
    @Test
    void roundTripsPersistentMarkerFields() {
        final UUID markerId = UUID.randomUUID();
        final UUID ownerId = UUID.randomUUID();
        final SurvivalResidenceMarker marker = new SurvivalResidenceMarker(
                markerId,
                42,
                ownerId,
                AreaBounds.between(new BlockPos(-4, 60, 7), new BlockPos(8, 73, 19)),
                List.of(new BlockPos(1, 64, 9), new BlockPos(4, 64, 12)),
                12345L);
        final ResidenceMarkerData original = new ResidenceMarkerData();
        original.put(marker);

        final CompoundTag serialized = original.save(new CompoundTag(), null);
        final ResidenceMarkerData restored = ResidenceMarkerData.load(serialized, null);

        assertEquals(List.of(marker), List.copyOf(restored.markers()));
    }

    @Test
    void replacingStableMarkerIdDoesNotCreateDuplicate() {
        final UUID markerId = UUID.randomUUID();
        final UUID ownerId = UUID.randomUUID();
        final ResidenceMarkerData data = new ResidenceMarkerData();
        final AreaBounds firstBounds = AreaBounds.between(BlockPos.ZERO, new BlockPos(2, 2, 2));
        final AreaBounds updatedBounds = AreaBounds.between(BlockPos.ZERO, new BlockPos(4, 4, 4));

        data.put(new SurvivalResidenceMarker(markerId, 1, ownerId, firstBounds, List.of(), 10L));
        data.put(new SurvivalResidenceMarker(markerId, 1, ownerId, updatedBounds, List.of(), 20L));

        assertEquals(1, data.markers().size());
        assertEquals(updatedBounds, data.markers().iterator().next().bounds());
        assertTrue(data.isDirty());
    }

    @Test
    void removesMarkerByStableId() {
        final UUID markerId = UUID.randomUUID();
        final ResidenceMarkerData data = new ResidenceMarkerData();
        final SurvivalResidenceMarker marker = new SurvivalResidenceMarker(
                markerId, 1, UUID.randomUUID(),
                AreaBounds.between(BlockPos.ZERO, BlockPos.ZERO), List.of(), 10L);
        data.put(marker);

        assertEquals(marker, data.remove(markerId));
        assertTrue(data.markers().isEmpty());
        assertNull(data.remove(markerId));
    }
}
