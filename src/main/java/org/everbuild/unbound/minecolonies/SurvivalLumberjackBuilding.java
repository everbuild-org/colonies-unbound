package org.everbuild.unbound.minecolonies;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingLumberjack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import org.everbuild.unbound.marker.AreaBounds;

/** Native Forester building restricted to its survival-defined woodland. */
public final class SurvivalLumberjackBuilding extends BuildingLumberjack implements SurvivalCraftingBuilding {
    public SurvivalLumberjackBuilding(final IColony colony, final BlockPos position) { super(colony, position); }
    @Override public boolean canBeBuiltByBuilder(final int newLevel) { return false; }
    @Override public boolean canDeconstruct() { return false; }
    @Override public boolean isBuilt() { return getBuildingLevel() > 0; }
    @Override public void requestUpgrade(final Player player, final BlockPos builder) { }
    @Override public void requestRepair(final BlockPos builder) { }
    @Override public void configureWorkArea(final AreaBounds bounds) { setRestrictedArea(bounds.min(), bounds.max()); }
    @Override public void clearWorkArea() { setRestrictedArea(null, null); }
}
