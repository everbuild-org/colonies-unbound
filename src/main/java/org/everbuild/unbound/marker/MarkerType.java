package org.everbuild.unbound.marker;

import java.util.Arrays;

/** Semantic type of a committed worksite volume. */
public enum MarkerType {
    RESIDENCE("residence"),
    RESTAURANT("restaurant"),
    GUARD("guard"),
    ANIMAL_PEN("animal_pen"),
    SHEEP_PEN("sheep_pen"),
    CHICKEN_PEN("chicken_pen"),
    PIG_PEN("pig_pen"),
    RABBIT_HUTCH("rabbit_hutch"),
    STABLE("stable"),
    APIARY("apiary"),
    BLACKSMITH("blacksmith"),
    SAWMILL("sawmill"),
    STONEMASON("stonemason"),
    FLETCHER("fletcher"),
    MECHANIC("mechanic"),
    CONCRETE_MIXER("concrete_mixer"),
    CRUSHER("crusher"),
    SIFTER("sifter"),
    BAKERY("bakery"),
    KITCHEN("kitchen"),
    SMELTERY("smeltery"),
    STONE_SMELTER("stone_smelter"),
    GLASSBLOWER("glassblower"),
    DYER("dyer"),
    ALCHEMIST("alchemist"),
    FARMER("farmer"),
    PLANTATION("plantation"),
    FISHERMAN("fisherman"),
    LUMBERJACK("lumberjack"),
    FLORIST("florist"),
    COMPOSTER("composter"),
    HOSPITAL("hospital"),
    SCHOOL("school"),
    LIBRARY("library"),
    UNIVERSITY("university"),
    TAVERN("tavern"),
    GRAVEYARD("graveyard"),
    ENCHANTER("enchanter"),
    NETHER_WORKER("nether_worker"),
    ARCHERY("archery"),
    COMBAT_ACADEMY("combat_academy"),
    WAREHOUSE("warehouse"),
    POST_BOX("post_box"),
    DELIVERYMAN("deliveryman"),
    BARRACKS("barracks"),
    BARRACKS_TOWER("barracks_tower"),
    GATE_HOUSE("gate_house"),
    BUILDER("builder"),
    MINER("miner"),
    SIMPLE_QUARRY("simple_quarry"),
    MEDIUM_QUARRY("medium_quarry"),
    TOWN_HALL("town_hall"),
    STASH("stash"),
    MYSTICAL_SITE("mystical_site");

    private final String id;

    MarkerType(final String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public static MarkerType fromId(final String id) {
        return Arrays.stream(values())
                .filter(type -> type.id.equals(id))
                .findFirst()
                .orElse(RESIDENCE);
    }
}
