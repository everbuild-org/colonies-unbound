package org.everbuild.unbound.minecolonies;

import com.minecolonies.api.colony.IColony;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.everbuild.unbound.marker.AreaBounds;

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

    default void registerWorkstation(final Level level, final BlockPos position) { }
    default void removeWorkstation(final BlockPos position) { }
    default void configureWorkArea(final AreaBounds bounds) { }
    default void clearWorkArea() { }
}
