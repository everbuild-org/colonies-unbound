package org.everbuild.unbound.minecolonies;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.util.MathUtils;
import com.minecolonies.api.util.NBTUtils;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingUniversity;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;

/** Native university backed by scanner-owned bookshelves. */
public final class SurvivalUniversityBuilding extends BuildingUniversity implements SurvivalCraftingBuilding {
    private static final String SURVIVAL_SHELVES = "survivalShelves";
    private final List<BlockPos> survivalShelves = new ArrayList<>();

    public SurvivalUniversityBuilding(final IColony colony, final BlockPos position) { super(colony, position); }
    @Override public boolean canBeBuiltByBuilder(final int newLevel) { return false; }
    @Override public boolean canDeconstruct() { return false; }
    @Override public boolean isBuilt() { return getBuildingLevel() > 0; }
    @Override public void requestUpgrade(final Player player, final BlockPos builder) { }
    @Override public void requestRepair(final BlockPos builder) { }
    @Override public void registerWorkstation(final Level level, final BlockPos position) {
        if (!survivalShelves.contains(position)) survivalShelves.add(position);
    }
    @Override public void removeWorkstation(final BlockPos position) { survivalShelves.remove(position); }
    @Override public BlockPos getRandomBookShelf() {
        survivalShelves.removeIf(position -> !getColony().getWorld().getBlockState(position).is(Tags.Blocks.BOOKSHELVES));
        return survivalShelves.isEmpty() ? getPosition() : survivalShelves.get(MathUtils.RANDOM.nextInt(survivalShelves.size()));
    }
    @Override public CompoundTag serializeNBT(final Provider provider) {
        final CompoundTag tag = super.serializeNBT(provider);
        final ListTag shelves = new ListTag();
        survivalShelves.forEach(position -> shelves.add(NBTUtils.writeBlockPos(position)));
        tag.put(SURVIVAL_SHELVES, shelves);
        return tag;
    }
    @Override public void deserializeNBT(final Provider provider, final CompoundTag tag) {
        super.deserializeNBT(provider, tag);
        survivalShelves.clear();
        final ListTag shelves = tag.getList(SURVIVAL_SHELVES, CompoundTag.TAG_INT_ARRAY);
        for (int index = 0; index < shelves.size(); index++) survivalShelves.add(NBTUtils.readBlockPos(shelves.get(index)));
    }
}
