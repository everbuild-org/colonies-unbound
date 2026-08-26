package org.everbuild.unbound.minecolonies;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingGlassblower;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

/** Native Glassblower building backed by a survival-defined volume. */
public final class SurvivalGlassblowerBuilding extends BuildingGlassblower implements SurvivalFurnaceCraftingBuilding {
    public SurvivalGlassblowerBuilding(final IColony colony, final BlockPos position) { super(colony, position); }
    @Override public boolean canBeBuiltByBuilder(final int newLevel) { return false; }
    @Override public boolean canDeconstruct() { return false; }
    @Override public boolean isBuilt() { return getBuildingLevel() > 0; }
    @Override public void requestUpgrade(final Player player, final BlockPos builder) { }
    @Override public void requestRepair(final BlockPos builder) { }
}
