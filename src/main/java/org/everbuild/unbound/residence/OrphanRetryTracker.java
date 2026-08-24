package org.everbuild.unbound.residence;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Requires repeated missing-building observations before destructive orphan cleanup. */
final class OrphanRetryTracker {
    private final int removalThreshold;
    private final Map<UUID, Integer> failures = new HashMap<>();

    OrphanRetryTracker(final int removalThreshold) {
        this.removalThreshold = removalThreshold;
    }

    boolean recordFailure(final UUID markerId) {
        return failures.merge(markerId, 1, Integer::sum) >= removalThreshold;
    }

    void clear(final UUID markerId) {
        failures.remove(markerId);
    }
}
