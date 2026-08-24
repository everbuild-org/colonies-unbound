package org.everbuild.unbound.marker;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

/** Building-owned marker that is synchronized and persisted with its anchor block entity. */
public record CommittedWorksiteMark(UUID id, MarkerType type, AreaBounds bounds) {
    private static final String ID_KEY = "id";
    private static final String TYPE_KEY = "type";
    private static final String MIN_KEY = "min";
    private static final String MAX_KEY = "max";

    public CompoundTag save() {
        final CompoundTag tag = new CompoundTag();
        tag.putUUID(ID_KEY, id);
        tag.putString(TYPE_KEY, type.id());
        tag.putLong(MIN_KEY, bounds.min().asLong());
        tag.putLong(MAX_KEY, bounds.max().asLong());
        return tag;
    }

    public static CommittedWorksiteMark load(final CompoundTag tag) {
        if (!tag.hasUUID(ID_KEY) || !tag.contains(MIN_KEY) || !tag.contains(MAX_KEY)) {
            return null;
        }
        return new CommittedWorksiteMark(
                tag.getUUID(ID_KEY),
                MarkerType.fromId(tag.getString(TYPE_KEY)),
                AreaBounds.between(BlockPos.of(tag.getLong(MIN_KEY)), BlockPos.of(tag.getLong(MAX_KEY))));
    }
}
