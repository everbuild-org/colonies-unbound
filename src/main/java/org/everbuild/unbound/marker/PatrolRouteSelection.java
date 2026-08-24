package org.everbuild.unbound.marker;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.Nullable;

/** Guard plaque selected as the owner of subsequently placed patrol nodes. */
public record PatrolRouteSelection(ResourceLocation dimension, BlockPos plaquePosition) {
    private static final String ROOT_KEY = "coloniesunbound_patrol_owner";
    private static final String DIMENSION_KEY = "dimension";
    private static final String POSITION_KEY = "position";

    public static @Nullable PatrolRouteSelection read(final ItemStack stack) {
        final CompoundTag root = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!root.contains(ROOT_KEY, CompoundTag.TAG_COMPOUND)) {
            return null;
        }
        final CompoundTag tag = root.getCompound(ROOT_KEY);
        final ResourceLocation dimension = ResourceLocation.tryParse(tag.getString(DIMENSION_KEY));
        return dimension == null || !tag.contains(POSITION_KEY, CompoundTag.TAG_LONG)
                ? null
                : new PatrolRouteSelection(dimension, BlockPos.of(tag.getLong(POSITION_KEY)));
    }

    public static void write(final ItemStack stack, final PatrolRouteSelection selection) {
        final CompoundTag tag = new CompoundTag();
        tag.putString(DIMENSION_KEY, selection.dimension().toString());
        tag.putLong(POSITION_KEY, selection.plaquePosition().asLong());
        CustomData.update(DataComponents.CUSTOM_DATA, stack, root -> root.put(ROOT_KEY, tag));
    }

    public static void clear(final ItemStack stack) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, root -> root.remove(ROOT_KEY));
    }
}
