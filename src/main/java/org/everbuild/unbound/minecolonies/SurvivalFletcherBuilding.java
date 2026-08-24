package org.everbuild.unbound.minecolonies;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingFletcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

/** Native Fletcher building backed by a survival-defined volume. */
public final class SurvivalFletcherBuilding extends BuildingFletcher implements SurvivalCraftingBuilding {
    public SurvivalFletcherBuilding(final IColony colony, final BlockPos position) { super(colony, position); }
    @Override public boolean canBeBuiltByBuilder(final int newLevel) { return false; }
    @Override public boolean canDeconstruct() { return false; }
    @Override public boolean isBuilt() { return getBuildingLevel() > 0; }
    @Override public void requestUpgrade(final Player player, final BlockPos builder) { }
    @Override public void requestRepair(final BlockPos builder) { }
}
