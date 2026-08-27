package org.everbuild.unbound.minecolonies;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.util.NBTUtils;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingCombatAcademy;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** Native combat academy with removable scanner-owned training dummies. */
public final class SurvivalCombatAcademyBuilding extends BuildingCombatAcademy implements SurvivalCraftingBuilding {
    private final List<BlockPos> targets = new ArrayList<>();
    public SurvivalCombatAcademyBuilding(final IColony colony, final BlockPos position) { super(colony, position); }
    @Override public boolean canBeBuiltByBuilder(final int level) { return false; }
    @Override public boolean canDeconstruct() { return false; }
    @Override public boolean isBuilt() { return getBuildingLevel() > 0; }
    @Override public void requestUpgrade(final Player player, final BlockPos builder) { }
    @Override public void requestRepair(final BlockPos builder) { }
    @Override public void registerWorkstation(final Level level, final BlockPos position) {
        final BlockPos target = position.below(); if (!targets.contains(target)) targets.add(target);
    }
    @Override public void removeWorkstation(final BlockPos position) { targets.remove(position.below()); }
    @Override public BlockPos getRandomCombatTarget(final RandomSource random) {
        return targets.isEmpty() ? null : targets.get(random.nextInt(targets.size()));
    }
    @Override public CompoundTag serializeNBT(final Provider provider) {
        final CompoundTag tag = super.serializeNBT(provider); final ListTag list = new ListTag();
        targets.forEach(pos -> list.add(NBTUtils.writeBlockPos(pos))); tag.put("survivalTargets", list); return tag;
    }
    @Override public void deserializeNBT(final Provider provider, final CompoundTag tag) {
        super.deserializeNBT(provider, tag); targets.clear(); final ListTag list = tag.getList("survivalTargets", CompoundTag.TAG_INT_ARRAY);
        for (int i = 0; i < list.size(); i++) targets.add(NBTUtils.readBlockPos(list.get(i)));
    }
}
