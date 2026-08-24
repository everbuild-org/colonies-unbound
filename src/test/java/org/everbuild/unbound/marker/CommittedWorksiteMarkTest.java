package org.everbuild.unbound.marker;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;

class CommittedWorksiteMarkTest {
    @Test
    void roundTripsTypeIdentityAndBounds() {
        final CommittedWorksiteMark mark = new CommittedWorksiteMark(
                UUID.randomUUID(),
                MarkerType.RESIDENCE,
                AreaBounds.between(new BlockPos(-8, 60, 12), new BlockPos(5, 72, 24)));

        assertEquals(mark, CommittedWorksiteMark.load(mark.save()));
    }
}
