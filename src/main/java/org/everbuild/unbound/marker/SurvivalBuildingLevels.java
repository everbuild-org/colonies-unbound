package org.everbuild.unbound.marker;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/** Shared configured-level limits and operational readiness for survival buildings. */
public final class SurvivalBuildingLevels {
    public static final int DEFAULT_LEVEL = 1;
    public static final int MAXIMUM_LEVEL = 5;

    private static final Set<MarkerType> FIXED_LEVEL_TYPES = EnumSet.of(
            MarkerType.POST_BOX,
            MarkerType.SIMPLE_QUARRY,
            MarkerType.MEDIUM_QUARRY,
            MarkerType.STASH,
            MarkerType.MYSTICAL_SITE);

    private static final Set<MarkerType> STORAGE_ONLY_TYPES = EnumSet.of(
            MarkerType.DELIVERYMAN,
            MarkerType.BARRACKS);

    private static final Set<MarkerType> STORAGE_AND_WORKSITE_TYPES = EnumSet.of(
            MarkerType.BLACKSMITH,
            MarkerType.SAWMILL,
            MarkerType.STONEMASON,
            MarkerType.FLETCHER,
            MarkerType.MECHANIC,
            MarkerType.CONCRETE_MIXER,
            MarkerType.CRUSHER,
            MarkerType.SIFTER,
            MarkerType.BAKERY,
            MarkerType.KITCHEN,
            MarkerType.SMELTERY,
            MarkerType.STONE_SMELTER,
            MarkerType.GLASSBLOWER,
            MarkerType.DYER,
            MarkerType.ALCHEMIST,
            MarkerType.FARMER,
            MarkerType.PLANTATION,
            MarkerType.FISHERMAN,
            MarkerType.LUMBERJACK,
            MarkerType.FLORIST,
            MarkerType.COMPOSTER,
            MarkerType.HOSPITAL,
            MarkerType.SCHOOL,
            MarkerType.LIBRARY,
            MarkerType.UNIVERSITY,
            MarkerType.TAVERN,
            MarkerType.GRAVEYARD,
            MarkerType.ENCHANTER,
            MarkerType.NETHER_WORKER,
            MarkerType.ARCHERY,
            MarkerType.COMBAT_ACADEMY,
            MarkerType.BARRACKS_TOWER,
            MarkerType.GATE_HOUSE,
            MarkerType.BUILDER,
            MarkerType.MINER);

    private SurvivalBuildingLevels() {
    }

    public static int maximumConfiguredLevel(final MarkerType type) {
        return FIXED_LEVEL_TYPES.contains(type) ? DEFAULT_LEVEL : MAXIMUM_LEVEL;
    }

    public static int clampConfiguredLevel(final MarkerType type, final int level) {
        return Math.max(DEFAULT_LEVEL, Math.min(maximumConfiguredLevel(type), level));
    }

    public static int effectiveLevel(final CommittedWorksiteMark mark) {
        return isReady(mark.type(), mark.pois()) ? mark.configuredLevel() : 0;
    }

    public static boolean isReady(final MarkerType type, final List<WorksitePoi> points) {
        if (type == MarkerType.RESIDENCE) {
            return contains(points, WorksitePoiType.BED);
        }
        if (type == MarkerType.APIARY) {
            return contains(points, WorksitePoiType.STORAGE) && contains(points, WorksitePoiType.HIVE);
        }
        if (type == MarkerType.WAREHOUSE) {
            return contains(points, WorksitePoiType.WORKSITE);
        }
        if (STORAGE_ONLY_TYPES.contains(type)) {
            return contains(points, WorksitePoiType.STORAGE);
        }
        if (STORAGE_AND_WORKSITE_TYPES.contains(type)) {
            return contains(points, WorksitePoiType.STORAGE) && contains(points, WorksitePoiType.WORKSITE);
        }
        if (type == MarkerType.ANIMAL_PEN
                || type == MarkerType.SHEEP_PEN
                || type == MarkerType.CHICKEN_PEN
                || type == MarkerType.PIG_PEN
                || type == MarkerType.RABBIT_HUTCH
                || type == MarkerType.STABLE) {
            return contains(points, WorksitePoiType.STORAGE)
                    && contains(points, WorksitePoiType.ENTRANCE)
                    && contains(points, WorksitePoiType.PASTURE)
                    && (type != MarkerType.STABLE || contains(points, WorksitePoiType.STALL));
        }
        if (type == MarkerType.RESTAURANT) {
            return contains(points, WorksitePoiType.STORAGE)
                    && contains(points, WorksitePoiType.WORKSITE)
                    && contains(points, WorksitePoiType.ENTRANCE);
        }
        return true;
    }

    private static boolean contains(final List<WorksitePoi> points, final WorksitePoiType type) {
        return points.stream().anyMatch(point -> point.type() == type);
    }
}
