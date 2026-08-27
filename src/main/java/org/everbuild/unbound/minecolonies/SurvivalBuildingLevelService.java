package org.everbuild.unbound.minecolonies;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.api.colony.permissions.Action;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.everbuild.unbound.marker.CommittedWorksiteMark;
import org.everbuild.unbound.marker.SurvivalBuildingLevels;

/** Server-authoritative manual level editing for committed survival plaques. */
public final class SurvivalBuildingLevelService {
    private SurvivalBuildingLevelService() {
    }

    public static LevelEditOutcome adjust(
            final ServerLevel level,
            final ServerPlayer player,
            final BlockPos plaquePosition,
            final boolean decrease) {
        final Owner owner = owner(level, player, plaquePosition);
        if (owner.failure() != null) {
            return LevelEditOutcome.failure(owner.failure());
        }

        final CommittedWorksiteMark current = owner.tile().committedMark();
        final int maximum = SurvivalBuildingLevels.maximumConfiguredLevel(current.type());
        if (maximum == SurvivalBuildingLevels.DEFAULT_LEVEL) {
            return outcome(LevelEditResult.FIXED_LEVEL, current, maximum);
        }

        final int requested = current.configuredLevel() + (decrease ? -1 : 1);
        if (requested < SurvivalBuildingLevels.DEFAULT_LEVEL) {
            return outcome(LevelEditResult.AT_MINIMUM, current, maximum);
        }
        if (requested > maximum) {
            return outcome(LevelEditResult.AT_MAXIMUM, current, maximum);
        }
        return apply(owner, current.withConfiguredLevel(requested), LevelEditResult.UPDATED, maximum);
    }

    public static LevelEditOutcome set(
            final ServerLevel level,
            final ServerPlayer player,
            final BlockPos plaquePosition,
            final int requestedLevel) {
        final Owner owner = owner(level, player, plaquePosition);
        if (owner.failure() != null) {
            return LevelEditOutcome.failure(owner.failure());
        }
        final CommittedWorksiteMark current = owner.tile().committedMark();
        final int maximum = SurvivalBuildingLevels.maximumConfiguredLevel(current.type());
        final int configured = SurvivalBuildingLevels.clampConfiguredLevel(current.type(), requestedLevel);
        if (configured == current.configuredLevel()) {
            return outcome(maximum == SurvivalBuildingLevels.DEFAULT_LEVEL
                    ? LevelEditResult.FIXED_LEVEL : LevelEditResult.UNCHANGED, current, maximum);
        }
        return apply(owner, current.withConfiguredLevel(configured), LevelEditResult.UPDATED, maximum);
    }

    private static LevelEditOutcome apply(
            final Owner owner,
            final CommittedWorksiteMark updated,
            final LevelEditResult result,
            final int maximum) {
        owner.tile().setCommittedMark(updated);
        owner.building().setBuildingLevel(updated.effectiveLevel());
        owner.building().markDirty();
        owner.colony().markDirty();
        return outcome(result, updated, maximum);
    }

    private static Owner owner(
            final ServerLevel level,
            final ServerPlayer player,
            final BlockPos plaquePosition) {
        if (!(level.getBlockEntity(plaquePosition) instanceof MarkedBuildingTileEntity tile)
                || tile.committedMark() == null) {
            return Owner.failure(LevelEditResult.NO_COMMITTED_MARK);
        }
        final IColony colony = IColonyManager.getInstance().getColonyByPosFromWorld(level, plaquePosition);
        if (colony == null) {
            return Owner.failure(LevelEditResult.OUTSIDE_COLONY);
        }
        if (!colony.getPermissions().hasPermission(player, Action.MANAGE_HUTS)) {
            return Owner.failure(LevelEditResult.NO_PERMISSION);
        }
        final IBuilding building = tile.getBuilding();
        if (building == null || building.getColony().getID() != colony.getID()) {
            return Owner.failure(LevelEditResult.BUILDING_UNAVAILABLE);
        }
        return new Owner(colony, building, tile, null);
    }

    private static LevelEditOutcome outcome(
            final LevelEditResult result,
            final CommittedWorksiteMark mark,
            final int maximum) {
        return new LevelEditOutcome(result, mark.configuredLevel(), mark.effectiveLevel(), maximum);
    }

    private record Owner(
            IColony colony,
            IBuilding building,
            MarkedBuildingTileEntity tile,
            LevelEditResult failure) {
        private static Owner failure(final LevelEditResult failure) {
            return new Owner(null, null, null, failure);
        }
    }

    public record LevelEditOutcome(
            LevelEditResult result,
            int configuredLevel,
            int effectiveLevel,
            int maximumLevel) {
        private static LevelEditOutcome failure(final LevelEditResult result) {
            return new LevelEditOutcome(result, 0, 0, 0);
        }
    }

    public enum LevelEditResult {
        UPDATED,
        UNCHANGED,
        AT_MINIMUM,
        AT_MAXIMUM,
        FIXED_LEVEL,
        NO_COMMITTED_MARK,
        OUTSIDE_COLONY,
        NO_PERMISSION,
        BUILDING_UNAVAILABLE
    }
}
