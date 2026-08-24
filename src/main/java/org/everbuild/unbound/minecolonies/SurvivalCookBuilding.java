package org.everbuild.unbound.minecolonies;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingCook;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

/** Native Cook building whose level and metadata come from a marked survival build. */
public final class SurvivalCookBuilding extends BuildingCook {
    public SurvivalCookBuilding(final IColony colony, final BlockPos position) {
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
    public void requestUpgrade(final Player player, final BlockPos builder) {
        // Survival workplaces are reinspected rather than upgraded from a schematic.
    }

    @Override
    public void requestRepair(final BlockPos builder) {
        // Survival workplaces are reinspected rather than repaired from a schematic.
    }
}
