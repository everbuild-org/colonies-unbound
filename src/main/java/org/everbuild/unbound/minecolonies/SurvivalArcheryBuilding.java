package org.everbuild.unbound.minecolonies;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.util.NBTUtils;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingArchery;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.everbuild.unbound.marker.AreaBounds;

/** Native archery academy with scanned targets and glowstone firing lines. */
public final class SurvivalArcheryBuilding extends BuildingArchery implements SurvivalCraftingBuilding {
    private final List<BlockPos> targets = new ArrayList<>();
    private final List<BlockPos> stands = new ArrayList<>();
    public SurvivalArcheryBuilding(final IColony colony, final BlockPos position) { super(colony, position); }
    @Override public boolean canBeBuiltByBuilder(final int level) { return false; }
    @Override public boolean canDeconstruct() { return false; }
    @Override public boolean isBuilt() { return getBuildingLevel() > 0; }
    @Override public void requestUpgrade(final Player player, final BlockPos builder) { }
    @Override public void requestRepair(final BlockPos builder) { }
    @Override public void registerWorkstation(final Level level, final BlockPos position) {
        if (!targets.contains(position)) targets.add(position);
    }
    @Override public void removeWorkstation(final BlockPos position) { targets.remove(position); }
    @Override public void configureWorkArea(final AreaBounds bounds) {
        stands.clear();
        for (final BlockPos position : BlockPos.betweenClosed(bounds.min(), bounds.max())) {
            if (getColony().getWorld().getBlockState(position).is(Blocks.GLOWSTONE)) stands.add(position.immutable());
        }
    }
    @Override public void clearWorkArea() { targets.clear(); stands.clear(); }
    @Override public BlockPos getRandomShootingStandPosition(final RandomSource random) {
        return stands.isEmpty() ? getPosition() : stands.get(random.nextInt(stands.size()));
    }
    @Override public BlockPos getRandomShootingTarget(final RandomSource random) {
        return targets.isEmpty() ? null : targets.get(random.nextInt(targets.size()));
    }
    @Override public CompoundTag serializeNBT(final Provider provider) {
        final CompoundTag tag = super.serializeNBT(provider);
        tag.put("survivalTargets", write(targets)); tag.put("survivalStands", write(stands)); return tag;
    }
    @Override public void deserializeNBT(final Provider provider, final CompoundTag tag) {
        super.deserializeNBT(provider, tag); read(tag, "survivalTargets", targets); read(tag, "survivalStands", stands);
    }
    private static ListTag write(final List<BlockPos> positions) {
        final ListTag list = new ListTag(); positions.forEach(pos -> list.add(NBTUtils.writeBlockPos(pos))); return list;
    }
    private static void read(final CompoundTag tag, final String key, final List<BlockPos> positions) {
        positions.clear(); final ListTag list = tag.getList(key, CompoundTag.TAG_INT_ARRAY);
        for (int i = 0; i < list.size(); i++) positions.add(NBTUtils.readBlockPos(list.get(i)));
    }
}
