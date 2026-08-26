package org.everbuild.unbound.minecolonies;

import com.minecolonies.api.blocks.ModBlocks;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingGraveyard;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** Native graveyard backed by scanner-owned MineColonies graves. */
public final class SurvivalGraveyardBuilding extends BuildingGraveyard implements SurvivalCraftingBuilding {
    public SurvivalGraveyardBuilding(final IColony colony, final BlockPos position) { super(colony, position); }
    @Override public boolean canBeBuiltByBuilder(final int newLevel) { return false; }
    @Override public boolean canDeconstruct() { return false; }
    @Override public boolean isBuilt() { return getBuildingLevel() > 0; }
    @Override public void requestUpgrade(final Player player, final BlockPos builder) { }
    @Override public void requestRepair(final BlockPos builder) { }
    @Override public void registerWorkstation(final Level level, final BlockPos position) {
        final var state = level.getBlockState(position);
        if (state.is(ModBlocks.blockNamedGrave)) registerBlockPosition(state, position, level);
        getColony().getGraveManager().addNewGrave(position);
    }
    @Override public void removeWorkstation(final BlockPos position) {
        getGravePositions().removeIf(tuple -> tuple.getA().equals(position));
        getColony().getGraveManager().removeGrave(position);
    }
}
