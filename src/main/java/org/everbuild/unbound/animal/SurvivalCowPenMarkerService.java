package org.everbuild.unbound.animal;

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
import org.everbuild.unbound.marker.MarkerType;
import org.everbuild.unbound.marker.PoiTargetValidator;
import org.everbuild.unbound.marker.WorksitePoi;
import org.everbuild.unbound.marker.WorksitePoiType;
import org.everbuild.unbound.minecolonies.SurvivalCowPenBuilding;
import org.everbuild.unbound.minecolonies.SurvivalCowPenTileEntity;
import org.everbuild.unbound.residence.ResidenceMarkerService;

/** Activates a native MineColonies Cowboy inside an inspected survival pen. */
public final class SurvivalCowPenMarkerService {
    private SurvivalCowPenMarkerService() {
    }

    public static RegistrationResult register(
            final ServerLevel level,
            final ServerPlayer player,
            final MarkerSelection selection,
            final CowPenInspection inspection) {
        final AreaBounds bounds = inspection.bounds();
        final IColony colony = IColonyManager.getInstance().getColonyByPosFromWorld(level, bounds.min());
        if (colony == null) {
            return RegistrationResult.OUTSIDE_COLONY;
        }
        if (!colony.getPermissions().hasPermission(player, Action.MANAGE_HUTS)) {
            return RegistrationResult.NO_PERMISSION;
        }
        if (!cornersBelongTo(level, bounds, colony)) {
            return RegistrationResult.CROSSES_COLONY_BORDER;
        }
        final BlockPos plaque = inspection.plaquePositions().getFirst();
        final IBuilding nativeBuilding = IColonyManager.getInstance().getBuilding(level, plaque);
        if (!(nativeBuilding instanceof SurvivalCowPenBuilding building)
                || !(level.getBlockEntity(plaque) instanceof SurvivalCowPenTileEntity tile)) {
            return RegistrationResult.PLAQUE_NOT_REGISTERED;
        }

        final UUID markerId = tile.committedMark() == null
                ? selection.markerId()
                : tile.committedMark().id();
        final List<WorksitePoi> retained = tile.committedMark() == null
                ? List.of()
                : tile.committedMark().pois().stream()
                        .filter(poi -> bounds.contains(poi.position()))
                        .toList();
        synchronize(
                colony,
                building,
                tile,
                tile.committedMark() == null ? List.of() : tile.committedMark().pois(),
                new CommittedWorksiteMark(
                        markerId,
                        MarkerType.ANIMAL_PEN,
                        bounds,
                        mergeScannedPois(retained, inspection)));
        return RegistrationResult.SAVED;
    }

    public static ResidenceMarkerService.PointEditOutcome editPoint(
            final ServerLevel level,
            final ServerPlayer player,
            final BlockPos position,
            final WorksitePoiType poiType,
            final boolean remove) {
        final IColony colony = IColonyManager.getInstance().getColonyByPosFromWorld(level, position);
        if (colony == null) {
            return outcome(ResidenceMarkerService.PointEditResult.NO_COMMITTED_VOLUME);
        }
        final List<Owner> owners = colony.getServerBuildingManager().getBuildings().values().stream()
                .filter(SurvivalCowPenBuilding.class::isInstance)
                .map(SurvivalCowPenBuilding.class::cast)
                .map(building -> owner(building, level))
                .filter(java.util.Objects::nonNull)
                .filter(owner -> owner.tile().committedMark().bounds().contains(position))
                .toList();
        if (owners.isEmpty()) {
            return outcome(ResidenceMarkerService.PointEditResult.NO_COMMITTED_VOLUME);
        }
        if (owners.size() > 1) {
            return outcome(ResidenceMarkerService.PointEditResult.AMBIGUOUS_VOLUME);
        }
        if (!colony.getPermissions().hasPermission(player, Action.MANAGE_HUTS)) {
            return outcome(ResidenceMarkerService.PointEditResult.NO_PERMISSION);
        }
        if (poiType != WorksitePoiType.STORAGE) {
            return outcome(ResidenceMarkerService.PointEditResult.SCANNER_OWNED);
        }

        final Owner owner = owners.getFirst();
        final CommittedWorksiteMark current = owner.tile().committedMark();
        final WorksitePoi edited = new WorksitePoi(WorksitePoiType.STORAGE, position);
        final List<WorksitePoi> points = new ArrayList<>(current.pois());
        if (remove) {
            if (!points.remove(edited)) {
                return outcome(ResidenceMarkerService.PointEditResult.POINT_NOT_FOUND);
            }
        } else if (points.contains(edited)) {
            return outcome(ResidenceMarkerService.PointEditResult.ALREADY_PRESENT);
        } else if (PoiTargetValidator.validate(level, position, WorksitePoiType.STORAGE)
                != PoiTargetValidator.Result.VALID) {
            return outcome(ResidenceMarkerService.PointEditResult.INVALID_STORAGE_TARGET);
        } else {
            points.add(edited);
        }
        synchronize(
                colony,
                owner.building(),
                owner.tile(),
                current.pois(),
                new CommittedWorksiteMark(current.id(), current.type(), current.bounds(), points));
        return outcome(remove
                ? ResidenceMarkerService.PointEditResult.REMOVED
                : ResidenceMarkerService.PointEditResult.ADDED);
    }

