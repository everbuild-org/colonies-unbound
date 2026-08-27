package org.everbuild.unbound.minecolonies;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.core.colony.buildings.modules.BuildingModules;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingBarracksTower;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** Native barracks tower with scanner-owned guard beds. */
public final class SurvivalBarracksTowerBuilding extends BuildingBarracksTower implements SurvivalCraftingBuilding {
    public SurvivalBarracksTowerBuilding(final IColony colony, final BlockPos position) { super(colony, position); }
    @Override public boolean canBeBuiltByBuilder(final int level) { return false; }
    @Override public boolean canDeconstruct() { return false; }
    @Override public boolean isBuilt() { return getBuildingLevel() > 0; }
    @Override public void requestUpgrade(final Player player, final BlockPos builder) { }
    @Override public void requestRepair(final BlockPos builder) { }
    @Override public void registerWorkstation(final Level level, final BlockPos position) {
        registerBlockPosition(level.getBlockState(position), position, level);
    }
    @Override public void removeWorkstation(final BlockPos position) { getModule(BuildingModules.BED).removeBed(position); }
    @Override public void onColonyTick(final IColony colony) {
        super.onColonyTick(colony);
        final BlockPos parent = colony.getServerBuildingManager().getBuildings().values().stream()
                .filter(SurvivalBarracksBuilding.class::isInstance)
                .filter(building -> building.getBuildingLevel() > 0)
                .min(java.util.Comparator.comparingDouble(building -> building.getPosition().distSqr(getPosition())))
                .map(building -> building.getPosition().immutable()).orElse(null);
        addBarracks(parent);
    }
}
