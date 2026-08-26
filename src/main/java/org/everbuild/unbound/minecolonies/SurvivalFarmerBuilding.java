package org.everbuild.unbound.minecolonies;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.core.colony.buildings.modules.BuildingModules;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingFarmer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** Native Farmer building backed by survival-owned scarecrow fields. */
public final class SurvivalFarmerBuilding extends BuildingFarmer implements SurvivalCraftingBuilding {
    public SurvivalFarmerBuilding(final IColony colony, final BlockPos position) { super(colony, position); }
    @Override public boolean canBeBuiltByBuilder(final int newLevel) { return false; }
    @Override public boolean canDeconstruct() { return false; }
    @Override public boolean isBuilt() { return getBuildingLevel() > 0; }
    @Override public void requestUpgrade(final Player player, final BlockPos builder) { }
    @Override public void requestRepair(final BlockPos builder) { }

    @Override
    public void registerWorkstation(final Level level, final BlockPos position) {
        final FarmerFieldsModule fields = getModule(BuildingModules.FARMER_FIELDS);
        getColony().getServerBuildingManager().getBuildingExtensions(extension -> extension.getPosition().equals(position))
                .forEach(fields::assignExtension);
    }

    @Override
    public void removeWorkstation(final BlockPos position) {
        final FarmerFieldsModule fields = getModule(BuildingModules.FARMER_FIELDS);
        fields.getOwnedExtensions().stream().filter(extension -> extension.getPosition().equals(position))
                .toList().forEach(fields::freeExtension);
    }
}