    public static ReconcileResult reconcilePois(
            final ServerLevel level,
            final SurvivalCowPenBuilding building,
            final SurvivalCowPenTileEntity tile) {
        final CommittedWorksiteMark current = tile.committedMark();
        if (current == null) {
            return ReconcileResult.MARK_UNAVAILABLE;
        }
        final CowPenInspection inspection = CowPenInspector.inspect(level, current.bounds());
        if (inspection.status() == CowPenInspection.Status.AREA_NOT_LOADED) {
            return ReconcileResult.AREA_NOT_LOADED;
        }
        if (inspection.plaquePositions().size() != 1
                || !inspection.plaquePositions().getFirst().equals(tile.getBlockPos())) {
            return ReconcileResult.MARK_UNAVAILABLE;
        }
        final List<WorksitePoi> updated = mergeScannedPois(current.pois(), inspection);
        final int desiredLevel = isReady(updated) ? 1 : 0;
        if (Set.copyOf(updated).equals(Set.copyOf(current.pois()))
                && building.getBuildingLevel() == desiredLevel) {
            return ReconcileResult.UNCHANGED;
        }
        final IColony colony = IColonyManager.getInstance().getColonyByPosFromWorld(level, tile.getBlockPos());
        if (colony == null || colony.getID() != building.getColony().getID()) {
            return ReconcileResult.BUILDING_UNAVAILABLE;
        }
        synchronize(
                colony,
                building,
                tile,
                current.pois(),
                new CommittedWorksiteMark(current.id(), current.type(), current.bounds(), updated));
        return ReconcileResult.UPDATED;
    }

    static List<WorksitePoi> mergeScannedPois(
            final List<WorksitePoi> existing,
            final CowPenInspection inspection) {
        final List<WorksitePoi> merged = new ArrayList<>();
        existing.stream()
                .filter(poi -> poi.type() != WorksitePoiType.ENTRANCE
                        && poi.type() != WorksitePoiType.PASTURE)
                .forEach(merged::add);
        inspection.gatePositions().stream()
                .map(position -> new WorksitePoi(WorksitePoiType.ENTRANCE, position))
                .forEach(merged::add);
        inspection.pasturePositions().stream()
                .findFirst()
                .map(position -> new WorksitePoi(WorksitePoiType.PASTURE, position))
                .ifPresent(merged::add);
        return List.copyOf(merged);
    }

