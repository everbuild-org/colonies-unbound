package org.everbuild.unbound.minecolonies;

import com.minecolonies.api.colony.IColony;
import net.minecraft.core.BlockPos;

/** Common operations exposed by native herder buildings backed by survival pen volumes. */
public interface SurvivalAnimalPenBuilding {
    IColony getColony();

    BlockPos getPosition();

    int getBuildingLevel();

    void setBuildingLevel(int level);

    void setCorners(BlockPos minimum, BlockPos maximum);

    void addContainerPosition(BlockPos position);

    void removeContainerPosition(BlockPos position);

    void markDirty();
}
