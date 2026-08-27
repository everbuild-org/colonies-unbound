package org.everbuild.unbound.minecolonies;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.core.colony.buildings.workerbuildings.PostBox;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

/** Native request-system Post Box using the survival plaque inventory. */
public final class SurvivalPostBoxBuilding extends PostBox implements SurvivalCraftingBuilding {
    public SurvivalPostBoxBuilding(final IColony colony, final BlockPos position) { super(colony, position); }
    @Override public boolean canBeBuiltByBuilder(final int level) { return false; }
    @Override public boolean canDeconstruct() { return false; }
    @Override public boolean isBuilt() { return true; }
    @Override public void requestUpgrade(final Player player, final BlockPos builder) { }
    @Override public void requestRepair(final BlockPos builder) { }
}
