package org.everbuild.unbound.minecolonies;

import com.minecolonies.api.blocks.AbstractColonyBlock;
import com.minecolonies.api.colony.buildings.registry.BuildingEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public final class SurvivalRabbitHutchBlock extends AbstractColonyBlock<SurvivalRabbitHutchBlock> {
    public SurvivalRabbitHutchBlock(final BlockBehaviour.Properties properties) { super(properties); }
    @Override public String getHutName() { return "survival_rabbit_hutch"; }
    @Override public BuildingEntry getBuildingEntry() { return MineColoniesIntegration.SURVIVAL_RABBIT_HUTCH.get(); }
    @Override public BlockEntity newBlockEntity(final BlockPos position, final BlockState state) {
        return new SurvivalRabbitHutchTileEntity(position, state);
    }
}
