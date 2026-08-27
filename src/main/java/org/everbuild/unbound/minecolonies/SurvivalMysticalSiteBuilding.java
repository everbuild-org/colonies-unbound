package org.everbuild.unbound.minecolonies;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.core.colony.buildings.BuildingMysticalSite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

/** Native mystical site anchored to a committed survival volume. */
public final class SurvivalMysticalSiteBuilding extends BuildingMysticalSite implements SurvivalCraftingBuilding {
    public SurvivalMysticalSiteBuilding(final IColony colony, final BlockPos position) { super(colony, position); }
    @Override public boolean canBeBuiltByBuilder(final int level) { return false; }
    @Override public boolean canDeconstruct() { return false; }
    @Override public boolean isBuilt() { return getBuildingLevel() > 0; }
    @Override public void requestUpgrade(final Player player, final BlockPos builder) { }
    @Override public void requestRepair(final BlockPos builder) { }
}
