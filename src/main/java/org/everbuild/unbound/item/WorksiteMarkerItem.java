package org.everbuild.unbound.item;

import java.util.List;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.everbuild.unbound.marker.AreaBounds;
import org.everbuild.unbound.marker.MarkerSelection;
import org.everbuild.unbound.marker.MarkerToolMode;
import org.everbuild.unbound.minecolonies.SurvivalResidenceTileEntity;
import org.everbuild.unbound.minecolonies.SurvivalCookTileEntity;
import org.everbuild.unbound.residence.ResidenceInspection;
import org.everbuild.unbound.residence.ResidenceInspector;
import org.everbuild.unbound.residence.ResidenceMarkerService;
import org.everbuild.unbound.workplace.CookInspection;
import org.everbuild.unbound.workplace.CookInspector;
import org.everbuild.unbound.workplace.SurvivalCookMarkerService;

/** Selects residence volumes and edits typed semantic points with one marker tool. */
public final class WorksiteMarkerItem extends Item {
    public static final int MAXIMUM_AXIS_LENGTH = 128;

    public WorksiteMarkerItem(final Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(
            final Level level,
            final Player player,
            final InteractionHand usedHand) {
        final ItemStack stack = player.getItemInHand(usedHand);
        if (!level.isClientSide) {
            final MarkerToolMode nextMode = MarkerToolMode.read(stack).next();
            MarkerToolMode.write(stack, nextMode);
            MarkerSelection.clear(stack);
            player.displayClientMessage(
                    Component.translatable(
                            "message.coloniesunbound.marker.mode_changed",
                            Component.translatable(modeTranslationKey(nextMode))),
                    true);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public InteractionResult useOn(final UseOnContext context) {
        final ItemStack stack = context.getItemInHand();
        if (context.getPlayer() == null) {
            return InteractionResult.PASS;
        }
        final MarkerToolMode mode = MarkerToolMode.read(stack);
        if (!mode.isAreaMode()) {
            if (!context.getLevel().isClientSide
                    && context.getLevel() instanceof ServerLevel serverLevel
                    && context.getPlayer() instanceof ServerPlayer serverPlayer) {
                ResidenceMarkerService.PointEditOutcome outcome = SurvivalCookMarkerService.editPoint(
                        serverLevel,
                        serverPlayer,
                        context.getClickedPos(),
                        mode.poiType(),
                        context.getPlayer().isShiftKeyDown());
                if (outcome.result() == ResidenceMarkerService.PointEditResult.NO_COMMITTED_VOLUME) {
                    outcome = ResidenceMarkerService.editPoint(
                            serverLevel,
                            serverPlayer,
                            context.getClickedPos(),
                            mode.poiType(),
                            context.getPlayer().isShiftKeyDown());
                }
                context.getPlayer().displayClientMessage(pointEditReport(outcome, mode), true);
            }
            return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
        }
        if (context.getPlayer().isShiftKeyDown()) {
            if (!context.getLevel().isClientSide) {
                if (context.getLevel() instanceof ServerLevel serverLevel
                        && context.getPlayer() instanceof ServerPlayer serverPlayer
                        && context.getLevel().getBlockEntity(context.getClickedPos())
                                instanceof SurvivalCookTileEntity cookTile
                        && cookTile.committedMark() != null) {
                    context.getPlayer().displayClientMessage(
                            removalReport(SurvivalCookMarkerService.removeCommittedMark(
                                    serverLevel, serverPlayer, cookTile)),
                            true);
                    return InteractionResult.SUCCESS;
                }
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
        final CookInspection cookInspection = CookInspector.inspect(context.getLevel(), bounds);
        if (!cookInspection.plaquePositions().isEmpty()) {
            final Component report = !inspection.plaquePositions().isEmpty()
                            || cookInspection.status() == CookInspection.Status.MULTIPLE_PLAQUES
                    ? Component.translatable("message.coloniesunbound.inspection.multiple_building_plaques")
                            .withStyle(ChatFormatting.RED)
                    : cookInspection.status() == CookInspection.Status.VALID
                            ? cookRegistrationReport(context, stack, completed, bounds, cookInspection)
                            : Component.translatable("message.coloniesunbound.inspection.no_plaque")
                                    .withStyle(ChatFormatting.RED);
            context.getPlayer().displayClientMessage(report, true);
            return;
        }
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

    private static Component cookRegistrationReport(
            final UseOnContext context,
            final ItemStack stack,
            final MarkerSelection selection,
            final AreaBounds bounds,
            final CookInspection inspection) {
        if (!(context.getLevel() instanceof ServerLevel serverLevel)
                || !(context.getPlayer() instanceof ServerPlayer serverPlayer)) {
            return Component.empty();
        }
        final SurvivalCookMarkerService.RegistrationResult result = SurvivalCookMarkerService.register(
                serverLevel,
                serverPlayer,
                selection,
                bounds,
                inspection.plaquePositions().getFirst(),
                inspection.furnacePositions());
        if (result == SurvivalCookMarkerService.RegistrationResult.SAVED) {
            MarkerSelection.clear(stack);
        }
        return switch (result) {
            case SAVED -> Component.translatable(
                            "message.coloniesunbound.cook.saved",
                            bounds.sizeX(), bounds.sizeY(), bounds.sizeZ())
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

    private static Component pointEditReport(
            final ResidenceMarkerService.PointEditOutcome outcome,
            final MarkerToolMode mode) {
        final Component type = Component.translatable(modeTranslationKey(mode));
        final MutableComponent report = switch (outcome.result()) {
            case ADDED -> Component.translatable("message.coloniesunbound.marker.point_added", type)
                    .withStyle(ChatFormatting.GREEN);
            case REMOVED -> Component.translatable("message.coloniesunbound.marker.point_removed", type)
                    .withStyle(ChatFormatting.GREEN);
            case ALREADY_PRESENT -> Component.translatable("message.coloniesunbound.marker.point_exists", type)
                    .withStyle(ChatFormatting.YELLOW);
            case POINT_NOT_FOUND -> Component.translatable("message.coloniesunbound.marker.point_not_found", type)
                    .withStyle(ChatFormatting.RED);
            case NO_COMMITTED_VOLUME -> Component.translatable("message.coloniesunbound.marker.no_volume")
                    .withStyle(ChatFormatting.RED);
            case AMBIGUOUS_VOLUME -> Component.translatable("message.coloniesunbound.marker.ambiguous_volume")
                    .withStyle(ChatFormatting.RED);
            case NO_PERMISSION -> Component.translatable("message.coloniesunbound.inspection.no_permission")
                    .withStyle(ChatFormatting.RED);
            case MARK_UNAVAILABLE -> Component.translatable("message.coloniesunbound.marker.mark_unavailable")
                    .withStyle(ChatFormatting.RED);
            case INVALID_STORAGE_TARGET -> Component.translatable("message.coloniesunbound.marker.invalid_storage")
                    .withStyle(ChatFormatting.RED);
            case INVALID_WORKSITE_TARGET -> Component.translatable("message.coloniesunbound.marker.invalid_worksite")
                    .withStyle(ChatFormatting.RED);
            case INVALID_ENTRANCE_TARGET -> Component.translatable("message.coloniesunbound.marker.invalid_entrance")
                    .withStyle(ChatFormatting.RED);
            case INVALID_INTERACTION_TARGET -> Component.translatable("message.coloniesunbound.marker.invalid_interaction")
                    .withStyle(ChatFormatting.RED);
            case SCANNER_OWNED -> Component.translatable("message.coloniesunbound.marker.scanner_owned")
                    .withStyle(ChatFormatting.RED);
        };
        if (outcome.summary() == null) {
            return report;
        }
        final String summaryKey = outcome.summary().isReady()
                ? "message.coloniesunbound.workplace.ready"
                : "message.coloniesunbound.workplace.progress";
        return report.append(" — ").append(Component.translatable(
                summaryKey,
                outcome.summary().storagePoints(),
                outcome.summary().worksitePoints(),
                outcome.summary().entrancePoints()));
    }

    private static String modeTranslationKey(final MarkerToolMode mode) {
        return "mode.coloniesunbound.marker." + mode.id();
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
        tooltip.add(Component.translatable(
                        "tooltip.coloniesunbound.worksite_marker.mode",
                        Component.translatable(modeTranslationKey(MarkerToolMode.read(stack))))
                .withStyle(ChatFormatting.AQUA));

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
