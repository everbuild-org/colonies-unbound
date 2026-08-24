package org.everbuild.unbound.minecolonies;

import com.minecolonies.api.blocks.AbstractColonyBlock;
import com.minecolonies.api.colony.buildings.registry.BuildingEntry;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

/** Shared plaque block for survival crafting workplaces. */
public final class SurvivalCraftingBlock extends AbstractColonyBlock<SurvivalCraftingBlock> {
    private final String hutName;
    private final Supplier<BuildingEntry> buildingEntry;

    public SurvivalCraftingBlock(
            final BlockBehaviour.Properties properties,
            final String hutName,
            final Supplier<BuildingEntry> buildingEntry) {
        super(properties);
        this.hutName = hutName;
        this.buildingEntry = buildingEntry;
    }

    @Override public String getHutName() { return hutName; }
    @Override public BuildingEntry getBuildingEntry() { return buildingEntry.get(); }
    @Override public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
        return new SurvivalCraftingTileEntity(pos, state);
    }
}
