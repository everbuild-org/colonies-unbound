package org.everbuild.unbound.item;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import org.everbuild.unbound.marker.AreaBounds;
import org.everbuild.unbound.marker.MarkerSelection;

/** Selects an inclusive survival-building volume using two block clicks. */
public final class WorksiteMarkerItem extends Item {
    public static final int MAXIMUM_AXIS_LENGTH = 128;

    public WorksiteMarkerItem(final Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult useOn(final UseOnContext context) {
        final ItemStack stack = context.getItemInHand();
        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            if (!context.getLevel().isClientSide) {
                MarkerSelection.clear(stack);
                context.getPlayer().displayClientMessage(
                        Component.translatable("message.coloniesunbound.marker.cleared"), true);
            }
            return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
        }

        if (!context.getLevel().isClientSide) {
            selectCorner(context, stack);
        }
        return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
    }

    private static void selectCorner(final UseOnContext context, final ItemStack stack) {
        final BlockPos clicked = context.getClickedPos().immutable();
        final ResourceLocation dimension = context.getLevel().dimension().location();
        final MarkerSelection existing = MarkerSelection.read(stack);

        if (existing == null || existing.isComplete() || !existing.dimension().equals(dimension)) {
            MarkerSelection.write(stack, new MarkerSelection(dimension, clicked, null));
            context.getPlayer().displayClientMessage(
                    Component.translatable(
                            "message.coloniesunbound.marker.first_corner",
                            clicked.getX(), clicked.getY(), clicked.getZ()),
                    true);
            return;
        }

        final AreaBounds bounds = AreaBounds.between(existing.firstCorner(), clicked);
        if (!bounds.fitsWithin(MAXIMUM_AXIS_LENGTH)) {
            context.getPlayer().displayClientMessage(
                    Component.translatable(
                                    "message.coloniesunbound.marker.too_large", MAXIMUM_AXIS_LENGTH)
                            .withStyle(ChatFormatting.RED),
                    true);
            return;
        }

        MarkerSelection.write(stack, new MarkerSelection(dimension, existing.firstCorner(), clicked));
        context.getPlayer().displayClientMessage(
                Component.translatable(
                        "message.coloniesunbound.marker.area_selected",
                        bounds.sizeX(), bounds.sizeY(), bounds.sizeZ(), bounds.volume()),
                true);
    }

    @Override
    public void appendHoverText(
            final ItemStack stack,
            final TooltipContext context,
            final List<Component> tooltip,
            final TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.coloniesunbound.worksite_marker.use")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.coloniesunbound.worksite_marker.clear")
                .withStyle(ChatFormatting.DARK_GRAY));

        final MarkerSelection selection = MarkerSelection.read(stack);
        if (selection == null) {
            return;
        }

        final AreaBounds bounds = selection.bounds();
        if (bounds == null) {
            tooltip.add(Component.translatable("tooltip.coloniesunbound.worksite_marker.pending")
                    .withStyle(ChatFormatting.AQUA));
        } else {
            tooltip.add(Component.translatable(
                            "tooltip.coloniesunbound.worksite_marker.area",
                            bounds.sizeX(), bounds.sizeY(), bounds.sizeZ())
                    .withStyle(ChatFormatting.GOLD));
        }
    }
}
