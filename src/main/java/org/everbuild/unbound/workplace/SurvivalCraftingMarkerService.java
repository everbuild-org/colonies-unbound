package org.everbuild.unbound.workplace;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.permissions.Action;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.everbuild.unbound.marker.AreaBounds;
import org.everbuild.unbound.marker.CommittedWorksiteMark;
import org.everbuild.unbound.marker.MarkerSelection;
import org.everbuild.unbound.marker.PoiTargetValidator;
import org.everbuild.unbound.marker.WorksitePoi;
import org.everbuild.unbound.marker.WorksitePoiType;
import org.everbuild.unbound.minecolonies.SurvivalCraftingBuilding;
import org.everbuild.unbound.minecolonies.SurvivalCraftingTileEntity;
import org.everbuild.unbound.residence.ResidenceMarkerService;

/** Shared persistence, readiness, and native binding for survival crafting workplaces. */
public final class SurvivalCraftingMarkerService {
    private SurvivalCraftingMarkerService() { }

    public static RegistrationResult register(
            final ServerLevel level, final ServerPlayer player, final MarkerSelection selection,
            final CraftingWorkplaceInspection inspection) {
        final IColony colony = IColonyManager.getInstance().getColonyByPosFromWorld(level, inspection.bounds().min());
        if (colony == null) return RegistrationResult.OUTSIDE_COLONY;
        if (!colony.getPermissions().hasPermission(player, Action.MANAGE_HUTS)) return RegistrationResult.NO_PERMISSION;
        if (!cornersBelongTo(level, inspection.bounds(), colony)) return RegistrationResult.CROSSES_COLONY_BORDER;
        final BlockPos plaque = inspection.plaquePositions().getFirst();
        final IBuilding nativeBuilding = IColonyManager.getInstance().getBuilding(level, plaque);
        if (!inspection.definition().buildingType().isInstance(nativeBuilding)
                || !(nativeBuilding instanceof SurvivalCraftingBuilding building)
                || !(level.getBlockEntity(plaque) instanceof SurvivalCraftingTileEntity tile)) {
            return RegistrationResult.PLAQUE_NOT_REGISTERED;
        }
        final UUID id = tile.committedMark() == null ? selection.markerId() : tile.committedMark().id();
        final List<WorksitePoi> retained = tile.committedMark() == null ? List.of()
                : tile.committedMark().pois().stream()
                        .filter(point -> inspection.bounds().contains(point.position())).toList();
        synchronize(level, colony, building, tile,
                tile.committedMark() == null ? List.of() : tile.committedMark().pois(),
                new CommittedWorksiteMark(id, inspection.definition().markerType(), inspection.bounds(),
                        mergeWorkstations(retained, inspection.workstationPositions())));
        return RegistrationResult.SAVED;
    }

    public static ResidenceMarkerService.PointEditOutcome editPoint(
            final ServerLevel level, final ServerPlayer player, final BlockPos position,
            final WorksitePoiType type, final boolean remove) {
        final IColony colony = IColonyManager.getInstance().getColonyByPosFromWorld(level, position);
        if (colony == null) return outcome(ResidenceMarkerService.PointEditResult.NO_COMMITTED_VOLUME);
        final List<Owner> owners = colony.getServerBuildingManager().getBuildings().values().stream()
                .filter(SurvivalCraftingBuilding.class::isInstance)
                .map(SurvivalCraftingBuilding.class::cast)
                .map(building -> owner(level, building))
                .filter(java.util.Objects::nonNull)
                .filter(owner -> owner.tile().committedMark().bounds().contains(position)).toList();
        if (owners.isEmpty()) return outcome(ResidenceMarkerService.PointEditResult.NO_COMMITTED_VOLUME);
        if (owners.size() > 1) return outcome(ResidenceMarkerService.PointEditResult.AMBIGUOUS_VOLUME);
        if (!colony.getPermissions().hasPermission(player, Action.MANAGE_HUTS)) {
            return outcome(ResidenceMarkerService.PointEditResult.NO_PERMISSION);
        }
        if (type == WorksitePoiType.WORKSITE) return outcome(ResidenceMarkerService.PointEditResult.SCANNER_OWNED);
        if (type != WorksitePoiType.STORAGE && type != WorksitePoiType.ENTRANCE
                && type != WorksitePoiType.INTERACTION) {
            return outcome(ResidenceMarkerService.PointEditResult.INVALID_INTERACTION_TARGET);
        }
        final Owner owner = owners.getFirst();
        final CommittedWorksiteMark current = owner.tile().committedMark();
        final WorksitePoi point = new WorksitePoi(type, position);
        final List<WorksitePoi> points = new ArrayList<>(current.pois());
        if (remove) {
            if (!points.remove(point)) return outcome(ResidenceMarkerService.PointEditResult.POINT_NOT_FOUND);
        } else if (points.contains(point)) {
            return outcome(ResidenceMarkerService.PointEditResult.ALREADY_PRESENT);
        } else {
            final PoiTargetValidator.Result validation = PoiTargetValidator.validate(level, position, type);
            if (validation != PoiTargetValidator.Result.VALID) return outcome(validationFailure(validation));
            points.add(point);
        }
        synchronize(level, colony, owner.building(), owner.tile(), current.pois(),
                new CommittedWorksiteMark(current.id(), current.type(), current.bounds(), points));
        return outcome(remove ? ResidenceMarkerService.PointEditResult.REMOVED
                : ResidenceMarkerService.PointEditResult.ADDED);
    }

