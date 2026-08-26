package org.everbuild.unbound.minecolonies;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.util.NBTUtils;
import com.minecolonies.api.util.constant.ColonyConstants;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingSchool;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WoolCarpetBlock;

/** Native school backed by scanner-owned carpet seats. */
public final class SurvivalSchoolBuilding extends BuildingSchool implements SurvivalCraftingBuilding {
    private static final String SURVIVAL_SEATS = "survivalSeats";
    private final List<BlockPos> survivalSeats = new ArrayList<>();

    public SurvivalSchoolBuilding(final IColony colony, final BlockPos position) { super(colony, position); }
    @Override public boolean canBeBuiltByBuilder(final int newLevel) { return false; }
    @Override public boolean canDeconstruct() { return false; }
    @Override public boolean isBuilt() { return getBuildingLevel() > 0; }
    @Override public void requestUpgrade(final Player player, final BlockPos builder) { }
    @Override public void requestRepair(final BlockPos builder) { }

    @Override
    public void registerWorkstation(final Level level, final BlockPos position) {
        if (!survivalSeats.contains(position)) survivalSeats.add(position);
    }

    @Override public void removeWorkstation(final BlockPos position) { survivalSeats.remove(position); }

    @Override
    public BlockPos getRandomPlaceToSit() {
        survivalSeats.removeIf(position -> !(getColony().getWorld().getBlockState(position).getBlock() instanceof WoolCarpetBlock));
        return survivalSeats.isEmpty() ? null : survivalSeats.get(ColonyConstants.rand.nextInt(survivalSeats.size()));
    }

    @Override
    public CompoundTag serializeNBT(final Provider provider) {
        final CompoundTag tag = super.serializeNBT(provider);
        final ListTag seats = new ListTag();
        survivalSeats.forEach(position -> seats.add(NBTUtils.writeBlockPos(position)));
        tag.put(SURVIVAL_SEATS, seats);
        return tag;
    }

    @Override
    public void deserializeNBT(final Provider provider, final CompoundTag tag) {
        super.deserializeNBT(provider, tag);
        survivalSeats.clear();
        final ListTag seats = tag.getList(SURVIVAL_SEATS, CompoundTag.TAG_INT_ARRAY);
        for (int index = 0; index < seats.size(); index++) survivalSeats.add(NBTUtils.readBlockPos(seats.get(index)));
    }
}
