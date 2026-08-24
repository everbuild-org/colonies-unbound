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

        final MarkerType plaqueType = plaque instanceof SurvivalCookTileEntity
                ? MarkerType.RESTAURANT
                : MarkerType.RESIDENCE;
        renderPanel(event.getGuiGraphics(), minecraft.font, PlaqueHudModel.from(plaqueType, plaque.committedMark()));
    }

    private static void renderPanel(
            final GuiGraphics graphics,
            final Font font,
            final PlaqueHudModel model) {
        final int requirementRows = Math.max(1, model.requirements().size());
        final int panelHeight = HEADER_HEIGHT + 39 + requirementRows * ROW_HEIGHT + 17;
        final int x = Math.max(8, graphics.guiWidth() - PANEL_WIDTH - 12);
        final int y = Mth.clamp((graphics.guiHeight() - panelHeight) / 2, 8, Math.max(8, graphics.guiHeight() - panelHeight - 8));
        final int accent = model.type() == MarkerType.RESTAURANT ? 0xFFB460D2 : 0xFF4E83EE;

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
                ? 0.0F
                : (float) model.satisfiedRequiredCount() / model.requiredCount();
        graphics.fill(barX, barY, barX + Mth.floor(barWidth * progress), barY + 3, stateColor);
    }

    private static Component title(final MarkerType type) {
        return Component.translatable(type == MarkerType.RESTAURANT
                ? "hud.coloniesunbound.plaque.dining_hall"
                : "hud.coloniesunbound.plaque.residence");
    }

    private static Component stateLabel(final PlaqueHudModel.State state) {
        return Component.translatable(switch (state) {
            case ACTIVE -> "hud.coloniesunbound.plaque.active";
            case DRAFT -> "hud.coloniesunbound.plaque.draft";
            case UNCONFIGURED -> "hud.coloniesunbound.plaque.unconfigured";
        });
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
