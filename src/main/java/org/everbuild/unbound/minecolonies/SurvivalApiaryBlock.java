package org.everbuild.unbound.minecolonies;

import com.minecolonies.api.blocks.AbstractColonyBlock;
import com.minecolonies.api.colony.buildings.registry.BuildingEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/** MineColonies-compatible anchor for a survival-built apiary. */
public final class SurvivalApiaryBlock extends AbstractColonyBlock<SurvivalApiaryBlock> {
    public SurvivalApiaryBlock(final BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public String getHutName() {
        return "survival_apiary";
    }

    @Override
    public BuildingEntry getBuildingEntry() {
        return MineColoniesIntegration.SURVIVAL_APIARY.get();
    }

    @Override
    public BlockEntity newBlockEntity(final BlockPos position, final BlockState state) {
        return new SurvivalApiaryTileEntity(position, state);
    }
}
