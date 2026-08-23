package org.everbuild.unbound.marker;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.Nullable;

/**
 * The selection carried by a Worksite Marker stack.
 *
 * <p>Using vanilla custom item data keeps the authoritative server mutation synchronized to the
 * client without introducing a custom packet before marker collections need their own storage.</p>
 */
public record MarkerSelection(
        ResourceLocation dimension,
        BlockPos firstCorner,
        @Nullable BlockPos secondCorner) {
    private static final String ROOT_KEY = "coloniesunbound_selection";
    private static final String DIMENSION_KEY = "dimension";
    private static final String FIRST_KEY = "first";
    private static final String SECOND_KEY = "second";

    public static @Nullable MarkerSelection read(final ItemStack stack) {
        final CompoundTag root = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!root.contains(ROOT_KEY, CompoundTag.TAG_COMPOUND)) {
            return null;
        }

        final CompoundTag tag = root.getCompound(ROOT_KEY);
        final ResourceLocation dimension = ResourceLocation.tryParse(tag.getString(DIMENSION_KEY));
        if (dimension == null || !tag.contains(FIRST_KEY, CompoundTag.TAG_LONG)) {
            return null;
        }

        final BlockPos first = BlockPos.of(tag.getLong(FIRST_KEY));
        final BlockPos second = tag.contains(SECOND_KEY, CompoundTag.TAG_LONG)
                ? BlockPos.of(tag.getLong(SECOND_KEY))
                : null;
        return new MarkerSelection(dimension, first, second);
    }

    public static void write(final ItemStack stack, final MarkerSelection selection) {
        final CompoundTag selectionTag = new CompoundTag();
        selectionTag.putString(DIMENSION_KEY, selection.dimension().toString());
        selectionTag.putLong(FIRST_KEY, selection.firstCorner().asLong());
        if (selection.secondCorner() != null) {
            selectionTag.putLong(SECOND_KEY, selection.secondCorner().asLong());
        }

        CustomData.update(DataComponents.CUSTOM_DATA, stack, root -> root.put(ROOT_KEY, selectionTag));
    }

    public static void clear(final ItemStack stack) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, root -> root.remove(ROOT_KEY));
    }

    public boolean isComplete() {
        return secondCorner != null;
    }

    public @Nullable AreaBounds bounds() {
        return secondCorner == null ? null : AreaBounds.between(firstCorner, secondCorner);
    }
}
