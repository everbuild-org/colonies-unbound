package org.everbuild.unbound.minecolonies;

import com.minecolonies.api.blocks.AbstractColonyBlock;
import com.minecolonies.api.colony.buildings.registry.BuildingEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/** MineColonies-compatible anchor for a survival-built Dining Hall. */
public final class SurvivalCookBlock extends AbstractColonyBlock<SurvivalCookBlock> {
    public SurvivalCookBlock(final BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public String getHutName() {
        return "survival_cook";
    }

    @Override
    public BuildingEntry getBuildingEntry() {
        return MineColoniesIntegration.SURVIVAL_COOK.get();
    }

    @Override
    public BlockEntity newBlockEntity(final BlockPos position, final BlockState state) {
        return new SurvivalCookTileEntity(position, state);
    }
}
