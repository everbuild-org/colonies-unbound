package org.everbuild.unbound.minecolonies;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.core.colony.buildings.DefaultBuildingInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

/** A logical Residence whose physical volume is supplied by survival inspection, not a schematic. */
public final class SurvivalResidenceBuilding extends DefaultBuildingInstance {
    public SurvivalResidenceBuilding(final IColony colony, final BlockPos position) {
        super(colony, position, "survival_residence", 5);
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
        // Survival residences are reinspected rather than upgraded from a schematic.
    }

    @Override
    public void requestRepair(final BlockPos builder) {
        // Survival residences are reinspected rather than repaired from a schematic.
    }
}
