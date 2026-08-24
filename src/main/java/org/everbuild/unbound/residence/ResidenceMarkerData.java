package org.everbuild.unbound.residence;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import org.everbuild.unbound.marker.AreaBounds;

/** Per-dimension persistent collection of survival Residence markers. */
public final class ResidenceMarkerData extends SavedData {
    private static final String DATA_NAME = "coloniesunbound_residences";
    private static final String MARKERS_KEY = "markers";
    private static final Factory<ResidenceMarkerData> FACTORY = new Factory<>(
            ResidenceMarkerData::new, ResidenceMarkerData::load, DataFixTypes.LEVEL);

    private final Map<UUID, SurvivalResidenceMarker> markers = new LinkedHashMap<>();

    public static ResidenceMarkerData get(final ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FACTORY, DATA_NAME);
    }

    public Collection<SurvivalResidenceMarker> markers() {
        return java.util.List.copyOf(markers.values());
    }

    public SurvivalResidenceMarker put(final SurvivalResidenceMarker marker) {
        markers.put(marker.id(), marker);
        setDirty();
        return marker;
    }

    public SurvivalResidenceMarker remove(final UUID markerId) {
        final SurvivalResidenceMarker removed = markers.remove(markerId);
        if (removed != null) {
            setDirty();
        }
        return removed;
    }

    @Override
    public CompoundTag save(final CompoundTag tag, final HolderLookup.Provider registries) {
        final ListTag markerTags = new ListTag();
        for (final SurvivalResidenceMarker marker : markers.values()) {
            final CompoundTag markerTag = new CompoundTag();
            markerTag.putUUID("id", marker.id());
            markerTag.putInt("colony", marker.colonyId());
            markerTag.putUUID("owner", marker.ownerId());
            markerTag.putLong("min", marker.bounds().min().asLong());
            markerTag.putLong("max", marker.bounds().max().asLong());
            markerTag.putLongArray("beds", marker.bedHeads().stream().map(BlockPos::asLong).toList());
            markerTag.putLong("inspected_at", marker.inspectedAt());
            markerTags.add(markerTag);
        }
        tag.put(MARKERS_KEY, markerTags);
        return tag;
    }

    public static ResidenceMarkerData load(final CompoundTag tag, final HolderLookup.Provider registries) {
        final ResidenceMarkerData data = new ResidenceMarkerData();
        final ListTag markerTags = tag.getList(MARKERS_KEY, Tag.TAG_COMPOUND);
        for (final Tag entry : markerTags) {
            if (!(entry instanceof CompoundTag markerTag)
                    || !markerTag.hasUUID("id")
                    || !markerTag.hasUUID("owner")) {
                continue;
            }
            final long[] beds = markerTag.getLongArray("beds");
            final SurvivalResidenceMarker marker = new SurvivalResidenceMarker(
                    markerTag.getUUID("id"),
                    markerTag.getInt("colony"),
                    markerTag.getUUID("owner"),
                    AreaBounds.between(
                            BlockPos.of(markerTag.getLong("min")),
                            BlockPos.of(markerTag.getLong("max"))),
                    java.util.Arrays.stream(beds).mapToObj(BlockPos::of).toList(),
                    markerTag.getLong("inspected_at"));
            data.markers.put(marker.id(), marker);
        }
        return data;
    }
}
