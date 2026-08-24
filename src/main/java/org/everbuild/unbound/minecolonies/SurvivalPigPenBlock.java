package org.everbuild.unbound.minecolonies;

import com.minecolonies.api.blocks.AbstractColonyBlock;
import com.minecolonies.api.colony.buildings.registry.BuildingEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public final class SurvivalPigPenBlock extends AbstractColonyBlock<SurvivalPigPenBlock> {
    public SurvivalPigPenBlock(final BlockBehaviour.Properties properties) { super(properties); }
    @Override public String getHutName() { return "survival_pig_pen"; }
    @Override public BuildingEntry getBuildingEntry() { return MineColoniesIntegration.SURVIVAL_PIG_PEN.get(); }
    @Override public BlockEntity newBlockEntity(final BlockPos position, final BlockState state) {
        return new SurvivalPigPenTileEntity(position, state);
    }
}
