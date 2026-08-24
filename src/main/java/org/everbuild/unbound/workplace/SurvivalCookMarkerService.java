package org.everbuild.unbound.workplace;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.permissions.Action;
import com.minecolonies.core.colony.buildings.modules.BuildingModules;
import com.minecolonies.core.colony.buildings.modules.FurnaceUserModule;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.everbuild.unbound.marker.AreaBounds;
import org.everbuild.unbound.marker.CommittedWorksiteMark;
import org.everbuild.unbound.marker.MarkerSelection;
import org.everbuild.unbound.marker.MarkerType;
import org.everbuild.unbound.marker.PoiTargetValidator;
import org.everbuild.unbound.marker.WorkplacePointSummary;
import org.everbuild.unbound.marker.WorksitePoi;
import org.everbuild.unbound.marker.WorksitePoiType;
import org.everbuild.unbound.minecolonies.SurvivalCookBuilding;
import org.everbuild.unbound.minecolonies.SurvivalCookTileEntity;
import org.everbuild.unbound.residence.ResidenceMarkerService;

/** Activates native MineColonies Cook behavior from a marked survival workplace. */
public final class SurvivalCookMarkerService {
    private SurvivalCookMarkerService() {
    }

    public static RegistrationResult register(
            final ServerLevel level,
            final ServerPlayer player,
            final MarkerSelection selection,
            final AreaBounds bounds,
            final BlockPos plaque) {
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

        final IBuilding building = IColonyManager.getInstance().getBuilding(level, plaque);
        if (!(building instanceof SurvivalCookBuilding cookBuilding)
                || !(level.getBlockEntity(plaque) instanceof SurvivalCookTileEntity cookTile)) {
            return RegistrationResult.PLAQUE_NOT_REGISTERED;
        }

        final UUID markerId = cookTile.committedMark() == null
                ? selection.markerId()
                : cookTile.committedMark().id();
        final List<WorksitePoi> retainedPois = cookTile.committedMark() == null
                ? List.of()
                : cookTile.committedMark().pois().stream()
                        .filter(poi -> bounds.contains(poi.position()))
                        .toList();
        synchronize(
                level,
                colony,
                cookBuilding,
                cookTile,
                cookTile.committedMark() == null ? List.of() : cookTile.committedMark().pois(),
                new CommittedWorksiteMark(markerId, MarkerType.RESTAURANT, bounds, retainedPois));
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

        final List<CookOwner> owners = colony.getServerBuildingManager().getBuildings().values().stream()
                .filter(SurvivalCookBuilding.class::isInstance)
                .map(SurvivalCookBuilding.class::cast)
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

        final CookOwner owner = owners.getFirst();
        final CommittedWorksiteMark current = owner.tile().committedMark();
        final WorksitePoi editedPoi = new WorksitePoi(poiType, position);
        final List<WorksitePoi> pois = new ArrayList<>(current.pois());
        if (remove) {
            if (!pois.remove(editedPoi)) {
                return new ResidenceMarkerService.PointEditOutcome(
                        ResidenceMarkerService.PointEditResult.POINT_NOT_FOUND,
                        WorkplacePointSummary.from(pois));
            }
        } else if (pois.contains(editedPoi)) {
            return new ResidenceMarkerService.PointEditOutcome(
                    ResidenceMarkerService.PointEditResult.ALREADY_PRESENT,
                    WorkplacePointSummary.from(pois));
        } else {
            final PoiTargetValidator.Result validation = PoiTargetValidator.validate(level, position, poiType);
            if (validation != PoiTargetValidator.Result.VALID) {
                return new ResidenceMarkerService.PointEditOutcome(
                        validationFailure(validation), WorkplacePointSummary.from(pois));
            }
            pois.add(editedPoi);
        }

        synchronize(
                level,
                colony,
                owner.building(),
                owner.tile(),
                current.pois(),
                new CommittedWorksiteMark(current.id(), current.type(), current.bounds(), pois));
        return new ResidenceMarkerService.PointEditOutcome(
                remove
                        ? ResidenceMarkerService.PointEditResult.REMOVED
                        : ResidenceMarkerService.PointEditResult.ADDED,
                WorkplacePointSummary.from(pois));
    }

    public static ResidenceMarkerService.RemovalResult removeCommittedMark(
            final ServerLevel level,
            final ServerPlayer player,
            final SurvivalCookTileEntity tile) {
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
        if (tile.getBuilding() instanceof SurvivalCookBuilding building) {
            synchronize(level, colony, building, tile, tile.committedMark().pois(), null);
        } else {
            tile.clearCommittedMark();
        }
        return ResidenceMarkerService.RemovalResult.REMOVED;
    }

