package org.everbuild.unbound.minecolonies;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public final class SurvivalPigPenTileEntity extends MarkedBuildingTileEntity {
    public SurvivalPigPenTileEntity(final BlockPos position, final BlockState state) {
        super(MineColoniesIntegration.SURVIVAL_PIG_PEN_TILE.get(), position, state);
    }
}