    public static ResidenceMarkerService.RemovalResult removeCommittedMark(
            final ServerLevel level,
            final ServerPlayer player,
            final SurvivalCowPenTileEntity tile) {
        if (tile.committedMark() == null) {
            return ResidenceMarkerService.RemovalResult.NO_MARK;
        }
        final IColony colony = IColonyManager.getInstance().getColonyByPosFromWorld(level, tile.getBlockPos());
        if (colony == null) {
            return ResidenceMarkerService.RemovalResult.OUTSIDE_COLONY;
        }
        if (!colony.getPermissions().hasPermission(player, Action.MANAGE_HUTS)) {
            return ResidenceMarkerService.RemovalResult.NO_PERMISSION;
        }
        if (tile.getBuilding() instanceof SurvivalCowPenBuilding building) {
            synchronize(colony, building, tile, tile.committedMark().pois(), null);
        } else {
            tile.clearCommittedMark();
        }
        return ResidenceMarkerService.RemovalResult.REMOVED;
    }

    private static void synchronize(
            final IColony colony,
            final SurvivalCowPenBuilding building,
            final SurvivalCowPenTileEntity tile,
            final List<WorksitePoi> previous,
            final CommittedWorksiteMark updated) {
        previous.stream()
                .filter(poi -> poi.type() == WorksitePoiType.STORAGE)
                .map(WorksitePoi::position)
                .forEach(building::removeContainerPosition);
        if (updated == null) {
            tile.clearCommittedMark();
            building.setBuildingLevel(0);
        } else {
            tile.setCommittedMark(updated);
            building.setCorners(updated.bounds().min(), updated.bounds().max());
            updated.pois().stream()
                    .filter(poi -> poi.type() == WorksitePoiType.STORAGE)
                    .map(WorksitePoi::position)
                    .forEach(building::addContainerPosition);
            building.setBuildingLevel(isReady(updated.pois()) ? 1 : 0);
        }
        building.markDirty();
        colony.markDirty();
    }

    private static boolean isReady(final List<WorksitePoi> points) {
        return contains(points, WorksitePoiType.STORAGE)
                && contains(points, WorksitePoiType.ENTRANCE)
                && contains(points, WorksitePoiType.PASTURE);
    }

    private static boolean contains(final List<WorksitePoi> points, final WorksitePoiType type) {
        return points.stream().anyMatch(point -> point.type() == type);
    }

    private static Owner owner(final SurvivalCowPenBuilding building, final ServerLevel level) {
        return level.getBlockEntity(building.getPosition()) instanceof SurvivalCowPenTileEntity tile
                        && tile.committedMark() != null
                ? new Owner(building, tile)
                : null;
    }

    private static ResidenceMarkerService.PointEditOutcome outcome(
            final ResidenceMarkerService.PointEditResult result) {
        return new ResidenceMarkerService.PointEditOutcome(result, null);
    }

    private static boolean cornersBelongTo(
            final ServerLevel level,
            final AreaBounds bounds,
            final IColony expected) {
        return List.of(
                        bounds.min(), bounds.max(),
                        new BlockPos(bounds.min().getX(), bounds.min().getY(), bounds.max().getZ()),
                        new BlockPos(bounds.min().getX(), bounds.max().getY(), bounds.min().getZ()),
                        new BlockPos(bounds.max().getX(), bounds.min().getY(), bounds.min().getZ()),
                        new BlockPos(bounds.min().getX(), bounds.max().getY(), bounds.max().getZ()),
                        new BlockPos(bounds.max().getX(), bounds.min().getY(), bounds.max().getZ()),
                        new BlockPos(bounds.max().getX(), bounds.max().getY(), bounds.min().getZ()))
                .stream()
                .allMatch(position -> {
                    final IColony colony = IColonyManager.getInstance().getColonyByPosFromWorld(level, position);
                    return colony != null && colony.getID() == expected.getID();
                });
    }

    private record Owner(SurvivalCowPenBuilding building, SurvivalCowPenTileEntity tile) {
    }

    public enum RegistrationResult {
        SAVED,
        OUTSIDE_COLONY,
        CROSSES_COLONY_BORDER,
        NO_PERMISSION,
        PLAQUE_NOT_REGISTERED
    }

    public enum ReconcileResult {
        UPDATED,
        UNCHANGED,
        AREA_NOT_LOADED,
        MARK_UNAVAILABLE,
        BUILDING_UNAVAILABLE
    }
}
