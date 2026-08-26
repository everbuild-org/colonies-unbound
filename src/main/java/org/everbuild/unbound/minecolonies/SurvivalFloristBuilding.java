package org.everbuild.unbound.minecolonies;

import com.minecolonies.api.blocks.ModBlocks;
import com.minecolonies.api.colony.IColony;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingFlorist;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** Native Florist building backed by scanned composted planting soil. */
public final class SurvivalFloristBuilding extends BuildingFlorist implements SurvivalCraftingBuilding {
    public SurvivalFloristBuilding(final IColony colony, final BlockPos position) { super(colony, position); }
    @Override public boolean canBeBuiltByBuilder(final int newLevel) { return false; }
    @Override public boolean canDeconstruct() { return false; }
    @Override public boolean isBuilt() { return getBuildingLevel() > 0; }
    @Override public void requestUpgrade(final Player player, final BlockPos builder) { }
    @Override public void requestRepair(final BlockPos builder) { }
    @Override public void registerWorkstation(final Level level, final BlockPos position) {
        registerBlockPosition(ModBlocks.blockCompostedDirt, position, level);
    }
    @Override public void removeWorkstation(final BlockPos position) { removePlantableGround(position); }
}
