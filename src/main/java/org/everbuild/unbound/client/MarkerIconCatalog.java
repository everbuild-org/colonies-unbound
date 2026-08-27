package org.everbuild.unbound.client;

/** Maps semantic marker IDs to the icon texture shared by that marker category. */
final class MarkerIconCatalog {
    private MarkerIconCatalog() {
    }

    static String textureId(final String iconId) {
        return switch (iconId) {
            case "sheep_pen", "chicken_pen", "pig_pen", "rabbit_hutch", "stable", "stall" -> "animal_pen";
            case "apiary", "hive" -> "farm";
            case "blacksmith", "sawmill", "stonemason", "fletcher", "mechanic",
                    "concrete_mixer", "crusher", "sifter", "bakery", "kitchen", "smeltery",
                    "stone_smelter", "glassblower", "dyer", "alchemist", "farmer", "plantation",
                    "fisherman", "lumberjack", "florist", "composter" -> "worksite";
            default -> iconId;
        };
    }
}
