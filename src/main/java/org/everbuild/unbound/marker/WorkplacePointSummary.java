package org.everbuild.unbound.marker;

import java.util.List;

/** Minimal manually-authored requirements shared by future survival workplace adapters. */
public record WorkplacePointSummary(int storagePoints, int worksitePoints, int entrancePoints) {
    public static WorkplacePointSummary from(final List<WorksitePoi> pois) {
        int storage = 0;
        int worksite = 0;
        int entrances = 0;
        for (final WorksitePoi poi : pois) {
            switch (poi.type()) {
                case STORAGE -> storage++;
                case WORKSITE -> worksite++;
                case ENTRANCE -> entrances++;
                default -> {
                }
            }
        }
        return new WorkplacePointSummary(storage, worksite, entrances);
    }

    public boolean isReady() {
        return storagePoints > 0 && worksitePoints > 0 && entrancePoints > 0;
    }
}
