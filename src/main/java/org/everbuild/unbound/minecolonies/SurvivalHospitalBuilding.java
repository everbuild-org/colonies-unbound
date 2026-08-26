package org.everbuild.unbound.minecolonies;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.util.NBTUtils;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingHospital;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** Native hospital backed by scanner-owned patient beds. */
public final class SurvivalHospitalBuilding extends BuildingHospital implements SurvivalCraftingBuilding {
    private static final String SURVIVAL_BEDS = "survivalBeds";
    private final List<BlockPos> survivalBeds = new ArrayList<>();

    public SurvivalHospitalBuilding(final IColony colony, final BlockPos position) { super(colony, position); }
    @Override public boolean canBeBuiltByBuilder(final int newLevel) { return false; }
    @Override public boolean canDeconstruct() { return false; }
    @Override public boolean isBuilt() { return getBuildingLevel() > 0; }
    @Override public void requestUpgrade(final Player player, final BlockPos builder) { }
    @Override public void requestRepair(final BlockPos builder) { }
    @Override public List<BlockPos> getBedList() { return List.copyOf(survivalBeds); }

    @Override
    public void registerWorkstation(final Level level, final BlockPos position) {
        if (!survivalBeds.contains(position)) survivalBeds.add(position);
        registerBlockPosition(level.getBlockState(position), position, level);
    }

    @Override public void removeWorkstation(final BlockPos position) { survivalBeds.remove(position); }

    @Override
    public CompoundTag serializeNBT(final Provider provider) {
        final CompoundTag tag = super.serializeNBT(provider);
        final ListTag beds = new ListTag();
        survivalBeds.forEach(position -> beds.add(NBTUtils.writeBlockPos(position)));
        tag.put(SURVIVAL_BEDS, beds);
        return tag;
    }

    @Override
    public void deserializeNBT(final Provider provider, final CompoundTag tag) {
        super.deserializeNBT(provider, tag);
        survivalBeds.clear();
        final ListTag beds = tag.getList(SURVIVAL_BEDS, CompoundTag.TAG_INT_ARRAY);
        for (int index = 0; index < beds.size(); index++) survivalBeds.add(NBTUtils.readBlockPos(beds.get(index)));
    }
}
