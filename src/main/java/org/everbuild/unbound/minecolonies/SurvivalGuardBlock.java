package org.everbuild.unbound.minecolonies;

import com.minecolonies.api.blocks.AbstractColonyBlock;
import com.minecolonies.api.colony.buildings.registry.BuildingEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/** MineColonies-compatible anchor for a survival-built Guard Tower. */
public final class SurvivalGuardBlock extends AbstractColonyBlock<SurvivalGuardBlock> {
    public SurvivalGuardBlock(final BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public String getHutName() {
        return "survival_guard";
    }

    @Override
    public BuildingEntry getBuildingEntry() {
        return MineColoniesIntegration.SURVIVAL_GUARD.get();
    }

    @Override
    public BlockEntity newBlockEntity(final BlockPos position, final BlockState state) {
        return new SurvivalGuardTileEntity(position, state);
    }
}
