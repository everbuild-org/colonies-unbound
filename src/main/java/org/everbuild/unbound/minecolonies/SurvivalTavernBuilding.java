package org.everbuild.unbound.minecolonies;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.core.colony.buildings.DefaultBuildingInstance;
import com.minecolonies.core.colony.buildings.modules.BuildingModules;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** Native tavern whose guest beds are owned by the committed volume. */
public final class SurvivalTavernBuilding extends DefaultBuildingInstance implements SurvivalCraftingBuilding {
    public SurvivalTavernBuilding(final IColony colony, final BlockPos position) { super(colony, position, "tavern", 3); }
    @Override public boolean canBeBuiltByBuilder(final int newLevel) { return false; }
    @Override public boolean canDeconstruct() { return false; }
    @Override public boolean isBuilt() { return getBuildingLevel() > 0; }
    @Override public void requestUpgrade(final Player player, final BlockPos builder) { }
    @Override public void requestRepair(final BlockPos builder) { }
    @Override public void registerWorkstation(final Level level, final BlockPos position) {
        registerBlockPosition(level.getBlockState(position), position, level);
    }
    @Override public void removeWorkstation(final BlockPos position) { getModule(BuildingModules.BED).removeBed(position); }
}
