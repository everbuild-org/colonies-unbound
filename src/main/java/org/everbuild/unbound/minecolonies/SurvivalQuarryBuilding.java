package org.everbuild.unbound.minecolonies;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.core.colony.buildings.DefaultBuildingInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import org.everbuild.unbound.marker.AreaBounds;

/** Native quarry controller whose committed volume defines its footprint. */
public final class SurvivalQuarryBuilding extends DefaultBuildingInstance implements SurvivalCraftingBuilding {
    public SurvivalQuarryBuilding(final IColony colony, final BlockPos position, final String name) {
        super(colony, position, name, 1);
    }
    @Override public boolean canBeBuiltByBuilder(final int level) { return false; }
    @Override public boolean canDeconstruct() { return false; }
    @Override public boolean isBuilt() { return getBuildingLevel() > 0; }
    @Override public void requestUpgrade(final Player player, final BlockPos builder) { }
    @Override public void requestRepair(final BlockPos builder) { }
    @Override public void configureWorkArea(final AreaBounds bounds) {
        final var townHall = getColony().getServerBuildingManager().getTownHall();
        if (townHall != null) setStructurePack(townHall.getStructurePack());
    }
}
