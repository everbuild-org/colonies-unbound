package org.everbuild.unbound.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import org.everbuild.unbound.ColoniesUnbound;
import org.everbuild.unbound.marker.MarkerType;
import org.everbuild.unbound.minecolonies.MarkedBuildingTileEntity;
import org.everbuild.unbound.minecolonies.SurvivalCookTileEntity;
import org.everbuild.unbound.minecolonies.SurvivalCowPenTileEntity;
import org.everbuild.unbound.minecolonies.SurvivalSheepPenTileEntity;
import org.everbuild.unbound.minecolonies.SurvivalChickenPenTileEntity;
import org.everbuild.unbound.minecolonies.SurvivalPigPenTileEntity;
import org.everbuild.unbound.minecolonies.SurvivalRabbitHutchTileEntity;
import org.everbuild.unbound.minecolonies.SurvivalStableTileEntity;
import org.everbuild.unbound.minecolonies.SurvivalApiaryTileEntity;
import org.everbuild.unbound.minecolonies.SurvivalCraftingTileEntity;
import org.everbuild.unbound.minecolonies.SurvivalGuardTileEntity;
import org.everbuild.unbound.workplace.CraftingWorkplaceDefinition;

/** Compact contextual inspector shown while the crosshair rests on a survival plaque. */
@EventBusSubscriber(modid = ColoniesUnbound.MOD_ID, value = Dist.CLIENT)
public final class PlaqueInspectorHud {
    private static final int PANEL_WIDTH = 196;
    private static final int PADDING = 9;
    private static final int HEADER_HEIGHT = 25;
    private static final int ROW_HEIGHT = 12;

    private static final int PANEL = 0xE5121720;
    private static final int PANEL_LIGHT = 0xE51A2230;
    private static final int SHADOW = 0x80000000;
    private static final int TEXT = 0xFFE8EDF5;
    private static final int MUTED = 0xFF99A4B3;
    private static final int SUCCESS = 0xFF65D68A;
    private static final int WARNING = 0xFFFFC35A;
    private static final int INACTIVE = 0xFF8C96A5;

    private PlaqueInspectorHud() {
    }

    @SubscribeEvent
    public static void render(final RenderGuiEvent.Post event) {
        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null
                || minecraft.level == null
                || minecraft.screen != null
                || minecraft.options.hideGui
                || !(minecraft.hitResult instanceof BlockHitResult hit)
                || !(minecraft.level.getBlockEntity(hit.getBlockPos()) instanceof MarkedBuildingTileEntity plaque)) {
            return;
        }

