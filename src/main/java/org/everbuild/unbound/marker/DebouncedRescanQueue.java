package org.everbuild.unbound.marker;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Coalesces repeated world changes into one delayed scan per logical owner. */
public final class DebouncedRescanQueue<K> {
    private final Map<K, Long> deadlines = new LinkedHashMap<>();

    public void schedule(final K key, final long deadline) {
        deadlines.merge(key, deadline, Math::max);
    }

    public List<K> drainDue(final long gameTime) {
        final List<K> due = new ArrayList<>();
        deadlines.entrySet().removeIf(entry -> {
            if (entry.getValue() <= gameTime) {
                due.add(entry.getKey());
                return true;
            }
            return false;
        });
        return due;
    }

    public boolean isEmpty() {
        return deadlines.isEmpty();
    }
}