    private static void synchronize(
            final ServerLevel level,
            final IColony colony,
            final SurvivalCookBuilding building,
            final SurvivalCookTileEntity tile,
            final List<WorksitePoi> previousPois,
            final CommittedWorksiteMark updatedMark) {
        final FurnaceUserModule furnaces = building.getModule(BuildingModules.FURNACE);
        previousPois.stream()
                .filter(poi -> poi.type() == WorksitePoiType.WORKSITE)
                .map(WorksitePoi::position)
                .forEach(furnaces::removeFromFurnaces);
        previousPois.stream()
                .filter(poi -> poi.type() == WorksitePoiType.STORAGE)
                .map(WorksitePoi::position)
                .forEach(building::removeContainerPosition);

        if (updatedMark == null) {
            tile.clearCommittedMark();
            building.setBuildingLevel(0);
        } else {
            tile.setCommittedMark(updatedMark);
            building.setCorners(updatedMark.bounds().min(), updatedMark.bounds().max());
            updatedMark.pois().stream()
                    .filter(poi -> poi.type() == WorksitePoiType.WORKSITE)
                    .map(WorksitePoi::position)
                    .forEach(position -> furnaces.onBlockPlacedInBuilding(
                            level.getBlockState(position), position, level));
            updatedMark.pois().stream()
                    .filter(poi -> poi.type() == WorksitePoiType.STORAGE)
                    .map(WorksitePoi::position)
                    .forEach(building::addContainerPosition);
            building.setBuildingLevel(WorkplacePointSummary.from(updatedMark.pois()).isReady() ? 1 : 0);
        }
        building.markDirty();
        colony.markDirty();
    }

    private static CookOwner owner(final SurvivalCookBuilding building, final ServerLevel level) {
        final BlockPos position = building.getPosition();
        if (level.getBlockEntity(position) instanceof SurvivalCookTileEntity tile
                && tile.committedMark() != null) {
            return new CookOwner(building, tile);
        }
        return null;
    }

    private static ResidenceMarkerService.PointEditResult validationFailure(
            final PoiTargetValidator.Result validation) {
        return switch (validation) {
            case INVALID_STORAGE -> ResidenceMarkerService.PointEditResult.INVALID_STORAGE_TARGET;
            case INVALID_WORKSITE -> ResidenceMarkerService.PointEditResult.INVALID_WORKSITE_TARGET;
            case INVALID_ENTRANCE -> ResidenceMarkerService.PointEditResult.INVALID_ENTRANCE_TARGET;
            case INVALID_INTERACTION -> ResidenceMarkerService.PointEditResult.INVALID_INTERACTION_TARGET;
            case SCANNER_OWNED -> ResidenceMarkerService.PointEditResult.SCANNER_OWNED;
            case VALID -> throw new IllegalStateException("Handled above");
        };
    }

    private static ResidenceMarkerService.PointEditOutcome outcome(
            final ResidenceMarkerService.PointEditResult result) {
        return new ResidenceMarkerService.PointEditOutcome(result, null);
    }

    private static boolean cornersBelongTo(
            final ServerLevel level,
            final AreaBounds bounds,
            final IColony expectedColony) {
        final List<BlockPos> corners = List.of(
                bounds.min(), bounds.max(),
                new BlockPos(bounds.min().getX(), bounds.min().getY(), bounds.max().getZ()),
                new BlockPos(bounds.min().getX(), bounds.max().getY(), bounds.min().getZ()),
                new BlockPos(bounds.max().getX(), bounds.min().getY(), bounds.min().getZ()),
                new BlockPos(bounds.min().getX(), bounds.max().getY(), bounds.max().getZ()),
                new BlockPos(bounds.max().getX(), bounds.min().getY(), bounds.max().getZ()),
                new BlockPos(bounds.max().getX(), bounds.max().getY(), bounds.min().getZ()));
        return corners.stream().allMatch(position -> {
            final IColony colony = IColonyManager.getInstance().getColonyByPosFromWorld(level, position);
            return colony != null && colony.getID() == expectedColony.getID();
        });
    }

    private record CookOwner(SurvivalCookBuilding building, SurvivalCookTileEntity tile) {
    }

    public enum RegistrationResult {
        SAVED,
        OUTSIDE_COLONY,
        CROSSES_COLONY_BORDER,
        NO_PERMISSION,
        PLAQUE_NOT_REGISTERED
    }
}
