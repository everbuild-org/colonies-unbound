package org.everbuild.unbound.minecolonies;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/** Building anchor that owns one committed survival apiary volume. */
public final class SurvivalApiaryTileEntity extends MarkedBuildingTileEntity {
    public SurvivalApiaryTileEntity(final BlockPos position, final BlockState state) {
        super(MineColoniesIntegration.SURVIVAL_APIARY_TILE.get(), position, state);
    }
}
