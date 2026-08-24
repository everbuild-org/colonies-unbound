package org.everbuild.unbound.minecolonies;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingGuardTower;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

/** Native Guard Tower whose footprint and patrol route come from survival markers. */
public final class SurvivalGuardBuilding extends BuildingGuardTower {
    public SurvivalGuardBuilding(final IColony colony, final BlockPos position) {
        super(colony, position);
    }

    @Override
    public boolean canBeBuiltByBuilder(final int newLevel) {
        return false;
    }

    @Override
    public boolean canDeconstruct() {
        return false;
    }

    @Override
    public boolean isBuilt() {
        return getBuildingLevel() > 0;
    }

    @Override
    public void requestUpgrade(final Player player, final BlockPos builder) {
        // Survival towers are reinspected rather than upgraded from a schematic.
    }

    @Override
    public void requestRepair(final BlockPos builder) {
        // Survival towers are reinspected rather than repaired from a schematic.
    }
}
