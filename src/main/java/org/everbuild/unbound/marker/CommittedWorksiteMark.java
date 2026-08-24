package org.everbuild.unbound.marker;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

/** Building-owned marker that is synchronized and persisted with its anchor block entity. */
public record CommittedWorksiteMark(UUID id, MarkerType type, AreaBounds bounds, List<WorksitePoi> pois) {
    private static final String ID_KEY = "id";
    private static final String TYPE_KEY = "type";
    private static final String MIN_KEY = "min";
    private static final String MAX_KEY = "max";
    private static final String POIS_KEY = "pois";

    public CommittedWorksiteMark {
        pois = List.copyOf(pois);
    }

    public CommittedWorksiteMark(final UUID id, final MarkerType type, final AreaBounds bounds) {
        this(id, type, bounds, List.of());
    }

    public CompoundTag save() {
        final CompoundTag tag = new CompoundTag();
        tag.putUUID(ID_KEY, id);
        tag.putString(TYPE_KEY, type.id());
        tag.putLong(MIN_KEY, bounds.min().asLong());
        tag.putLong(MAX_KEY, bounds.max().asLong());
        final ListTag poiTags = new ListTag();
        for (final WorksitePoi poi : pois) {
            final CompoundTag poiTag = new CompoundTag();
            poiTag.putString(TYPE_KEY, poi.type().id());
            poiTag.putLong("position", poi.position().asLong());
            poiTags.add(poiTag);
        }
        tag.put(POIS_KEY, poiTags);
        return tag;
    }

    public static CommittedWorksiteMark load(final CompoundTag tag) {
        if (!tag.hasUUID(ID_KEY) || !tag.contains(MIN_KEY) || !tag.contains(MAX_KEY)) {
            return null;
        }
        final List<WorksitePoi> pois = new ArrayList<>();
        for (final Tag entry : tag.getList(POIS_KEY, Tag.TAG_COMPOUND)) {
            if (entry instanceof CompoundTag poiTag) {
                final WorksitePoiType poiType = WorksitePoiType.fromId(poiTag.getString(TYPE_KEY));
                if (poiType != null && poiTag.contains("position")) {
                    pois.add(new WorksitePoi(poiType, BlockPos.of(poiTag.getLong("position"))));
                }
            }
        }
        return new CommittedWorksiteMark(
                tag.getUUID(ID_KEY),
                MarkerType.fromId(tag.getString(TYPE_KEY)),
                AreaBounds.between(BlockPos.of(tag.getLong(MIN_KEY)), BlockPos.of(tag.getLong(MAX_KEY))),
                pois);
    }
}
