package org.everbuild.unbound.residence;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.permissions.Action;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.core.colony.buildings.modules.BedHandlingModule;
import com.minecolonies.core.colony.buildings.modules.BuildingModules;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.everbuild.unbound.marker.AreaBounds;
import org.everbuild.unbound.marker.CommittedWorksiteMark;
import org.everbuild.unbound.marker.MarkerSelection;
import org.everbuild.unbound.marker.MarkerType;
import org.everbuild.unbound.minecolonies.SurvivalResidenceTileEntity;
import org.everbuild.unbound.minecolonies.SurvivalResidenceBuilding;

/** Validates MineColonies ownership before committing a Residence marker to world data. */
public final class ResidenceMarkerService {
    private ResidenceMarkerService() {
    }

    public static Result register(
            final ServerLevel level,
            final ServerPlayer player,
            final MarkerSelection selection,
            final ResidenceInspection inspection) {
        final AreaBounds bounds = inspection.bounds();
        final IColony colony = IColonyManager.getInstance().getColonyByPosFromWorld(level, bounds.min());
        if (colony == null) {
            return Result.OUTSIDE_COLONY;
        }
        if (!colony.getPermissions().hasPermission(player, Action.MANAGE_HUTS)) {
            return Result.NO_PERMISSION;
        }
        if (!cornersBelongTo(level, bounds, colony)) {
            return Result.CROSSES_COLONY_BORDER;
        }

        final BlockPos plaque = inspection.plaquePositions().getFirst();
        final IBuilding building = IColonyManager.getInstance().getBuilding(level, plaque);
        if (!(building instanceof SurvivalResidenceBuilding)
                || !(level.getBlockEntity(plaque) instanceof SurvivalResidenceTileEntity residenceTile)) {
            return Result.PLAQUE_NOT_REGISTERED;
        }

        final UUID markerId = residenceTile.committedMark() == null
                ? selection.markerId()
                : residenceTile.committedMark().id();

        building.setCorners(bounds.min(), bounds.max());
        building.setBuildingLevel(1);
        final BedHandlingModule bedModule = building.getModule(BuildingModules.BED);
        for (final BlockPos oldBed : List.copyOf(bedModule.getRegisteredBlocks())) {
            bedModule.removeBed(oldBed);
        }
        for (final BlockPos bed : inspection.bedHeads()) {
            bedModule.onBlockPlacedInBuilding(level.getBlockState(bed), bed, level);
        }
        building.markDirty();
        colony.markDirty();

        residenceTile.setCommittedMark(new CommittedWorksiteMark(markerId, MarkerType.RESIDENCE, bounds));

        ResidenceMarkerData.get(level).put(new SurvivalResidenceMarker(
                markerId,
                colony.getID(),
                player.getUUID(),
                bounds,
                inspection.bedHeads(),
                level.getGameTime()));
        return Result.SAVED;
    }

    public static RemovalResult removeCommittedMark(
            final ServerLevel level,
            final ServerPlayer player,
            final SurvivalResidenceTileEntity residenceTile) {
        if (residenceTile.committedMark() == null) {
            return RemovalResult.NO_MARK;
        }
        final IColony colony = IColonyManager.getInstance()
                .getColonyByPosFromWorld(level, residenceTile.getBlockPos());
        if (colony == null) {
            return RemovalResult.OUTSIDE_COLONY;
        }
        if (!colony.getPermissions().hasPermission(player, Action.MANAGE_HUTS)) {
            return RemovalResult.NO_PERMISSION;
        }

        ResidenceMarkerData.get(level).remove(residenceTile.committedMark().id());
        residenceTile.clearCommittedMark();
        return RemovalResult.REMOVED;
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
        return corners.stream().allMatch(pos -> {
            final IColony colony = IColonyManager.getInstance().getColonyByPosFromWorld(level, pos);
            return colony != null && colony.getID() == expectedColony.getID();
        });
    }

    public enum Result {
        SAVED,
        OUTSIDE_COLONY,
        CROSSES_COLONY_BORDER,
        NO_PERMISSION,
        PLAQUE_NOT_REGISTERED
    }

    public enum RemovalResult {
        REMOVED,
        NO_MARK,
        OUTSIDE_COLONY,
        NO_PERMISSION
    }
}
