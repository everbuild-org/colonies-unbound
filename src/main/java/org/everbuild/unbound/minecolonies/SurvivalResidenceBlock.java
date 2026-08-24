package org.everbuild.unbound.minecolonies;

import com.minecolonies.api.blocks.AbstractColonyBlock;
import com.minecolonies.api.colony.buildings.registry.BuildingEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.everbuild.unbound.residence.ResidenceMarkerData;

/** Physical MineColonies-compatible anchor embedded in a survival-built residence. */
public final class SurvivalResidenceBlock extends AbstractColonyBlock<SurvivalResidenceBlock> {
    public SurvivalResidenceBlock(final BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public String getHutName() {
        return "survival_residence";
    }

    @Override
    public BuildingEntry getBuildingEntry() {
        return MineColoniesIntegration.SURVIVAL_RESIDENCE.get();
    }

    @Override
    public BlockEntity newBlockEntity(final BlockPos position, final BlockState state) {
        return new SurvivalResidenceTileEntity(position, state);
    }

    @Override
    public void onRemove(
            final BlockState oldState,
            final Level level,
            final BlockPos position,
            final BlockState newState,
            final boolean movedByPiston) {
        if (!oldState.is(newState.getBlock()) && level instanceof ServerLevel serverLevel) {
            final BlockEntity blockEntity = level.getBlockEntity(position);
            if (blockEntity instanceof SurvivalResidenceTileEntity residenceTile
                    && residenceTile.committedMark() != null) {
                ResidenceMarkerData.get(serverLevel).remove(residenceTile.committedMark().id());
            }
        }
        super.onRemove(oldState, level, position, newState, movedByPiston);
    }
}
