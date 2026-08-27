package org.everbuild.unbound.minecolonies;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.core.colony.buildings.workerbuildings.Stash;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

/** Native request-system Stash using a survival plaque anchor. */
public final class SurvivalStashBuilding extends Stash implements SurvivalCraftingBuilding {
    public SurvivalStashBuilding(final IColony colony, final BlockPos position) { super(colony, position); }
    @Override public boolean canBeBuiltByBuilder(final int level) { return false; }
    @Override public boolean canDeconstruct() { return false; }
    @Override public boolean isBuilt() { return true; }
    @Override public void requestUpgrade(final Player player, final BlockPos builder) { }
    @Override public void requestRepair(final BlockPos builder) { }
}
