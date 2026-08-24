package org.everbuild.unbound.minecolonies;

import com.minecolonies.api.colony.IColony;
import net.minecraft.core.BlockPos;

/** Common native-building operations used by survival crafting adapters. */
public interface SurvivalCraftingBuilding {
    IColony getColony();
    BlockPos getPosition();
    int getBuildingLevel();
    void setBuildingLevel(int level);
    void setCorners(BlockPos minimum, BlockPos maximum);
    void addContainerPosition(BlockPos position);
    void removeContainerPosition(BlockPos position);
    void markDirty();
}
