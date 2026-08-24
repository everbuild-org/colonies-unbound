package org.everbuild.unbound.minecolonies;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingShepherd;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

/** Native Shepherd workplace whose working area is a marked survival pen. */
public final class SurvivalSheepPenBuilding extends BuildingShepherd implements SurvivalAnimalPenBuilding {
    public SurvivalSheepPenBuilding(final IColony colony, final BlockPos position) {
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
        // Survival pens are reinspected rather than upgraded from a schematic.
    }

    @Override
    public void requestRepair(final BlockPos builder) {
        // Survival pens are reinspected rather than repaired from a schematic.
    }
}
