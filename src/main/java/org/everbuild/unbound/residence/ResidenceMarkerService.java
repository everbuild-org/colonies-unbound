package org.everbuild.unbound.residence;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.permissions.Action;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.everbuild.unbound.marker.AreaBounds;
import org.everbuild.unbound.marker.MarkerSelection;

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

        ResidenceMarkerData.get(level).put(new SurvivalResidenceMarker(
                selection.markerId(),
                colony.getID(),
                player.getUUID(),
                bounds,
                inspection.bedHeads(),
                level.getGameTime()));
        return Result.SAVED;
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
        NO_PERMISSION
    }
}
