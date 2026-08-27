package org.everbuild.unbound.minecolonies;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingMiner;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LadderBlock;

/** Native Miner with a scanner-owned shaft ladder and its supporting wall block. */
public final class SurvivalMinerBuilding extends BuildingMiner implements SurvivalCraftingBuilding {
    private BlockPos ladder;
    private BlockPos support;
    public SurvivalMinerBuilding(final IColony colony, final BlockPos position) { super(colony, position); }
    @Override public boolean canBeBuiltByBuilder(final int level) { return false; }
    @Override public boolean canDeconstruct() { return false; }
    @Override public boolean isBuilt() { return getBuildingLevel() > 0; }
    @Override public void requestUpgrade(final Player player, final BlockPos builder) { }
    @Override public void requestRepair(final BlockPos builder) { }
    @Override public void registerWorkstation(final Level level, final BlockPos position) {
        ladder = position; support = position.relative(level.getBlockState(position).getValue(LadderBlock.FACING).getOpposite());
    }
    @Override public void removeWorkstation(final BlockPos position) { if (position.equals(ladder)) { ladder = null; support = null; } }
    @Override public BlockPos getLadderLocation() { return ladder; }
    @Override public BlockPos getCobbleLocation() { return support; }
    @Override public CompoundTag serializeNBT(final Provider provider) {
        final CompoundTag tag = super.serializeNBT(provider); BlockPosUtil.writeOptional(tag, "survivalLadder", ladder);
        BlockPosUtil.writeOptional(tag, "survivalSupport", support); return tag;
    }
    @Override public void deserializeNBT(final Provider provider, final CompoundTag tag) {
        super.deserializeNBT(provider, tag); ladder = BlockPosUtil.readOrNull(tag, "survivalLadder");
        support = BlockPosUtil.readOrNull(tag, "survivalSupport");
    }
}
