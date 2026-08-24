package org.everbuild.unbound.residence;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class OrphanRetryTrackerTest {
    @Test
    void requiresRepeatedFailuresAndCanRecoverBeforeRemoval() {
        final OrphanRetryTracker tracker = new OrphanRetryTracker(3);
        final UUID markerId = UUID.randomUUID();

        assertFalse(tracker.recordFailure(markerId));
        assertFalse(tracker.recordFailure(markerId));
        tracker.clear(markerId);
        assertFalse(tracker.recordFailure(markerId));
        assertFalse(tracker.recordFailure(markerId));
        assertTrue(tracker.recordFailure(markerId));
    }
}
