package org.everbuild.unbound.minecolonies;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/** Shared committed-volume anchor for crafting workplaces. */
public final class SurvivalCraftingTileEntity extends MarkedBuildingTileEntity {
    public SurvivalCraftingTileEntity(final BlockPos position, final BlockState state) {
        super(MineColoniesIntegration.SURVIVAL_CRAFTING_TILE.get(), position, state);
    }
}
