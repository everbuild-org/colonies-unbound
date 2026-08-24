package org.everbuild.unbound.minecolonies;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/** Building anchor for a survival-defined MineColonies Guard Tower. */
public final class SurvivalGuardTileEntity extends MarkedBuildingTileEntity {
    public SurvivalGuardTileEntity(final BlockPos position, final BlockState state) {
        super(MineColoniesIntegration.SURVIVAL_GUARD_TILE.get(), position, state);
    }
}