    public static ReconcileResult reconcilePois(
            final ServerLevel level, final SurvivalCraftingBuilding building,
            final SurvivalCraftingTileEntity tile) {
        final CommittedWorksiteMark current = tile.committedMark();
        if (current == null) return ReconcileResult.MARK_UNAVAILABLE;
        final CraftingWorkplaceInspection inspection = CraftingWorkplaceInspector.inspect(level, current.bounds());
        if (inspection.status() == CraftingWorkplaceInspection.Status.AREA_NOT_LOADED) {
            return ReconcileResult.AREA_NOT_LOADED;
        }
        if ((inspection.status() != CraftingWorkplaceInspection.Status.VALID
                        && inspection.status() != CraftingWorkplaceInspection.Status.NO_WORKSTATION)
                || inspection.plaquePositions().size() != 1
                || !inspection.plaquePositions().getFirst().equals(tile.getBlockPos())
                || inspection.definition().markerType() != current.type()) {
            return ReconcileResult.MARK_UNAVAILABLE;
        }
        final List<WorksitePoi> updated = mergeWorkstations(current.pois(), inspection.workstationPositions());
        final int desiredLevel = isReady(updated) ? 1 : 0;
        if (Set.copyOf(updated).equals(Set.copyOf(current.pois())) && building.getBuildingLevel() == desiredLevel) {
            return ReconcileResult.UNCHANGED;
        }
        final IColony colony = IColonyManager.getInstance().getColonyByPosFromWorld(level, tile.getBlockPos());
        if (colony == null || colony.getID() != building.getColony().getID()) return ReconcileResult.BUILDING_UNAVAILABLE;
        synchronize(level, colony, building, tile, current.pois(),
                new CommittedWorksiteMark(current.id(), current.type(), current.bounds(), updated));
        return ReconcileResult.UPDATED;
    }

    static List<WorksitePoi> mergeWorkstations(final List<WorksitePoi> existing, final List<BlockPos> workstations) {
        final List<WorksitePoi> merged = new ArrayList<>();
        existing.stream().filter(point -> point.type() != WorksitePoiType.WORKSITE).forEach(merged::add);
        workstations.stream().map(pos -> new WorksitePoi(WorksitePoiType.WORKSITE, pos)).forEach(merged::add);
        return List.copyOf(merged);
    }

    public static ResidenceMarkerService.RemovalResult removeCommittedMark(
            final ServerLevel level, final ServerPlayer player, final SurvivalCraftingTileEntity tile) {
        if (tile.committedMark() == null) return ResidenceMarkerService.RemovalResult.NO_MARK;
        final IColony colony = IColonyManager.getInstance().getColonyByPosFromWorld(level, tile.getBlockPos());
        if (colony == null) return ResidenceMarkerService.RemovalResult.OUTSIDE_COLONY;
        if (!colony.getPermissions().hasPermission(player, Action.MANAGE_HUTS)) {
            return ResidenceMarkerService.RemovalResult.NO_PERMISSION;
        }
        if (tile.getBuilding() instanceof SurvivalCraftingBuilding building) {
            synchronize(level, colony, building, tile, tile.committedMark().pois(), null);
        } else tile.clearCommittedMark();
        return ResidenceMarkerService.RemovalResult.REMOVED;
    }

