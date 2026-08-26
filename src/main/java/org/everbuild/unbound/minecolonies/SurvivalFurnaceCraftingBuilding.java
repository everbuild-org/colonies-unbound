package org.everbuild.unbound.minecolonies;

import com.minecolonies.core.colony.buildings.AbstractBuilding;
import com.minecolonies.core.colony.buildings.modules.BuildingModules;
import com.minecolonies.core.colony.buildings.modules.FurnaceUserModule;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/** Shared native furnace-list lifecycle for survival smelting workplaces. */
public interface SurvivalFurnaceCraftingBuilding extends SurvivalCraftingBuilding {
    private FurnaceUserModule furnaces() {
        return ((AbstractBuilding) this).getModule(BuildingModules.FURNACE);
    }

    @Override
    default void registerWorkstation(final Level level, final BlockPos position) {
        furnaces().onBlockPlacedInBuilding(level.getBlockState(position), position, level);
    }

    @Override
    default void removeWorkstation(final BlockPos position) {
        furnaces().removeFromFurnaces(position);
    }
}
