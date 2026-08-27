package org.everbuild.unbound.minecolonies;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.entity.citizen.AbstractEntityCitizen;
import com.minecolonies.api.util.NBTUtils;
import com.minecolonies.core.colony.buildings.modules.BuildingModules;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingGateHouse;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.properties.BedPart;
import org.everbuild.unbound.marker.AreaBounds;

/** Native Gate House with target-block guard posts and scanner-owned beds. */
public final class SurvivalGateHouseBuilding extends BuildingGateHouse implements SurvivalCraftingBuilding {
    private final List<BlockPos> posts = new ArrayList<>();
    private final List<BlockPos> beds = new ArrayList<>();
    public SurvivalGateHouseBuilding(final IColony colony, final BlockPos position) { super(colony, position); }
    @Override public boolean canBeBuiltByBuilder(final int level) { return false; }
    @Override public boolean canDeconstruct() { return false; }
    @Override public boolean isBuilt() { return getBuildingLevel() > 0; }
    @Override public void requestUpgrade(final Player player, final BlockPos builder) { }
    @Override public void requestRepair(final BlockPos builder) { }
    @Override public void registerWorkstation(final Level level, final BlockPos position) { if (!posts.contains(position)) posts.add(position); }
    @Override public void removeWorkstation(final BlockPos position) { posts.remove(position); }
    @Override public void configureWorkArea(final AreaBounds bounds) {
        beds.forEach(position -> getModule(BuildingModules.BED).removeBed(position)); beds.clear();
        final Level level = getColony().getWorld();
        for (final BlockPos position : BlockPos.betweenClosed(bounds.min(), bounds.max())) {
            final var state = level.getBlockState(position);
            if (state.getBlock() instanceof BedBlock && state.getValue(BedBlock.PART) == BedPart.HEAD) {
                beds.add(position.immutable()); registerBlockPosition(state, position, level);
            }
        }
    }
    @Override public void clearWorkArea() {
        beds.forEach(position -> getModule(BuildingModules.BED).removeBed(position)); beds.clear(); posts.clear();
    }
    @Override public BlockPos getGuardPos(final AbstractEntityCitizen worker) {
        return posts.isEmpty() ? getPosition() : posts.get(Math.floorMod(worker.getId(), posts.size()));
    }
    @Override public CompoundTag serializeNBT(final Provider provider) {
        final CompoundTag tag = super.serializeNBT(provider); final ListTag list = new ListTag();
        posts.forEach(pos -> list.add(NBTUtils.writeBlockPos(pos))); tag.put("survivalPosts", list); return tag;
    }
    @Override public void deserializeNBT(final Provider provider, final CompoundTag tag) {
        super.deserializeNBT(provider, tag); posts.clear(); final ListTag list = tag.getList("survivalPosts", CompoundTag.TAG_INT_ARRAY);
        for (int i = 0; i < list.size(); i++) posts.add(NBTUtils.readBlockPos(list.get(i)));
    }
}
