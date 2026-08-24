package org.everbuild.unbound.residence;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Coalesces repeated block changes into one delayed scan per committed marker. */
final class MarkerRescanQueue {
    private final Map<UUID, Long> deadlines = new LinkedHashMap<>();

    void schedule(final UUID markerId, final long deadline) {
        deadlines.merge(markerId, deadline, Math::max);
    }

    List<UUID> drainDue(final long gameTime) {
        final List<UUID> due = new ArrayList<>();
        deadlines.entrySet().removeIf(entry -> {
            if (entry.getValue() <= gameTime) {
                due.add(entry.getKey());
                return true;
            }
            return false;
        });
        return due;
    }

    boolean isEmpty() {
        return deadlines.isEmpty();
    }
}
