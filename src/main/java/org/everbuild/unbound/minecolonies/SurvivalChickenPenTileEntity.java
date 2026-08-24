package org.everbuild.unbound.minecolonies;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public final class SurvivalChickenPenTileEntity extends MarkedBuildingTileEntity {
    public SurvivalChickenPenTileEntity(final BlockPos position, final BlockState state) {
        super(MineColoniesIntegration.SURVIVAL_CHICKEN_PEN_TILE.get(), position, state);
    }
}
