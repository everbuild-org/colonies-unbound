package org.everbuild.unbound.minecolonies;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/** Building anchor for a survival-defined MineColonies Dining Hall. */
public final class SurvivalCookTileEntity extends MarkedBuildingTileEntity {
    public SurvivalCookTileEntity(final BlockPos position, final BlockState state) {
        super(MineColoniesIntegration.SURVIVAL_COOK_TILE.get(), position, state);
    }
}
