package org.everbuild.unbound.minecolonies;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingDeliveryman;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

/** Native courier hut participating in the colony warehouse assignment network. */
public final class SurvivalDeliverymanBuilding extends BuildingDeliveryman implements SurvivalCraftingBuilding {
    public SurvivalDeliverymanBuilding(final IColony colony, final BlockPos position) { super(colony, position); }
    @Override public boolean canBeBuiltByBuilder(final int level) { return false; }
    @Override public boolean canDeconstruct() { return false; }
    @Override public boolean isBuilt() { return getBuildingLevel() > 0; }
    @Override public void requestUpgrade(final Player player, final BlockPos builder) { }
    @Override public void requestRepair(final BlockPos builder) { }
}
