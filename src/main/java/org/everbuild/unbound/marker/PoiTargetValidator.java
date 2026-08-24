package org.everbuild.unbound.marker;

import com.minecolonies.api.blocks.AbstractColonyBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;

/** Checks whether a clicked block can accurately fulfill the selected semantic role. */
public final class PoiTargetValidator {
    private PoiTargetValidator() {
    }

    public static Result validate(
            final ServerLevel level,
            final BlockPos position,
            final WorksitePoiType poiType) {
        final BlockState state = level.getBlockState(position);
        return switch (poiType) {
            case BED, PASTURE, HIVE -> Result.SCANNER_OWNED;
            case STORAGE -> isStorage(level, position, state)
                    ? Result.VALID
                    : Result.INVALID_STORAGE;
            case WORKSITE -> state.getBlock() instanceof AbstractFurnaceBlock
                    ? Result.VALID
                    : Result.INVALID_WORKSITE;
            case ENTRANCE -> isEntrance(state)
                    ? Result.VALID
                    : Result.INVALID_ENTRANCE;
            case INTERACTION -> state.isAir()
                    ? Result.INVALID_INTERACTION
                    : Result.VALID;
            case PATROL -> state.isAir()
                    ? Result.INVALID_INTERACTION
                    : Result.VALID;
            case STALL -> state.isAir()
                    ? Result.INVALID_INTERACTION
                    : Result.VALID;
        };
    }

    private static boolean isStorage(
            final ServerLevel level,
            final BlockPos position,
            final BlockState state) {
        return !(state.getBlock() instanceof AbstractFurnaceBlock)
                && !(state.getBlock() instanceof AbstractColonyBlock<?>)
                && level.getCapability(
                        Capabilities.ItemHandler.BLOCK,
                        position,
                        (Direction) null) != null;
    }

    private static boolean isEntrance(final BlockState state) {
        return state.getBlock() instanceof DoorBlock
                || state.getBlock() instanceof FenceGateBlock
                || state.getBlock() instanceof TrapDoorBlock;
    }

    public enum Result {
        VALID,
        INVALID_STORAGE,
        INVALID_WORKSITE,
        INVALID_ENTRANCE,
        INVALID_INTERACTION,
        SCANNER_OWNED
    }
}
