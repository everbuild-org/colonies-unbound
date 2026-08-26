package org.everbuild.unbound.minecolonies;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.core.colony.buildings.modules.BuildingModules;
import com.minecolonies.core.colony.buildings.modules.EnchanterStationsModule;
import com.minecolonies.core.colony.buildings.modules.WorkerBuildingModule;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingEnchanter;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** Native enchanter with automatic links to the colony's operational worker buildings. */
public final class SurvivalEnchanterBuilding extends BuildingEnchanter implements SurvivalCraftingBuilding {
    public SurvivalEnchanterBuilding(final IColony colony, final BlockPos position) { super(colony, position); }
    @Override public boolean canBeBuiltByBuilder(final int newLevel) { return false; }
    @Override public boolean canDeconstruct() { return false; }
    @Override public boolean isBuilt() { return getBuildingLevel() > 0; }
    @Override public void requestUpgrade(final Player player, final BlockPos builder) { }
    @Override public void requestRepair(final BlockPos builder) { }

    @Override
    public void registerWorkstation(final Level level, final BlockPos position) {
        synchronizeDrainStations();
    }

    @Override
    public void onColonyTick(final IColony colony) {
        super.onColonyTick(colony);
        if (getBuildingLevel() > 0) synchronizeDrainStations();
    }

    private void synchronizeDrainStations() {
        final EnchanterStationsModule stations = getModule(BuildingModules.ENCHANTER_STATIONS);
        final Set<BlockPos> desired = getColony().getServerBuildingManager().getBuildings().values().stream()
                .filter(building -> !building.getPosition().equals(getPosition()))
                .filter(building -> building.getBuildingLevel() > 0)
                .filter(building -> building.hasModule(WorkerBuildingModule.class))
                .map(building -> building.getPosition().immutable())
                .collect(java.util.stream.Collectors.toSet());
        final Set<BlockPos> current = stations.getBuildingsToGatherFrom();
        current.stream().filter(position -> !desired.contains(position)).forEach(stations::removeWorker);
        desired.stream().filter(position -> !current.contains(position)).forEach(stations::addWorker);
    }

    @Override
    public void removeWorkstation(final BlockPos position) {
        final EnchanterStationsModule stations = getModule(BuildingModules.ENCHANTER_STATIONS);
        stations.getBuildingsToGatherFrom().forEach(stations::removeWorker);
    }
}
