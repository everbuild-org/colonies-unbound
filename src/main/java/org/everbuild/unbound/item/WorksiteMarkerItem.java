package org.everbuild.unbound.item;

import java.util.List;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import org.everbuild.unbound.marker.AreaBounds;
import org.everbuild.unbound.marker.MarkerSelection;
import org.everbuild.unbound.minecolonies.SurvivalResidenceTileEntity;
import org.everbuild.unbound.residence.ResidenceInspection;
import org.everbuild.unbound.residence.ResidenceInspector;
import org.everbuild.unbound.residence.ResidenceMarkerService;

/** Selects an inclusive survival-building volume using two block clicks. */
public final class WorksiteMarkerItem extends Item {
    public static final int MAXIMUM_AXIS_LENGTH = 128;

    public WorksiteMarkerItem(final Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResult useOn(final UseOnContext context) {
        final ItemStack stack = context.getItemInHand();
        if (context.getPlayer() == null) {
            return InteractionResult.PASS;
        }
        if (context.getPlayer().isShiftKeyDown()) {
            if (!context.getLevel().isClientSide) {
                if (context.getLevel() instanceof ServerLevel serverLevel
                        && context.getPlayer() instanceof ServerPlayer serverPlayer
                        && context.getLevel().getBlockEntity(context.getClickedPos())
                                instanceof SurvivalResidenceTileEntity residenceTile
                        && residenceTile.committedMark() != null) {
                    context.getPlayer().displayClientMessage(
                            removalReport(ResidenceMarkerService.removeCommittedMark(
                                    serverLevel, serverPlayer, residenceTile)),
                            true);
                    return InteractionResult.SUCCESS;
                }
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
            MarkerSelection.write(stack, new MarkerSelection(UUID.randomUUID(), dimension, clicked, null));
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

        final MarkerSelection completed = new MarkerSelection(
                existing.markerId(), dimension, existing.firstCorner(), clicked);
        MarkerSelection.write(stack, completed);
        final ResidenceInspection inspection = ResidenceInspector.inspect(context.getLevel(), bounds);
        final Component report = switch (inspection.status()) {
            case VALID -> registrationReport(context, stack, completed, inspection, bounds);
            case NO_BEDS -> Component.translatable("message.coloniesunbound.inspection.no_beds")
                    .withStyle(ChatFormatting.RED);
            case AREA_NOT_LOADED -> Component.translatable("message.coloniesunbound.inspection.not_loaded")
                    .withStyle(ChatFormatting.RED);
            case AREA_TOO_LARGE -> Component.translatable(
                            "message.coloniesunbound.inspection.too_large",
                            ResidenceInspector.MAXIMUM_INSPECTION_VOLUME)
                    .withStyle(ChatFormatting.RED);
            case NO_PLAQUE -> Component.translatable("message.coloniesunbound.inspection.no_plaque")
                    .withStyle(ChatFormatting.RED);
            case MULTIPLE_PLAQUES -> Component.translatable("message.coloniesunbound.inspection.multiple_plaques")
                    .withStyle(ChatFormatting.RED);
        };
        context.getPlayer().displayClientMessage(report, true);
    }

    private static Component registrationReport(
            final UseOnContext context,
            final ItemStack stack,
            final MarkerSelection selection,
            final ResidenceInspection inspection,
            final AreaBounds bounds) {
        if (!(context.getLevel() instanceof ServerLevel serverLevel)
                || !(context.getPlayer() instanceof ServerPlayer serverPlayer)) {
            return Component.empty();
        }
        final ResidenceMarkerService.Result result =
                ResidenceMarkerService.register(serverLevel, serverPlayer, selection, inspection);
        if (result == ResidenceMarkerService.Result.SAVED) {
            MarkerSelection.clear(stack);
        }
        return switch (result) {
            case SAVED -> Component.translatable(
                    "message.coloniesunbound.inspection.saved",
                    bounds.sizeX(), bounds.sizeY(), bounds.sizeZ(), inspection.capacity())
                    .withStyle(ChatFormatting.GREEN);
            case OUTSIDE_COLONY -> Component.translatable("message.coloniesunbound.inspection.outside_colony")
                    .withStyle(ChatFormatting.RED);
            case CROSSES_COLONY_BORDER -> Component.translatable("message.coloniesunbound.inspection.crosses_border")
                    .withStyle(ChatFormatting.RED);
            case NO_PERMISSION -> Component.translatable("message.coloniesunbound.inspection.no_permission")
                    .withStyle(ChatFormatting.RED);
            case PLAQUE_NOT_REGISTERED -> Component.translatable("message.coloniesunbound.inspection.plaque_not_registered")
                    .withStyle(ChatFormatting.RED);
        };
    }

    private static Component removalReport(final ResidenceMarkerService.RemovalResult result) {
        return switch (result) {
            case REMOVED -> Component.translatable("message.coloniesunbound.marker.committed_removed")
                    .withStyle(ChatFormatting.GREEN);
            case NO_MARK -> Component.translatable("message.coloniesunbound.marker.no_committed_mark")
                    .withStyle(ChatFormatting.RED);
            case OUTSIDE_COLONY -> Component.translatable("message.coloniesunbound.inspection.outside_colony")
                    .withStyle(ChatFormatting.RED);
            case NO_PERMISSION -> Component.translatable("message.coloniesunbound.inspection.no_permission")
                    .withStyle(ChatFormatting.RED);
        };
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
