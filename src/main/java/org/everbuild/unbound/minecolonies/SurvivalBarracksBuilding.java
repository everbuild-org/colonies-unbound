package org.everbuild.unbound.minecolonies;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingBarracks;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingBarracksTower;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

/** Native barracks that maintains colony-wide parent links to survival barracks towers. */
public final class SurvivalBarracksBuilding extends BuildingBarracks implements SurvivalCraftingBuilding {
    public SurvivalBarracksBuilding(final IColony colony, final BlockPos position) { super(colony, position); }
    @Override public boolean canBeBuiltByBuilder(final int level) { return false; }
    @Override public boolean canDeconstruct() { return false; }
    @Override public boolean isBuilt() { return getBuildingLevel() > 0; }
    @Override public void requestUpgrade(final Player player, final BlockPos builder) { }
    @Override public void requestRepair(final BlockPos builder) { }
    @Override public void onColonyTick(final IColony colony) {
        super.onColonyTick(colony); if (getBuildingLevel() <= 0) return;
        final Set<BlockPos> desired = colony.getServerBuildingManager().getBuildings().values().stream()
                .filter(SurvivalBarracksTowerBuilding.class::isInstance)
                .filter(building -> building.getBuildingLevel() > 0)
                .filter(tower -> colony.getServerBuildingManager().getBuildings().values().stream()
                        .filter(SurvivalBarracksBuilding.class::isInstance)
                        .filter(building -> building.getBuildingLevel() > 0)
                        .min(java.util.Comparator.comparingDouble(building -> building.getPosition().distSqr(tower.getPosition())))
                        .map(building -> building.getPosition().equals(getPosition())).orElse(false))
                .map(building -> building.getPosition().immutable()).collect(Collectors.toSet());
        getTowers().removeIf(position -> !desired.contains(position));
        for (final BlockPos position : desired) {
            if (!getTowers().contains(position)) getTowers().add(position);
            if (colony.getServerBuildingManager().getBuilding(position) instanceof BuildingBarracksTower tower) {
                tower.addBarracks(getPosition());
            }
        }
    }
}
