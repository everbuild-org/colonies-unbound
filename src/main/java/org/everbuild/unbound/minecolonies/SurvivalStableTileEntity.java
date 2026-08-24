package org.everbuild.unbound.minecolonies;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public final class SurvivalStableTileEntity extends MarkedBuildingTileEntity {
    public SurvivalStableTileEntity(final BlockPos position, final BlockState state) {
        super(MineColoniesIntegration.SURVIVAL_STABLE_TILE.get(), position, state);
    }
}
