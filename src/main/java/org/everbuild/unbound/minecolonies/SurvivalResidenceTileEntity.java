package org.everbuild.unbound.minecolonies;

import com.minecolonies.core.tileentities.TileEntityColonyBuilding;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import org.everbuild.unbound.marker.CommittedWorksiteMark;

/** MineColonies building anchor that owns the committed residence volume. */
public final class SurvivalResidenceTileEntity extends TileEntityColonyBuilding {
    private static final String COMMITTED_MARK_KEY = "coloniesunbound_committed_mark";

    private CommittedWorksiteMark committedMark;

    public SurvivalResidenceTileEntity(final BlockPos position, final BlockState state) {
        super(MineColoniesIntegration.SURVIVAL_RESIDENCE_TILE.get(), position, state);
    }

    public CommittedWorksiteMark committedMark() {
        return committedMark;
    }

    public void setCommittedMark(final CommittedWorksiteMark mark) {
        committedMark = mark;
        syncChangedState();
    }

    public void clearCommittedMark() {
        committedMark = null;
        syncChangedState();
    }

    private void syncChangedState() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public void loadAdditional(final CompoundTag tag, final HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        committedMark = tag.contains(COMMITTED_MARK_KEY)
                ? CommittedWorksiteMark.load(tag.getCompound(COMMITTED_MARK_KEY))
                : null;
    }

    @Override
    public void saveAdditional(final CompoundTag tag, final HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (committedMark != null) {
            tag.put(COMMITTED_MARK_KEY, committedMark.save());
        }
    }
}
