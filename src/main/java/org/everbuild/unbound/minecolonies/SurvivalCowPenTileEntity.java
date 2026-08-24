package org.everbuild.unbound.minecolonies;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/** Building anchor that owns one committed survival Cow Pen volume. */
public final class SurvivalCowPenTileEntity extends MarkedBuildingTileEntity {
    public SurvivalCowPenTileEntity(final BlockPos position, final BlockState state) {
        super(MineColoniesIntegration.SURVIVAL_COW_PEN_TILE.get(), position, state);
    }
}
