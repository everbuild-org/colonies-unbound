package org.everbuild.unbound.client;

import java.util.ArrayList;
import java.util.List;
import org.everbuild.unbound.marker.AreaBounds;
import org.everbuild.unbound.marker.CommittedWorksiteMark;
import org.everbuild.unbound.marker.MarkerType;
import org.everbuild.unbound.marker.WorksitePoi;
import org.everbuild.unbound.marker.WorksitePoiType;

/** Immutable presentation data for the plaque inspector HUD. */
public record PlaqueHudModel(
        MarkerType type,
        State state,
        AreaBounds bounds,
        List<Requirement> requirements) {
    public PlaqueHudModel {
        requirements = List.copyOf(requirements);
    }

    public static PlaqueHudModel from(final MarkerType plaqueType, final CommittedWorksiteMark mark) {
        if (mark == null) {
            return new PlaqueHudModel(plaqueType, State.UNCONFIGURED, null, requirementsFor(plaqueType, List.of()));
        }

        final List<Requirement> requirements = requirementsFor(plaqueType, mark.pois());
        final boolean ready = requirements.stream()
                .filter(requirement -> !requirement.optional())
                .allMatch(Requirement::satisfied);
        return new PlaqueHudModel(plaqueType, ready ? State.ACTIVE : State.DRAFT, mark.bounds(), requirements);
    }

    public int satisfiedRequiredCount() {
        return (int) requirements.stream()
                .filter(requirement -> !requirement.optional() && requirement.satisfied())
                .count();
    }

    public int requiredCount() {
        return (int) requirements.stream().filter(requirement -> !requirement.optional()).count();
    }

    private static List<Requirement> requirementsFor(
            final MarkerType type,
            final List<WorksitePoi> points) {
        final List<Requirement> requirements = new ArrayList<>();
        if (type == MarkerType.RESIDENCE) {
            requirements.add(new Requirement("hud.coloniesunbound.plaque.beds", count(points, WorksitePoiType.BED), 1, false));
            return requirements;
        }
        if (type == MarkerType.GUARD) {
            requirements.add(new Requirement("hud.coloniesunbound.plaque.patrol_nodes", count(points, WorksitePoiType.PATROL), 0, true));
            return requirements;
        }
        if (type == MarkerType.APIARY) {
            requirements.add(new Requirement("hud.coloniesunbound.plaque.storage", count(points, WorksitePoiType.STORAGE), 1, false));
            requirements.add(new Requirement("hud.coloniesunbound.plaque.hives", count(points, WorksitePoiType.HIVE), 1, false));
            return requirements;
        }
        if (type == MarkerType.WAREHOUSE) {
            requirements.add(new Requirement("hud.coloniesunbound.plaque.racks", count(points, WorksitePoiType.WORKSITE), 1, false));
            return requirements;
        }
        if (type == MarkerType.POST_BOX || type == MarkerType.SIMPLE_QUARRY
                || type == MarkerType.MEDIUM_QUARRY || type == MarkerType.TOWN_HALL
                || type == MarkerType.STASH || type == MarkerType.MYSTICAL_SITE) {
            return requirements;
        }
        if (type == MarkerType.DELIVERYMAN || type == MarkerType.BARRACKS) {
            requirements.add(new Requirement("hud.coloniesunbound.plaque.storage", count(points, WorksitePoiType.STORAGE), 1, false));
            return requirements;
        }
        if (type == MarkerType.BLACKSMITH || type == MarkerType.SAWMILL
                || type == MarkerType.STONEMASON || type == MarkerType.FLETCHER
                || type == MarkerType.MECHANIC || type == MarkerType.CONCRETE_MIXER
                || type == MarkerType.CRUSHER || type == MarkerType.SIFTER
                || type == MarkerType.BAKERY || type == MarkerType.KITCHEN
                || type == MarkerType.SMELTERY || type == MarkerType.STONE_SMELTER
                || type == MarkerType.GLASSBLOWER || type == MarkerType.DYER
                || type == MarkerType.ALCHEMIST || type == MarkerType.FARMER
                || type == MarkerType.PLANTATION || type == MarkerType.FISHERMAN
                || type == MarkerType.LUMBERJACK || type == MarkerType.FLORIST
                || type == MarkerType.COMPOSTER || type == MarkerType.HOSPITAL
                || type == MarkerType.SCHOOL || type == MarkerType.LIBRARY
                || type == MarkerType.UNIVERSITY || type == MarkerType.TAVERN
                || type == MarkerType.GRAVEYARD || type == MarkerType.ENCHANTER
                || type == MarkerType.NETHER_WORKER) {
            requirements.add(new Requirement("hud.coloniesunbound.plaque.storage", count(points, WorksitePoiType.STORAGE), 1, false));
            final boolean natural = type == MarkerType.FARMER || type == MarkerType.PLANTATION
                    || type == MarkerType.FISHERMAN || type == MarkerType.LUMBERJACK
                    || type == MarkerType.FLORIST || type == MarkerType.COMPOSTER;
            final String resourceKey = switch (type) {
                case HOSPITAL, TAVERN -> "hud.coloniesunbound.plaque.beds";
                case SCHOOL -> "hud.coloniesunbound.plaque.classroom_seats";
                case LIBRARY, UNIVERSITY -> "hud.coloniesunbound.plaque.bookshelves";
                case GRAVEYARD -> "hud.coloniesunbound.plaque.graves";
                case ENCHANTER -> "hud.coloniesunbound.plaque.enchanting_tables";
                case NETHER_WORKER -> "hud.coloniesunbound.plaque.nether_portals";
                default -> natural ? "hud.coloniesunbound.plaque.resources" : "hud.coloniesunbound.plaque.workstations";
            };
            requirements.add(new Requirement(
                    resourceKey,
                    count(points, WorksitePoiType.WORKSITE), 1, false));
            return requirements;
        }
        if (type == MarkerType.ARCHERY || type == MarkerType.COMBAT_ACADEMY
                || type == MarkerType.BARRACKS_TOWER || type == MarkerType.GATE_HOUSE
                || type == MarkerType.BUILDER || type == MarkerType.MINER) {
            requirements.add(new Requirement("hud.coloniesunbound.plaque.storage", count(points, WorksitePoiType.STORAGE), 1, false));
            final String key = switch (type) {
                case ARCHERY -> "hud.coloniesunbound.plaque.archery_targets";
                case COMBAT_ACADEMY -> "hud.coloniesunbound.plaque.training_dummies";
                case BARRACKS_TOWER -> "hud.coloniesunbound.plaque.beds";
                case GATE_HOUSE -> "hud.coloniesunbound.plaque.guard_posts";
                case BUILDER -> "hud.coloniesunbound.plaque.workbenches";
                case MINER -> "hud.coloniesunbound.plaque.shaft_ladders";
                default -> throw new IllegalStateException("Handled Wave 6 type");
            };
            requirements.add(new Requirement(key, count(points, WorksitePoiType.WORKSITE), 1, false));
            return requirements;
        }
        if (type == MarkerType.ANIMAL_PEN
                || type == MarkerType.SHEEP_PEN
                || type == MarkerType.CHICKEN_PEN
                || type == MarkerType.PIG_PEN
                || type == MarkerType.RABBIT_HUTCH
                || type == MarkerType.STABLE) {
            requirements.add(new Requirement("hud.coloniesunbound.plaque.storage", count(points, WorksitePoiType.STORAGE), 1, false));
            requirements.add(new Requirement("hud.coloniesunbound.plaque.gates", count(points, WorksitePoiType.ENTRANCE), 1, false));
            requirements.add(new Requirement("hud.coloniesunbound.plaque.pasture", count(points, WorksitePoiType.PASTURE), 1, false));
            if (type == MarkerType.STABLE) {
                requirements.add(new Requirement("hud.coloniesunbound.plaque.stalls", count(points, WorksitePoiType.STALL), 1, false));
            }
            return requirements;
        }

        requirements.add(new Requirement("hud.coloniesunbound.plaque.storage", count(points, WorksitePoiType.STORAGE), 1, false));
        requirements.add(new Requirement("hud.coloniesunbound.plaque.furnace", count(points, WorksitePoiType.WORKSITE), 1, false));
        requirements.add(new Requirement("hud.coloniesunbound.plaque.entrance", count(points, WorksitePoiType.ENTRANCE), 1, false));
        requirements.add(new Requirement("hud.coloniesunbound.plaque.seats", count(points, WorksitePoiType.INTERACTION), 0, true));
        return requirements;
    }

    private static int count(final List<WorksitePoi> points, final WorksitePoiType type) {
        return (int) points.stream().filter(point -> point.type() == type).count();
    }

    public enum State {
        ACTIVE,
        DRAFT,
        UNCONFIGURED
    }

    public record Requirement(String translationKey, int count, int minimum, boolean optional) {
        public boolean satisfied() {
            return optional || count >= minimum;
        }
    }
}
