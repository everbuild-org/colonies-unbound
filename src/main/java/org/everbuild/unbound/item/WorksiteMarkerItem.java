package org.everbuild.unbound.item;

import java.util.List;
import java.util.UUID;
import org.everbuild.unbound.animal.AnimalPenInspection;
import org.everbuild.unbound.animal.AnimalPenInspector;
import org.everbuild.unbound.animal.SurvivalAnimalPenMarkerService;
import org.everbuild.unbound.animal.ApiaryInspection;
import org.everbuild.unbound.animal.ApiaryInspector;
import org.everbuild.unbound.animal.SurvivalApiaryMarkerService;
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
import net.minecraft.world.level.block.Block;
import org.everbuild.unbound.marker.AreaBounds;
import org.everbuild.unbound.marker.MarkerSelection;
import org.everbuild.unbound.marker.MarkerType;
import org.everbuild.unbound.marker.PatrolRouteSelection;
import org.everbuild.unbound.marker.MarkerToolMode;
import org.everbuild.unbound.minecolonies.SurvivalResidenceTileEntity;
import org.everbuild.unbound.minecolonies.SurvivalGuardTileEntity;
import org.everbuild.unbound.guard.GuardInspection;
import org.everbuild.unbound.guard.GuardInspector;
import org.everbuild.unbound.guard.SurvivalGuardMarkerService;
import org.everbuild.unbound.minecolonies.SurvivalCookTileEntity;
import org.everbuild.unbound.minecolonies.MarkedBuildingTileEntity;
import org.everbuild.unbound.minecolonies.MineColoniesIntegration;
import org.everbuild.unbound.minecolonies.SurvivalAnimalPenBuilding;
import org.everbuild.unbound.minecolonies.SurvivalCowPenBuilding;
import org.everbuild.unbound.minecolonies.SurvivalCowPenTileEntity;
import org.everbuild.unbound.minecolonies.SurvivalChickenPenBuilding;
import org.everbuild.unbound.minecolonies.SurvivalChickenPenTileEntity;
import org.everbuild.unbound.minecolonies.SurvivalPigPenBuilding;
import org.everbuild.unbound.minecolonies.SurvivalPigPenTileEntity;
import org.everbuild.unbound.minecolonies.SurvivalRabbitHutchBuilding;
import org.everbuild.unbound.minecolonies.SurvivalRabbitHutchTileEntity;
import org.everbuild.unbound.minecolonies.SurvivalSheepPenBuilding;
import org.everbuild.unbound.minecolonies.SurvivalSheepPenTileEntity;
import org.everbuild.unbound.minecolonies.SurvivalStableBuilding;
import org.everbuild.unbound.minecolonies.SurvivalStableTileEntity;
import org.everbuild.unbound.minecolonies.SurvivalApiaryTileEntity;
import org.everbuild.unbound.minecolonies.SurvivalCraftingTileEntity;
import org.everbuild.unbound.residence.ResidenceInspection;
import org.everbuild.unbound.residence.ResidenceInspector;
import org.everbuild.unbound.residence.ResidenceMarkerService;
import org.everbuild.unbound.workplace.CookInspection;
import org.everbuild.unbound.workplace.CookInspector;
import org.everbuild.unbound.workplace.SurvivalCookMarkerService;
import org.everbuild.unbound.workplace.CraftingWorkplaceInspection;
import org.everbuild.unbound.workplace.CraftingWorkplaceInspector;
import org.everbuild.unbound.workplace.SurvivalCraftingMarkerService;

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
            PatrolRouteSelection.clear(stack);
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
        if (mode == MarkerToolMode.PATROL_ROUTE) {
            return usePatrolMode(context, stack);
        }
        if (!mode.isAreaMode()) {
            if (!context.getLevel().isClientSide
                    && context.getLevel() instanceof ServerLevel serverLevel
                    && context.getPlayer() instanceof ServerPlayer serverPlayer) {
                ResidenceMarkerService.PointEditOutcome outcome = SurvivalCraftingMarkerService.editPoint(
                        serverLevel,
                        serverPlayer,
                        context.getClickedPos(),
                        mode.poiType(),
                        context.getPlayer().isShiftKeyDown());
                if (outcome.result() == ResidenceMarkerService.PointEditResult.NO_COMMITTED_VOLUME) {
                    outcome = SurvivalApiaryMarkerService.editPoint(
                            serverLevel, serverPlayer, context.getClickedPos(), mode.poiType(),
                            context.getPlayer().isShiftKeyDown());
                }
                if (outcome.result() == ResidenceMarkerService.PointEditResult.NO_COMMITTED_VOLUME) {
                    outcome = SurvivalAnimalPenMarkerService.editPoint(
                            serverLevel,
                            serverPlayer,
                            context.getClickedPos(),
                            mode.poiType(),
                            context.getPlayer().isShiftKeyDown());
                }
                if (outcome.result() == ResidenceMarkerService.PointEditResult.NO_COMMITTED_VOLUME) {
                    outcome = SurvivalCookMarkerService.editPoint(
                            serverLevel,
                            serverPlayer,
                            context.getClickedPos(),
                            mode.poiType(),
                            context.getPlayer().isShiftKeyDown());
                }
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
                                instanceof SurvivalCraftingTileEntity craftingTile
                        && craftingTile.committedMark() != null) {
                    context.getPlayer().displayClientMessage(
                            removalReport(SurvivalCraftingMarkerService.removeCommittedMark(
                                    serverLevel, serverPlayer, craftingTile)), true);
                    return InteractionResult.SUCCESS;
                }
                if (context.getLevel() instanceof ServerLevel serverLevel
                        && context.getPlayer() instanceof ServerPlayer serverPlayer
                        && context.getLevel().getBlockEntity(context.getClickedPos())
                                instanceof SurvivalApiaryTileEntity apiaryTile
                        && apiaryTile.committedMark() != null) {
                    context.getPlayer().displayClientMessage(
                            removalReport(SurvivalApiaryMarkerService.removeCommittedMark(
                                    serverLevel, serverPlayer, apiaryTile)),
                            true);
                    return InteractionResult.SUCCESS;
                }
                if (context.getLevel() instanceof ServerLevel serverLevel
                        && context.getPlayer() instanceof ServerPlayer serverPlayer
                        && context.getLevel().getBlockEntity(context.getClickedPos())
                                instanceof MarkedBuildingTileEntity animalPenTile
                        && animalPenTile.committedMark() != null
                        && animalPenTile.getBuilding() instanceof SurvivalAnimalPenBuilding) {
                    context.getPlayer().displayClientMessage(
                            removalReport(SurvivalAnimalPenMarkerService.removeCommittedMark(
                                    serverLevel, serverPlayer, animalPenTile)),
                            true);
                    return InteractionResult.SUCCESS;
                }
                if (context.getLevel() instanceof ServerLevel serverLevel
                        && context.getPlayer() instanceof ServerPlayer serverPlayer
                        && context.getLevel().getBlockEntity(context.getClickedPos())
                                instanceof SurvivalGuardTileEntity guardTile
                        && guardTile.committedMark() != null) {
                    context.getPlayer().displayClientMessage(
                            removalReport(SurvivalGuardMarkerService.removeCommittedMark(
                                    serverLevel, serverPlayer, guardTile)),
                            true);
                    return InteractionResult.SUCCESS;
                }
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
        final GuardInspection guardInspection = GuardInspector.inspect(context.getLevel(), bounds);
        final ApiaryInspection apiaryInspection = ApiaryInspector.inspect(context.getLevel(), bounds);
        final CraftingWorkplaceInspection craftingInspection =
                CraftingWorkplaceInspector.inspect(context.getLevel(), bounds);
        final List<AnimalPenDefinition> penDefinitions = animalPenDefinitions();
        final AnimalPenInspection animalPenInspection = AnimalPenInspector.inspect(
                context.getLevel(), bounds, penDefinitions.stream().map(AnimalPenDefinition::block).toList());
        if (!craftingInspection.plaquePositions().isEmpty()) {
            final boolean mixed = !inspection.plaquePositions().isEmpty()
                    || !cookInspection.plaquePositions().isEmpty()
                    || !guardInspection.plaquePositions().isEmpty()
                    || !apiaryInspection.plaquePositions().isEmpty()
                    || !animalPenInspection.plaquePositions().isEmpty()
                    || craftingInspection.status() == CraftingWorkplaceInspection.Status.MULTIPLE_PLAQUES;
            final Component report = mixed
                    ? Component.translatable("message.coloniesunbound.inspection.multiple_building_plaques")
                            .withStyle(ChatFormatting.RED)
                    : switch (craftingInspection.status()) {
                        case VALID -> craftingRegistrationReport(context, stack, completed, craftingInspection);
                        case NO_WORKSTATION -> Component.translatable(
                                        "message.coloniesunbound.crafting.no_workstation",
                                        Component.translatable("hud.coloniesunbound.plaque."
                                                + craftingInspection.definition().id()))
                                .withStyle(ChatFormatting.RED);
                        default -> Component.translatable("message.coloniesunbound.inspection.no_plaque")
                                .withStyle(ChatFormatting.RED);
                    };
            context.getPlayer().displayClientMessage(report, true);
            return;
        }
        if (!animalPenInspection.plaquePositions().isEmpty()) {
            final Component report = !inspection.plaquePositions().isEmpty()
                            || !cookInspection.plaquePositions().isEmpty()
                            || !guardInspection.plaquePositions().isEmpty()
                            || !apiaryInspection.plaquePositions().isEmpty()
                            || !craftingInspection.plaquePositions().isEmpty()
                            || animalPenInspection.status() == AnimalPenInspection.Status.MULTIPLE_PLAQUES
                    ? Component.translatable("message.coloniesunbound.inspection.multiple_building_plaques")
                            .withStyle(ChatFormatting.RED)
                    : switch (animalPenInspection.status()) {
                        case VALID -> {
                            final Block plaqueBlock = context.getLevel()
                                    .getBlockState(animalPenInspection.plaquePositions().getFirst())
                                    .getBlock();
                            final AnimalPenDefinition definition = penDefinitions.stream()
                                    .filter(candidate -> candidate.block() == plaqueBlock)
                                    .findFirst()
                                    .orElseThrow();
                            yield animalPenRegistrationReport(
                                    context,
                                    stack,
                                    completed,
                                    animalPenInspection,
                                    definition.markerType(),
                                    definition.buildingType(),
                                    definition.tileType(),
                                    definition.savedMessageKey());
                        }
                        case NO_GATE -> Component.translatable("message.coloniesunbound.cow_pen.no_gate")
                                .withStyle(ChatFormatting.RED);
                        case NO_PASTURE -> Component.translatable("message.coloniesunbound.cow_pen.no_pasture")
                                .withStyle(ChatFormatting.RED);
                        default -> Component.translatable("message.coloniesunbound.inspection.no_plaque")
                                .withStyle(ChatFormatting.RED);
                    };
            context.getPlayer().displayClientMessage(report, true);
            return;
        }
        if (!apiaryInspection.plaquePositions().isEmpty()) {
            final Component report = !inspection.plaquePositions().isEmpty()
                            || !cookInspection.plaquePositions().isEmpty()
                            || !guardInspection.plaquePositions().isEmpty()
                            || !animalPenInspection.plaquePositions().isEmpty()
                            || !craftingInspection.plaquePositions().isEmpty()
                            || apiaryInspection.status() == ApiaryInspection.Status.MULTIPLE_PLAQUES
                    ? Component.translatable("message.coloniesunbound.inspection.multiple_building_plaques")
                            .withStyle(ChatFormatting.RED)
                    : switch (apiaryInspection.status()) {
                        case VALID -> apiaryRegistrationReport(context, stack, completed, apiaryInspection);
                        case NO_HIVES -> Component.translatable("message.coloniesunbound.apiary.no_hives")
                                .withStyle(ChatFormatting.RED);
                        default -> Component.translatable("message.coloniesunbound.inspection.no_plaque")
                                .withStyle(ChatFormatting.RED);
                    };
            context.getPlayer().displayClientMessage(report, true);
            return;
        }
        if (!guardInspection.plaquePositions().isEmpty()) {
            final Component report = !inspection.plaquePositions().isEmpty()
                            || !cookInspection.plaquePositions().isEmpty()
                            || !animalPenInspection.plaquePositions().isEmpty()
                            || !apiaryInspection.plaquePositions().isEmpty()
                            || !craftingInspection.plaquePositions().isEmpty()
                            || guardInspection.status() == GuardInspection.Status.MULTIPLE_PLAQUES
                    ? Component.translatable("message.coloniesunbound.inspection.multiple_building_plaques")
                            .withStyle(ChatFormatting.RED)
                    : guardInspection.status() == GuardInspection.Status.VALID
                            ? guardRegistrationReport(context, stack, completed, bounds, guardInspection)
                            : Component.translatable("message.coloniesunbound.inspection.no_plaque")
                                    .withStyle(ChatFormatting.RED);
            context.getPlayer().displayClientMessage(report, true);
            return;
        }
        if (!cookInspection.plaquePositions().isEmpty()) {
            final Component report = !inspection.plaquePositions().isEmpty()
                            || !guardInspection.plaquePositions().isEmpty()
                            || !animalPenInspection.plaquePositions().isEmpty()
                            || !apiaryInspection.plaquePositions().isEmpty()
                            || !craftingInspection.plaquePositions().isEmpty()
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

    private static InteractionResult usePatrolMode(
            final UseOnContext context,
            final ItemStack stack) {
        if (context.getLevel().isClientSide
                || !(context.getLevel() instanceof ServerLevel serverLevel)
                || !(context.getPlayer() instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
        }
        if (context.getLevel().getBlockEntity(context.getClickedPos())
                instanceof SurvivalGuardTileEntity guardTile) {
            final SurvivalGuardMarkerService.RouteEditResult result =
                    SurvivalGuardMarkerService.selectRouteOwner(serverLevel, serverPlayer, guardTile);
            if (result == SurvivalGuardMarkerService.RouteEditResult.OWNER_SELECTED) {
                PatrolRouteSelection.write(stack, new PatrolRouteSelection(
                        serverLevel.dimension().location(), guardTile.getBlockPos()));
            }
            serverPlayer.displayClientMessage(patrolReport(result, 0), true);
            return InteractionResult.SUCCESS;
        }

        final PatrolRouteSelection selection = PatrolRouteSelection.read(stack);
        if (selection == null) {
            serverPlayer.displayClientMessage(
                    Component.translatable("message.coloniesunbound.patrol.select_owner")
                            .withStyle(ChatFormatting.RED),
                    true);
            return InteractionResult.SUCCESS;
        }
        final SurvivalGuardMarkerService.RouteEditOutcome outcome =
                SurvivalGuardMarkerService.editPatrolNode(
                        serverLevel,
                        serverPlayer,
                        selection,
                        context.getClickedPos(),
                        context.getPlayer().isShiftKeyDown());
        serverPlayer.displayClientMessage(patrolReport(outcome.result(), outcome.nodeCount()), true);
        return InteractionResult.SUCCESS;
    }

    private static Component patrolReport(
            final SurvivalGuardMarkerService.RouteEditResult result,
            final int nodeCount) {
        return switch (result) {
            case OWNER_SELECTED -> Component.translatable("message.coloniesunbound.patrol.owner_selected")
                    .withStyle(ChatFormatting.GREEN);
            case NODE_ADDED -> Component.translatable("message.coloniesunbound.patrol.node_added", nodeCount)
                    .withStyle(ChatFormatting.GREEN);
            case NODE_REMOVED -> Component.translatable("message.coloniesunbound.patrol.node_removed", nodeCount)
                    .withStyle(ChatFormatting.GREEN);
            case NODE_EXISTS -> Component.translatable("message.coloniesunbound.patrol.node_exists")
                    .withStyle(ChatFormatting.YELLOW);
            case NODE_NOT_FOUND -> Component.translatable("message.coloniesunbound.patrol.node_not_found")
                    .withStyle(ChatFormatting.RED);
            case TOO_MANY_NODES -> Component.translatable(
                            "message.coloniesunbound.patrol.too_many", SurvivalGuardMarkerService.MAXIMUM_PATROL_NODES)
                    .withStyle(ChatFormatting.RED);
            case TOO_FAR -> Component.translatable("message.coloniesunbound.patrol.too_far")
                    .withStyle(ChatFormatting.RED);
            case WRONG_DIMENSION -> Component.translatable("message.coloniesunbound.patrol.wrong_dimension")
                    .withStyle(ChatFormatting.RED);
            case OUTSIDE_COLONY -> Component.translatable("message.coloniesunbound.patrol.outside_colony")
                    .withStyle(ChatFormatting.RED);
            case NO_PERMISSION -> Component.translatable("message.coloniesunbound.inspection.no_permission")
                    .withStyle(ChatFormatting.RED);
            case MARK_UNAVAILABLE -> Component.translatable("message.coloniesunbound.marker.mark_unavailable")
                    .withStyle(ChatFormatting.RED);
        };
    }

    private static Component guardRegistrationReport(
            final UseOnContext context,
            final ItemStack stack,
            final MarkerSelection selection,
            final AreaBounds bounds,
            final GuardInspection inspection) {
        if (!(context.getLevel() instanceof ServerLevel serverLevel)
                || !(context.getPlayer() instanceof ServerPlayer serverPlayer)) {
            return Component.empty();
        }
        final SurvivalGuardMarkerService.RegistrationResult result = SurvivalGuardMarkerService.register(
                serverLevel,
                serverPlayer,
                selection,
                bounds,
                inspection.plaquePositions().getFirst());
        if (result == SurvivalGuardMarkerService.RegistrationResult.SAVED) {
            MarkerSelection.clear(stack);
        }
        return switch (result) {
            case SAVED -> Component.translatable(
                            "message.coloniesunbound.guard.saved",
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

    private static Component animalPenRegistrationReport(
            final UseOnContext context,
            final ItemStack stack,
            final MarkerSelection selection,
            final AnimalPenInspection inspection,
            final MarkerType markerType,
            final Class<? extends SurvivalAnimalPenBuilding> buildingType,
            final Class<? extends MarkedBuildingTileEntity> tileType,
            final String savedMessageKey) {
        if (!(context.getLevel() instanceof ServerLevel serverLevel)
                || !(context.getPlayer() instanceof ServerPlayer serverPlayer)) {
            return Component.empty();
        }
        final SurvivalAnimalPenMarkerService.RegistrationResult result = SurvivalAnimalPenMarkerService.register(
                serverLevel, serverPlayer, selection, inspection, markerType, buildingType, tileType);
        if (result == SurvivalAnimalPenMarkerService.RegistrationResult.SAVED) {
            MarkerSelection.clear(stack);
        }
        return switch (result) {
            case SAVED -> Component.translatable(
                            savedMessageKey,
                            inspection.bounds().sizeX(),
                            inspection.bounds().sizeY(),
                            inspection.bounds().sizeZ(),
                            inspection.gatePositions().size())
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

    private static Component craftingRegistrationReport(
            final UseOnContext context,
            final ItemStack stack,
            final MarkerSelection selection,
            final CraftingWorkplaceInspection inspection) {
        if (!(context.getLevel() instanceof ServerLevel serverLevel)
                || !(context.getPlayer() instanceof ServerPlayer serverPlayer)) {
            return Component.empty();
        }
        final SurvivalCraftingMarkerService.RegistrationResult result = SurvivalCraftingMarkerService.register(
                serverLevel, serverPlayer, selection, inspection);
        if (result == SurvivalCraftingMarkerService.RegistrationResult.SAVED) MarkerSelection.clear(stack);
        return switch (result) {
            case SAVED -> Component.translatable(
                            "message.coloniesunbound.crafting.saved",
                            Component.translatable("hud.coloniesunbound.plaque." + inspection.definition().id()),
                            inspection.bounds().sizeX(), inspection.bounds().sizeY(), inspection.bounds().sizeZ(),
                            inspection.workstationPositions().size())
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

    private static Component apiaryRegistrationReport(
            final UseOnContext context,
            final ItemStack stack,
            final MarkerSelection selection,
            final ApiaryInspection inspection) {
        if (!(context.getLevel() instanceof ServerLevel serverLevel)
                || !(context.getPlayer() instanceof ServerPlayer serverPlayer)) {
            return Component.empty();
        }
        final SurvivalApiaryMarkerService.RegistrationResult result = SurvivalApiaryMarkerService.register(
                serverLevel, serverPlayer, selection, inspection);
        if (result == SurvivalApiaryMarkerService.RegistrationResult.SAVED) {
            MarkerSelection.clear(stack);
        }
        return switch (result) {
            case SAVED -> Component.translatable(
                            "message.coloniesunbound.apiary.saved",
                            inspection.bounds().sizeX(), inspection.bounds().sizeY(),
                            inspection.bounds().sizeZ(), inspection.hivePositions().size())
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

    private static List<AnimalPenDefinition> animalPenDefinitions() {
        return List.of(
                new AnimalPenDefinition(
                        MineColoniesIntegration.SURVIVAL_COW_PEN_BLOCK.get(),
                        MarkerType.ANIMAL_PEN,
                        SurvivalCowPenBuilding.class,
                        SurvivalCowPenTileEntity.class,
                        "message.coloniesunbound.cow_pen.saved"),
                new AnimalPenDefinition(
                        MineColoniesIntegration.SURVIVAL_SHEEP_PEN_BLOCK.get(),
                        MarkerType.SHEEP_PEN,
                        SurvivalSheepPenBuilding.class,
                        SurvivalSheepPenTileEntity.class,
                        "message.coloniesunbound.sheep_pen.saved"),
                new AnimalPenDefinition(
                        MineColoniesIntegration.SURVIVAL_CHICKEN_PEN_BLOCK.get(),
                        MarkerType.CHICKEN_PEN,
                        SurvivalChickenPenBuilding.class,
                        SurvivalChickenPenTileEntity.class,
                        "message.coloniesunbound.chicken_pen.saved"),
                new AnimalPenDefinition(
                        MineColoniesIntegration.SURVIVAL_PIG_PEN_BLOCK.get(),
                        MarkerType.PIG_PEN,
                        SurvivalPigPenBuilding.class,
                        SurvivalPigPenTileEntity.class,
                        "message.coloniesunbound.pig_pen.saved"),
                new AnimalPenDefinition(
                        MineColoniesIntegration.SURVIVAL_RABBIT_HUTCH_BLOCK.get(),
                        MarkerType.RABBIT_HUTCH,
                        SurvivalRabbitHutchBuilding.class,
                        SurvivalRabbitHutchTileEntity.class,
                        "message.coloniesunbound.rabbit_hutch.saved"),
                new AnimalPenDefinition(
                        MineColoniesIntegration.SURVIVAL_STABLE_BLOCK.get(),
                        MarkerType.STABLE,
                        SurvivalStableBuilding.class,
                        SurvivalStableTileEntity.class,
                        "message.coloniesunbound.stable.saved"));
    }

    private record AnimalPenDefinition(
            Block block,
            MarkerType markerType,
            Class<? extends SurvivalAnimalPenBuilding> buildingType,
            Class<? extends MarkedBuildingTileEntity> tileType,
            String savedMessageKey) {
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
