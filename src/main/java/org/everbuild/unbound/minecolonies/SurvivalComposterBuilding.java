package org.everbuild.unbound.minecolonies;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.util.NBTUtils;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingComposter;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** Native Composter building with a removable survival barrel registry. */
public final class SurvivalComposterBuilding extends BuildingComposter implements SurvivalCraftingBuilding {
    private static final String SURVIVAL_BARRELS = "survivalBarrels";
    private final List<BlockPos> survivalBarrels = new ArrayList<>();

    public SurvivalComposterBuilding(final IColony colony, final BlockPos position) { super(colony, position); }
    @Override public boolean canBeBuiltByBuilder(final int newLevel) { return false; }
    @Override public boolean canDeconstruct() { return false; }
    @Override public boolean isBuilt() { return getBuildingLevel() > 0; }
    @Override public void requestUpgrade(final Player player, final BlockPos builder) { }
    @Override public void requestRepair(final BlockPos builder) { }
    @Override public List<BlockPos> getBarrels() { return List.copyOf(survivalBarrels); }
    @Override public void registerWorkstation(final Level level, final BlockPos position) {
        if (!survivalBarrels.contains(position)) survivalBarrels.add(position);
    }
    @Override public void removeWorkstation(final BlockPos position) { survivalBarrels.remove(position); }

    @Override
    public CompoundTag serializeNBT(final Provider provider) {
        final CompoundTag tag = super.serializeNBT(provider);
        final ListTag barrels = new ListTag();
        survivalBarrels.forEach(position -> barrels.add(NBTUtils.writeBlockPos(position)));
        tag.put(SURVIVAL_BARRELS, barrels);
        return tag;
    }

    @Override
    public void deserializeNBT(final Provider provider, final CompoundTag tag) {
        super.deserializeNBT(provider, tag);
        survivalBarrels.clear();
        final ListTag barrels = tag.getList(SURVIVAL_BARRELS, CompoundTag.TAG_INT_ARRAY);
        for (int index = 0; index < barrels.size(); index++) {
            survivalBarrels.add(NBTUtils.readBlockPos(barrels.get(index)));
        }
    }
}
