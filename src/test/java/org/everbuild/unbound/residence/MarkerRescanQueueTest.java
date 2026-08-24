package org.everbuild.unbound.residence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class MarkerRescanQueueTest {
    @Test
    void repeatedChangesMoveOneScanToTheLatestDeadline() {
        final MarkerRescanQueue queue = new MarkerRescanQueue();
        final UUID markerId = UUID.randomUUID();

        queue.schedule(markerId, 10L);
        queue.schedule(markerId, 14L);

        assertTrue(queue.drainDue(13L).isEmpty());
        assertEquals(List.of(markerId), queue.drainDue(14L));
        assertTrue(queue.isEmpty());
    }

    @Test
    void drainsOnlyMarkersWhoseDelayHasElapsed() {
        final MarkerRescanQueue queue = new MarkerRescanQueue();
        final UUID first = UUID.randomUUID();
        final UUID second = UUID.randomUUID();
        queue.schedule(first, 5L);
        queue.schedule(second, 8L);

        assertEquals(List.of(first), queue.drainDue(5L));
        assertEquals(List.of(second), queue.drainDue(8L));
    }
}