        final MarkerType plaqueType = plaque instanceof SurvivalCraftingTileEntity crafting
                ? crafting.committedMark() != null
                        ? crafting.committedMark().type()
                        : CraftingWorkplaceDefinition.all().stream()
                                .filter(definition -> minecraft.level.getBlockState(hit.getBlockPos()).is(definition.plaque().get()))
                                .map(CraftingWorkplaceDefinition::markerType)
                                .findFirst().orElse(MarkerType.RESIDENCE)
                : plaque instanceof SurvivalCookTileEntity
                ? MarkerType.RESTAURANT
                : plaque instanceof SurvivalGuardTileEntity
                        ? MarkerType.GUARD
                        : plaque instanceof SurvivalCowPenTileEntity
                                ? MarkerType.ANIMAL_PEN
                                : plaque instanceof SurvivalSheepPenTileEntity
                                        ? MarkerType.SHEEP_PEN
                                        : plaque instanceof SurvivalChickenPenTileEntity
                                                ? MarkerType.CHICKEN_PEN
                                                : plaque instanceof SurvivalPigPenTileEntity
                                                        ? MarkerType.PIG_PEN
                                                        : plaque instanceof SurvivalRabbitHutchTileEntity
                                                                ? MarkerType.RABBIT_HUTCH
                                                                : plaque instanceof SurvivalStableTileEntity
                                                                        ? MarkerType.STABLE
                                                                        : plaque instanceof SurvivalApiaryTileEntity
                                                                                ? MarkerType.APIARY
                                : MarkerType.RESIDENCE;
        renderPanel(event.getGuiGraphics(), minecraft.font, PlaqueHudModel.from(plaqueType, plaque.committedMark()));
    }

    private static void renderPanel(
            final GuiGraphics graphics,
            final Font font,
            final PlaqueHudModel model) {
        final int requirementRows = Math.max(1, model.requirements().size());
        final int panelHeight = HEADER_HEIGHT + 63 + requirementRows * ROW_HEIGHT + 17;
        final int x = Math.max(8, graphics.guiWidth() - PANEL_WIDTH - 12);
        final int y = Mth.clamp((graphics.guiHeight() - panelHeight) / 2, 8, Math.max(8, graphics.guiHeight() - panelHeight - 8));
        final int accent = switch (model.type()) {
            case RESTAURANT -> 0xFFB460D2;
            case GUARD -> 0xFFD24242;
            case ANIMAL_PEN -> 0xFF5BB048;
            case SHEEP_PEN -> 0xFF78B96A;
            case CHICKEN_PEN -> 0xFFE4C95A;
            case PIG_PEN -> 0xFFE58D98;
            case RABBIT_HUTCH -> 0xFFB79578;
            case STABLE -> 0xFF9B6C42;
            case APIARY -> 0xFFE1A72D;
            case BLACKSMITH, CRUSHER -> 0xFF7D8794;
            case SAWMILL, FLETCHER -> 0xFFB17A45;
            case STONEMASON, CONCRETE_MIXER -> 0xFF9B9387;
            case MECHANIC -> 0xFF6F93A8;
            case SIFTER -> 0xFFC5A96A;
            case BAKERY, KITCHEN -> 0xFFD18A45;
            case SMELTERY, STONE_SMELTER -> 0xFF8A5A48;
            case GLASSBLOWER -> 0xFF62B7C2;
            case DYER -> 0xFFB35EAD;
            case ALCHEMIST -> 0xFF7656B8;
            case FARMER, PLANTATION -> 0xFF72A94C;
            case FISHERMAN -> 0xFF4C91B8;
            case LUMBERJACK -> 0xFF497B3F;
            case FLORIST -> 0xFFD16E9B;
            case COMPOSTER -> 0xFF8A704C;
            case HOSPITAL -> 0xFFD85A63;
            case SCHOOL -> 0xFFDAA84A;
            case LIBRARY -> 0xFF8B6948;
            case UNIVERSITY -> 0xFF4C69B8;
            case TAVERN -> 0xFFB66A3C;
            case GRAVEYARD -> 0xFF68717C;
            case ENCHANTER -> 0xFF8754B8;
            case NETHER_WORKER -> 0xFF7440A0;
            case ARCHERY -> 0xFF668B55;
            case COMBAT_ACADEMY -> 0xFF9B4B45;
            case WAREHOUSE, POST_BOX, DELIVERYMAN, STASH -> 0xFFB2864D;
            case BARRACKS, BARRACKS_TOWER, GATE_HOUSE -> 0xFF5F6570;
            case BUILDER -> 0xFFD39A4A;
            case MINER, SIMPLE_QUARRY, MEDIUM_QUARRY -> 0xFF6C7077;
            case TOWN_HALL -> 0xFF4D79A8;
            case MYSTICAL_SITE -> 0xFF7651A8;
            case RESIDENCE -> 0xFF4E83EE;
        };

        graphics.fill(x + 3, y + 3, x + PANEL_WIDTH + 3, y + panelHeight + 3, SHADOW);
        graphics.fill(x, y, x + PANEL_WIDTH, y + panelHeight, accent);
        graphics.fill(x + 2, y + 2, x + PANEL_WIDTH - 2, y + panelHeight - 2, PANEL);
        graphics.fill(x + 2, y + 2, x + PANEL_WIDTH - 2, y + HEADER_HEIGHT, PANEL_LIGHT);
        graphics.fill(x + 2, y + HEADER_HEIGHT, x + 5, y + panelHeight - 2, accent);

        graphics.fill(x + PADDING, y + 8, x + PADDING + 8, y + 16, accent);
        graphics.fill(x + PADDING + 2, y + 6, x + PADDING + 6, y + 18, accent);
        graphics.drawString(font, title(model.type()), x + PADDING + 14, y + 7, TEXT, true);

        final Component state = stateLabel(model.state());
        final int stateColor = stateColor(model.state());
        final int stateWidth = font.width(state);
        graphics.drawString(font, state, x + PANEL_WIDTH - PADDING - stateWidth, y + 7, stateColor, true);

        int rowY = y + HEADER_HEIGHT + 7;
        graphics.drawString(font, Component.translatable("hud.coloniesunbound.plaque.state"), x + PADDING, rowY, MUTED, false);
        graphics.drawString(font, stateDescription(model.state()), x + 58, rowY, TEXT, false);
        rowY += 12;

        graphics.drawString(font, Component.translatable("hud.coloniesunbound.plaque.configured_level"), x + PADDING, rowY, MUTED, false);
        graphics.drawString(font, levelLabel(model.configuredLevel()), x + 82, rowY, TEXT, false);
        rowY += 12;

        graphics.drawString(font, Component.translatable("hud.coloniesunbound.plaque.effective_level"), x + PADDING, rowY, MUTED, false);
        graphics.drawString(font, levelLabel(model.effectiveLevel()), x + 82, rowY,
                model.effectiveLevel() > 0 ? SUCCESS : INACTIVE, false);
        rowY += 12;

        graphics.drawString(font, Component.translatable("hud.coloniesunbound.plaque.volume"), x + PADDING, rowY, MUTED, false);
        final Component volume = model.bounds() == null
                ? Component.translatable("hud.coloniesunbound.plaque.not_set")
                : Component.translatable(
                        "hud.coloniesunbound.plaque.dimensions",
                        model.bounds().sizeX(),
                        model.bounds().sizeY(),
                        model.bounds().sizeZ());
        graphics.drawString(font, volume, x + 58, rowY, TEXT, false);
        rowY += 16;

        graphics.drawString(font, Component.translatable("hud.coloniesunbound.plaque.requirements"), x + PADDING, rowY, MUTED, false);
        if (model.requiredCount() > 0) {
            final String progress = model.satisfiedRequiredCount() + "/" + model.requiredCount();
            graphics.drawString(font, progress, x + PANEL_WIDTH - PADDING - font.width(progress), rowY, MUTED, false);
        }
        rowY += 12;

        for (final PlaqueHudModel.Requirement requirement : model.requirements()) {
            final boolean satisfied = requirement.satisfied();
            final int color = requirement.optional() ? INACTIVE : satisfied ? SUCCESS : WARNING;
            graphics.fill(x + PADDING, rowY + 2, x + PADDING + 5, rowY + 7, color);
            graphics.drawString(font, Component.translatable(requirement.translationKey()), x + PADDING + 10, rowY, TEXT, false);

            final String count = requirement.optional()
                    ? Integer.toString(requirement.count())
                    : requirement.count() + " / " + requirement.minimum();
            graphics.drawString(font, count, x + PANEL_WIDTH - PADDING - font.width(count), rowY, color, false);
            rowY += ROW_HEIGHT;
        }

        final int barX = x + PADDING;
        final int barY = y + panelHeight - 10;
        final int barWidth = PANEL_WIDTH - PADDING * 2;
        graphics.fill(barX, barY, barX + barWidth, barY + 3, 0xFF303A48);
        final float progress = model.requiredCount() == 0
                ? model.state() == PlaqueHudModel.State.ACTIVE ? 1.0F : 0.0F
                : (float) model.satisfiedRequiredCount() / model.requiredCount();
        graphics.fill(barX, barY, barX + Mth.floor(barWidth * progress), barY + 3, stateColor);
    }

    private static Component title(final MarkerType type) {
        return Component.translatable(switch (type) {
            case RESTAURANT -> "hud.coloniesunbound.plaque.dining_hall";
            case GUARD -> "hud.coloniesunbound.plaque.guard_tower";
            case ANIMAL_PEN -> "hud.coloniesunbound.plaque.cow_pen";
            case SHEEP_PEN -> "hud.coloniesunbound.plaque.sheep_pen";
            case CHICKEN_PEN -> "hud.coloniesunbound.plaque.chicken_pen";
            case PIG_PEN -> "hud.coloniesunbound.plaque.pig_pen";
            case RABBIT_HUTCH -> "hud.coloniesunbound.plaque.rabbit_hutch";
            case STABLE -> "hud.coloniesunbound.plaque.stable";
            case APIARY -> "hud.coloniesunbound.plaque.apiary";
            case BLACKSMITH -> "hud.coloniesunbound.plaque.blacksmith";
            case SAWMILL -> "hud.coloniesunbound.plaque.sawmill";
            case STONEMASON -> "hud.coloniesunbound.plaque.stonemason";
            case FLETCHER -> "hud.coloniesunbound.plaque.fletcher";
            case MECHANIC -> "hud.coloniesunbound.plaque.mechanic";
            case CONCRETE_MIXER -> "hud.coloniesunbound.plaque.concrete_mixer";
            case CRUSHER -> "hud.coloniesunbound.plaque.crusher";
            case SIFTER -> "hud.coloniesunbound.plaque.sifter";
            case BAKERY -> "hud.coloniesunbound.plaque.bakery";
            case KITCHEN -> "hud.coloniesunbound.plaque.kitchen";
            case SMELTERY -> "hud.coloniesunbound.plaque.smeltery";
            case STONE_SMELTER -> "hud.coloniesunbound.plaque.stone_smelter";
            case GLASSBLOWER -> "hud.coloniesunbound.plaque.glassblower";
            case DYER -> "hud.coloniesunbound.plaque.dyer";
            case ALCHEMIST -> "hud.coloniesunbound.plaque.alchemist";
            case FARMER -> "hud.coloniesunbound.plaque.farmer";
            case PLANTATION -> "hud.coloniesunbound.plaque.plantation";
            case FISHERMAN -> "hud.coloniesunbound.plaque.fisherman";
            case LUMBERJACK -> "hud.coloniesunbound.plaque.lumberjack";
            case FLORIST -> "hud.coloniesunbound.plaque.florist";
            case COMPOSTER -> "hud.coloniesunbound.plaque.composter";
            case HOSPITAL -> "hud.coloniesunbound.plaque.hospital";
            case SCHOOL -> "hud.coloniesunbound.plaque.school";
            case LIBRARY -> "hud.coloniesunbound.plaque.library";
            case UNIVERSITY -> "hud.coloniesunbound.plaque.university";
            case TAVERN -> "hud.coloniesunbound.plaque.tavern";
            case GRAVEYARD -> "hud.coloniesunbound.plaque.graveyard";
            case ENCHANTER -> "hud.coloniesunbound.plaque.enchanter";
            case NETHER_WORKER -> "hud.coloniesunbound.plaque.nether_worker";
            case ARCHERY -> "hud.coloniesunbound.plaque.archery";
            case COMBAT_ACADEMY -> "hud.coloniesunbound.plaque.combat_academy";
            case WAREHOUSE -> "hud.coloniesunbound.plaque.warehouse";
            case POST_BOX -> "hud.coloniesunbound.plaque.post_box";
            case DELIVERYMAN -> "hud.coloniesunbound.plaque.deliveryman";
            case BARRACKS -> "hud.coloniesunbound.plaque.barracks";
            case BARRACKS_TOWER -> "hud.coloniesunbound.plaque.barracks_tower";
            case GATE_HOUSE -> "hud.coloniesunbound.plaque.gate_house";
            case BUILDER -> "hud.coloniesunbound.plaque.builder";
            case MINER -> "hud.coloniesunbound.plaque.miner";
            case SIMPLE_QUARRY -> "hud.coloniesunbound.plaque.simple_quarry";
            case MEDIUM_QUARRY -> "hud.coloniesunbound.plaque.medium_quarry";
            case TOWN_HALL -> "hud.coloniesunbound.plaque.town_hall";
            case STASH -> "hud.coloniesunbound.plaque.stash";
            case MYSTICAL_SITE -> "hud.coloniesunbound.plaque.mystical_site";
            case RESIDENCE -> "hud.coloniesunbound.plaque.residence";
        });
    }

    private static Component stateLabel(final PlaqueHudModel.State state) {
        return Component.translatable(switch (state) {
            case ACTIVE -> "hud.coloniesunbound.plaque.active";
            case DRAFT -> "hud.coloniesunbound.plaque.draft";
            case UNCONFIGURED -> "hud.coloniesunbound.plaque.unconfigured";
        });
    }

    private static Component levelLabel(final int level) {
        return level == 0
                ? Component.translatable("hud.coloniesunbound.plaque.inactive_level")
                : Component.literal(Integer.toString(level));
    }

    private static Component stateDescription(final PlaqueHudModel.State state) {
        return Component.translatable(switch (state) {
            case ACTIVE -> "hud.coloniesunbound.plaque.operational";
            case DRAFT -> "hud.coloniesunbound.plaque.needs_points";
            case UNCONFIGURED -> "hud.coloniesunbound.plaque.needs_volume";
        });
    }

    private static int stateColor(final PlaqueHudModel.State state) {
        return switch (state) {
            case ACTIVE -> SUCCESS;
            case DRAFT -> WARNING;
            case UNCONFIGURED -> INACTIVE;
        };
    }
}
