package org.everbuild.unbound.minecolonies;

import com.minecolonies.core.tileentities.TileEntityColonyBuilding;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.everbuild.unbound.marker.CommittedWorksiteMark;
import org.everbuild.unbound.marker.WorksitePoi;
import org.everbuild.unbound.marker.WorksitePoiType;

/** MineColonies anchor that owns and exposes one committed survival-defined volume. */
public abstract class MarkedBuildingTileEntity extends TileEntityColonyBuilding {
    private static final String COMMITTED_MARK_KEY = "coloniesunbound_committed_mark";

    private CommittedWorksiteMark committedMark;

    protected MarkedBuildingTileEntity(
            final BlockEntityType<? extends MarkedBuildingTileEntity> type,
            final BlockPos position,
            final BlockState state) {
        super(type, position, state);
    }

    public final CommittedWorksiteMark committedMark() {
        return committedMark;
    }

    public final void setCommittedMark(final CommittedWorksiteMark mark) {
        committedMark = mark;
        synchronizeSchematicMetadata(mark);
        syncChangedState();
    }

    public final void clearCommittedMark() {
        committedMark = null;
        setPositionedTags(Map.of());
        syncChangedState();
    }

    private void synchronizeSchematicMetadata(final CommittedWorksiteMark mark) {
        setSchematicCorners(
                mark.bounds().min().subtract(worldPosition),
                mark.bounds().max().subtract(worldPosition));

        final Map<BlockPos, List<String>> positionedTags = new LinkedHashMap<>();
        if (mark.type() == org.everbuild.unbound.marker.MarkerType.SIMPLE_QUARRY
                || mark.type() == org.everbuild.unbound.marker.MarkerType.MEDIUM_QUARRY) {
            positionedTags.put(BlockPos.ZERO, new ArrayList<>(List.of(
                    "shaft=infrastructure/mineshafts/" + mark.type().id().replace("_", "") + "shaft1.blueprint")));
        }
        for (final WorksitePoi poi : mark.pois()) {
            final String tag = schematicTag(poi.type());
            if (tag != null) {
                positionedTags
                        .computeIfAbsent(poi.position().subtract(worldPosition), ignored -> new ArrayList<>())
                        .add(tag);
            }
        }
        setPositionedTags(positionedTags);
    }

    private static String schematicTag(final WorksitePoiType type) {
        return switch (type) {
            case WORKSITE -> "work";
            case ENTRANCE -> "entrance";
            case INTERACTION -> "sit";
            case PATROL -> null;
            case PASTURE -> null;
            case STALL -> "stall";
            case HIVE -> null;
            default -> null;
        };
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
        if (committedMark != null) {
            synchronizeSchematicMetadata(committedMark);
        }
    }

    @Override
    public void saveAdditional(final CompoundTag tag, final HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (committedMark != null) {
            tag.put(COMMITTED_MARK_KEY, committedMark.save());
        }
    }
}
