package org.everbuild.unbound.minecolonies;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public final class SurvivalRabbitHutchTileEntity extends MarkedBuildingTileEntity {
    public SurvivalRabbitHutchTileEntity(final BlockPos position, final BlockState state) {
        super(MineColoniesIntegration.SURVIVAL_RABBIT_HUTCH_TILE.get(), position, state);
    }
}
