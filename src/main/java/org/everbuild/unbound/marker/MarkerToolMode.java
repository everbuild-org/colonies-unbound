package org.everbuild.unbound.marker;

import java.util.Arrays;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.Nullable;

/** Editing mode carried by each Worksite Marker stack. */
public enum MarkerToolMode {
    RESIDENCE_AREA("residence_area", null),
    STORAGE("storage", WorksitePoiType.STORAGE),
    WORKSITE("worksite", WorksitePoiType.WORKSITE),
    ENTRANCE("entrance", WorksitePoiType.ENTRANCE),
    INTERACTION("interaction", WorksitePoiType.INTERACTION),
    PATROL_ROUTE("patrol_route", WorksitePoiType.PATROL);

    private static final String MODE_KEY = "coloniesunbound_tool_mode";

    private final String id;
    private final WorksitePoiType poiType;

    MarkerToolMode(final String id, @Nullable final WorksitePoiType poiType) {
        this.id = id;
        this.poiType = poiType;
    }

    public String id() {
        return id;
    }

    public @Nullable WorksitePoiType poiType() {
        return poiType;
    }

    public boolean isAreaMode() {
        return poiType == null;
    }

    public MarkerToolMode next() {
        final MarkerToolMode[] modes = values();
        return modes[(ordinal() + 1) % modes.length];
    }

    public static MarkerToolMode read(final ItemStack stack) {
        final CompoundTag root = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        final String id = root.getString(MODE_KEY);
        return Arrays.stream(values())
                .filter(mode -> mode.id.equals(id))
                .findFirst()
                .orElse(RESIDENCE_AREA);
    }

    public static void write(final ItemStack stack, final MarkerToolMode mode) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, root -> root.putString(MODE_KEY, mode.id));
    }
}
