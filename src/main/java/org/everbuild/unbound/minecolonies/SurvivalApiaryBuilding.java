package org.everbuild.unbound.minecolonies;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingBeekeeper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

/** Native Beekeeper workplace whose hives come from a survival volume scan. */
public final class SurvivalApiaryBuilding extends BuildingBeekeeper {
    public SurvivalApiaryBuilding(final IColony colony, final BlockPos position) {
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
        // Survival apiaries are reinspected rather than upgraded from a schematic.
    }

    @Override
    public void requestRepair(final BlockPos builder) {
        // Survival apiaries are reinspected rather than repaired from a schematic.
    }
}
