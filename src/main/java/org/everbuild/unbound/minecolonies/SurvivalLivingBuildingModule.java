package org.everbuild.unbound.minecolonies;

import com.minecolonies.core.colony.buildings.modules.BedHandlingModule;
import com.minecolonies.core.colony.buildings.modules.BuildingModules;
import com.minecolonies.core.colony.buildings.modules.LivingBuildingModule;

/** Keeps native citizen assignment while deriving capacity from registered survival beds. */
public final class SurvivalLivingBuildingModule extends LivingBuildingModule {
    @Override
    public int getModuleMax() {
        if (building == null || !building.hasModule(BuildingModules.BED)) {
            return 0;
        }
        final BedHandlingModule beds = building.getModule(BuildingModules.BED);
        return beds.getRegisteredBlocks().size();
    }
}
