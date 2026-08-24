package org.everbuild.unbound.minecolonies;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/** MineColonies building anchor that owns the committed residence volume. */
public final class SurvivalResidenceTileEntity extends MarkedBuildingTileEntity {
    public SurvivalResidenceTileEntity(final BlockPos position, final BlockState state) {
        super(MineColoniesIntegration.SURVIVAL_RESIDENCE_TILE.get(), position, state);
    }
}
