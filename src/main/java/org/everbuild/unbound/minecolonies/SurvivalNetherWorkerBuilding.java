package org.everbuild.unbound.minecolonies;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.util.BlockPosUtil;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingNetherWorker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** Native Nether Worker backed by a scanner-owned live portal block. */
public final class SurvivalNetherWorkerBuilding extends BuildingNetherWorker implements SurvivalCraftingBuilding {
    private static final String SURVIVAL_PORTAL = "survivalPortal";
    private BlockPos survivalPortal;

    public SurvivalNetherWorkerBuilding(final IColony colony, final BlockPos position) { super(colony, position); }
    @Override public boolean canBeBuiltByBuilder(final int newLevel) { return false; }
    @Override public boolean canDeconstruct() { return false; }
    @Override public boolean isBuilt() { return getBuildingLevel() > 0; }
    @Override public void requestUpgrade(final Player player, final BlockPos builder) { }
    @Override public void requestRepair(final BlockPos builder) { }
    @Override public void registerWorkstation(final Level level, final BlockPos position) { survivalPortal = position; }
    @Override public void removeWorkstation(final BlockPos position) {
        if (position.equals(survivalPortal)) survivalPortal = null;
    }
    @Override public BlockPos getPortalLocation() { return survivalPortal; }
    @Override public CompoundTag serializeNBT(final Provider provider) {
        final CompoundTag tag = super.serializeNBT(provider);
        if (survivalPortal != null) BlockPosUtil.write(tag, SURVIVAL_PORTAL, survivalPortal);
        return tag;
    }
    @Override public void deserializeNBT(final Provider provider, final CompoundTag tag) {
        super.deserializeNBT(provider, tag);
        survivalPortal = tag.contains(SURVIVAL_PORTAL) ? BlockPosUtil.read(tag, SURVIVAL_PORTAL) : null;
    }
}
