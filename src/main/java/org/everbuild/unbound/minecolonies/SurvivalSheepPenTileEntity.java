package org.everbuild.unbound.minecolonies;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/** Building anchor that owns one committed survival Sheep Pen volume. */
public final class SurvivalSheepPenTileEntity extends MarkedBuildingTileEntity {
    public SurvivalSheepPenTileEntity(final BlockPos position, final BlockState state) {
        super(MineColoniesIntegration.SURVIVAL_SHEEP_PEN_TILE.get(), position, state);
    }
}
