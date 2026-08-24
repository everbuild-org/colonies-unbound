package org.everbuild.unbound.minecolonies;

import com.minecolonies.api.blocks.AbstractColonyBlock;
import com.minecolonies.api.colony.buildings.registry.BuildingEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public final class SurvivalChickenPenBlock extends AbstractColonyBlock<SurvivalChickenPenBlock> {
    public SurvivalChickenPenBlock(final BlockBehaviour.Properties properties) { super(properties); }
    @Override public String getHutName() { return "survival_chicken_pen"; }
    @Override public BuildingEntry getBuildingEntry() { return MineColoniesIntegration.SURVIVAL_CHICKEN_PEN.get(); }
    @Override public BlockEntity newBlockEntity(final BlockPos position, final BlockState state) {
        return new SurvivalChickenPenTileEntity(position, state);
    }
}
