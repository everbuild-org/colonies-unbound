package org.everbuild.unbound.marker;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;

class CommittedWorksiteMarkTest {
    @Test
    void roundTripsTypeIdentityBoundsAndPois() {
        final CommittedWorksiteMark mark = new CommittedWorksiteMark(
                UUID.randomUUID(),
                MarkerType.GUARD,
                AreaBounds.between(new BlockPos(-8, 60, 12), new BlockPos(5, 72, 24)),
                List.of(
                        new WorksitePoi(WorksitePoiType.PATROL, new BlockPos(-2, 64, 18)),
                        new WorksitePoi(WorksitePoiType.PATROL, new BlockPos(1, 64, 20))));

        assertEquals(mark, CommittedWorksiteMark.load(mark.save()));
    }

    @Test
    void roundTripsSheepPenAndScannedPasture() {
        final CommittedWorksiteMark mark = new CommittedWorksiteMark(
                UUID.randomUUID(),
                MarkerType.SHEEP_PEN,
                AreaBounds.between(BlockPos.ZERO, new BlockPos(6, 3, 6)),
                List.of(new WorksitePoi(WorksitePoiType.PASTURE, new BlockPos(2, 0, 2))));

        assertEquals(mark, CommittedWorksiteMark.load(mark.save()));
    }
}
