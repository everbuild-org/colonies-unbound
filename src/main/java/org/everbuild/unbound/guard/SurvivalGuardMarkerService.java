package org.everbuild.unbound.guard;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.permissions.Action;
import com.minecolonies.api.util.BlockPosUtil;
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
import org.everbuild.unbound.marker.PatrolRouteSelection;
import org.everbuild.unbound.marker.WorksitePoi;
import org.everbuild.unbound.marker.WorksitePoiType;
import org.everbuild.unbound.minecolonies.SurvivalGuardBuilding;
import org.everbuild.unbound.minecolonies.SurvivalGuardTileEntity;
import org.everbuild.unbound.residence.ResidenceMarkerService;

/** Registers survival Guard Towers and synchronizes ordered patrol nodes to native guard AI. */
public final class SurvivalGuardMarkerService {
    public static final int MAXIMUM_PATROL_NODES = 32;

    private SurvivalGuardMarkerService() {
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
        final IBuilding nativeBuilding = IColonyManager.getInstance().getBuilding(level, plaque);
        if (!(nativeBuilding instanceof SurvivalGuardBuilding building)
                || !(level.getBlockEntity(plaque) instanceof SurvivalGuardTileEntity tile)) {
            return RegistrationResult.PLAQUE_NOT_REGISTERED;
        }

        final UUID markerId = tile.committedMark() == null
                ? selection.markerId()
                : tile.committedMark().id();
        final List<WorksitePoi> retained = tile.committedMark() == null
                ? List.of()
                : tile.committedMark().pois();
        final int configuredLevel = tile.committedMark() == null
                ? 1
                : tile.committedMark().configuredLevel();
        synchronize(
                colony,
                building,
                tile,
                new CommittedWorksiteMark(markerId, MarkerType.GUARD, bounds, retained, configuredLevel));
        colony.getServerBuildingManager().guardBuildingChangedAt(building, 1);
        return RegistrationResult.SAVED;
    }

    public static RouteEditResult selectRouteOwner(
            final ServerLevel level,
            final ServerPlayer player,
            final SurvivalGuardTileEntity tile) {
        if (tile.committedMark() == null) {
            return RouteEditResult.MARK_UNAVAILABLE;
        }
        final IColony colony = IColonyManager.getInstance().getColonyByPosFromWorld(level, tile.getBlockPos());
        if (colony == null) {
            return RouteEditResult.OUTSIDE_COLONY;
        }
        return colony.getPermissions().hasPermission(player, Action.MANAGE_HUTS)
                ? RouteEditResult.OWNER_SELECTED
                : RouteEditResult.NO_PERMISSION;
    }

    public static RouteEditOutcome editPatrolNode(
            final ServerLevel level,
            final ServerPlayer player,
            final PatrolRouteSelection selection,
            final BlockPos node,
            final boolean remove) {
        if (!selection.dimension().equals(level.dimension().location())) {
            return new RouteEditOutcome(RouteEditResult.WRONG_DIMENSION, 0);
        }
        if (!(level.getBlockEntity(selection.plaquePosition()) instanceof SurvivalGuardTileEntity tile)
                || tile.committedMark() == null
                || !(tile.getBuilding() instanceof SurvivalGuardBuilding building)) {
            return new RouteEditOutcome(RouteEditResult.MARK_UNAVAILABLE, 0);
        }
        final IColony colony = IColonyManager.getInstance().getColonyByPosFromWorld(level, tile.getBlockPos());
        final IColony nodeColony = IColonyManager.getInstance().getColonyByPosFromWorld(level, node);
        if (colony == null || nodeColony == null || colony.getID() != nodeColony.getID()) {
            return new RouteEditOutcome(RouteEditResult.OUTSIDE_COLONY, patrolCount(tile.committedMark().pois()));
        }
        if (!colony.getPermissions().hasPermission(player, Action.MANAGE_HUTS)) {
            return new RouteEditOutcome(RouteEditResult.NO_PERMISSION, patrolCount(tile.committedMark().pois()));
        }
        if (BlockPosUtil.getDistance2D(node, building.getID()) > building.getPatrolDistance()) {
            return new RouteEditOutcome(RouteEditResult.TOO_FAR, patrolCount(tile.committedMark().pois()));
        }

        final CommittedWorksiteMark current = tile.committedMark();
        final WorksitePoi edited = new WorksitePoi(WorksitePoiType.PATROL, node);
        final List<WorksitePoi> points = new ArrayList<>(current.pois());
        if (remove) {
            if (!points.remove(edited)) {
                return new RouteEditOutcome(RouteEditResult.NODE_NOT_FOUND, patrolCount(points));
            }
        } else if (points.contains(edited)) {
            return new RouteEditOutcome(RouteEditResult.NODE_EXISTS, patrolCount(points));
        } else if (patrolCount(points) >= MAXIMUM_PATROL_NODES) {
            return new RouteEditOutcome(RouteEditResult.TOO_MANY_NODES, patrolCount(points));
        } else {
            points.add(edited);
        }

        synchronize(colony, building, tile, current.withPois(points));
        return new RouteEditOutcome(
                remove ? RouteEditResult.NODE_REMOVED : RouteEditResult.NODE_ADDED,
                patrolCount(points));
    }

    public static ResidenceMarkerService.RemovalResult removeCommittedMark(
            final ServerLevel level,
            final ServerPlayer player,
            final SurvivalGuardTileEntity tile) {
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
        if (tile.getBuilding() instanceof SurvivalGuardBuilding building) {
            building.resetPatrolTargets();
            building.setBuildingLevel(0);
            building.markDirty();
            colony.getServerBuildingManager().guardBuildingChangedAt(building, 0);
            colony.markDirty();
        }
        tile.clearCommittedMark();
        return ResidenceMarkerService.RemovalResult.REMOVED;
    }

    private static void synchronize(
            final IColony colony,
            final SurvivalGuardBuilding building,
            final SurvivalGuardTileEntity tile,
            final CommittedWorksiteMark mark) {
        tile.setCommittedMark(mark);
        building.setCorners(mark.bounds().min(), mark.bounds().max());
        building.resetPatrolTargets();
        mark.pois().stream()
                .filter(point -> point.type() == WorksitePoiType.PATROL)
                .map(WorksitePoi::position)
                .forEach(building::addPatrolTarget);
        building.setBuildingLevel(mark.effectiveLevel());
        building.markDirty();
        colony.markDirty();
    }

    private static int patrolCount(final List<WorksitePoi> points) {
        return (int) points.stream().filter(point -> point.type() == WorksitePoiType.PATROL).count();
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

    public enum RegistrationResult {
        SAVED,
        OUTSIDE_COLONY,
        CROSSES_COLONY_BORDER,
        NO_PERMISSION,
        PLAQUE_NOT_REGISTERED
    }

    public enum RouteEditResult {
        OWNER_SELECTED,
        NODE_ADDED,
        NODE_REMOVED,
        NODE_EXISTS,
        NODE_NOT_FOUND,
        TOO_MANY_NODES,
        TOO_FAR,
        WRONG_DIMENSION,
        OUTSIDE_COLONY,
        NO_PERMISSION,
        MARK_UNAVAILABLE
    }

    public record RouteEditOutcome(RouteEditResult result, int nodeCount) {
    }
}