    private static void synchronize(
            final ServerLevel level, final IColony colony, final SurvivalCraftingBuilding building,
            final SurvivalCraftingTileEntity tile, final List<WorksitePoi> previous,
            final CommittedWorksiteMark updated) {
        previous.stream().filter(point -> point.type() == WorksitePoiType.WORKSITE)
                .map(WorksitePoi::position).forEach(building::removeWorkstation);
        previous.stream().filter(point -> point.type() == WorksitePoiType.STORAGE)
                .map(WorksitePoi::position).forEach(building::removeContainerPosition);
        if (updated == null) {
            building.clearWorkArea();
            tile.clearCommittedMark();
            building.setBuildingLevel(0);
        } else {
            tile.setCommittedMark(updated);
            building.setCorners(updated.bounds().min(), updated.bounds().max());
            building.configureWorkArea(updated.bounds());
            building.setBuildingLevel(isReady(updated.pois()) ? 1 : 0);
            updated.pois().stream().filter(point -> point.type() == WorksitePoiType.STORAGE)
                    .map(WorksitePoi::position).forEach(building::addContainerPosition);
            updated.pois().stream().filter(point -> point.type() == WorksitePoiType.WORKSITE)
                    .map(WorksitePoi::position).forEach(pos -> building.registerWorkstation(level, pos));
        }
        building.markDirty();
        colony.markDirty();
    }

    private static boolean isReady(final List<WorksitePoi> points) {
        return points.stream().anyMatch(point -> point.type() == WorksitePoiType.STORAGE)
                && points.stream().anyMatch(point -> point.type() == WorksitePoiType.WORKSITE);
    }

    private static Owner owner(final ServerLevel level, final SurvivalCraftingBuilding building) {
        return level.getBlockEntity(building.getPosition()) instanceof SurvivalCraftingTileEntity tile
                        && tile.committedMark() != null ? new Owner(building, tile) : null;
    }

    private static ResidenceMarkerService.PointEditOutcome outcome(
            final ResidenceMarkerService.PointEditResult result) {
        return new ResidenceMarkerService.PointEditOutcome(result, null);
    }

    private static ResidenceMarkerService.PointEditResult validationFailure(final PoiTargetValidator.Result result) {
        return switch (result) {
            case INVALID_STORAGE -> ResidenceMarkerService.PointEditResult.INVALID_STORAGE_TARGET;
            case INVALID_WORKSITE -> ResidenceMarkerService.PointEditResult.INVALID_WORKSITE_TARGET;
            case INVALID_ENTRANCE -> ResidenceMarkerService.PointEditResult.INVALID_ENTRANCE_TARGET;
            case INVALID_INTERACTION -> ResidenceMarkerService.PointEditResult.INVALID_INTERACTION_TARGET;
            case SCANNER_OWNED -> ResidenceMarkerService.PointEditResult.SCANNER_OWNED;
            case VALID -> throw new IllegalStateException("Handled above");
        };
    }

    private static boolean cornersBelongTo(final ServerLevel level, final AreaBounds bounds, final IColony expected) {
        return List.of(bounds.min(), bounds.max(),
                new BlockPos(bounds.min().getX(), bounds.min().getY(), bounds.max().getZ()),
                new BlockPos(bounds.min().getX(), bounds.max().getY(), bounds.min().getZ()),
                new BlockPos(bounds.max().getX(), bounds.min().getY(), bounds.min().getZ()),
                new BlockPos(bounds.min().getX(), bounds.max().getY(), bounds.max().getZ()),
                new BlockPos(bounds.max().getX(), bounds.min().getY(), bounds.max().getZ()),
                new BlockPos(bounds.max().getX(), bounds.max().getY(), bounds.min().getZ())).stream()
                .allMatch(pos -> {
                    final IColony colony = IColonyManager.getInstance().getColonyByPosFromWorld(level, pos);
                    return colony != null && colony.getID() == expected.getID();
                });
    }

    private record Owner(SurvivalCraftingBuilding building, SurvivalCraftingTileEntity tile) { }
    public enum RegistrationResult { SAVED, OUTSIDE_COLONY, CROSSES_COLONY_BORDER, NO_PERMISSION, PLAQUE_NOT_REGISTERED }
    public enum ReconcileResult { UPDATED, UNCHANGED, AREA_NOT_LOADED, MARK_UNAVAILABLE, BUILDING_UNAVAILABLE }
}
