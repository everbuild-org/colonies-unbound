package org.everbuild.unbound.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import org.everbuild.unbound.ColoniesUnbound;
import org.everbuild.unbound.marker.CommittedWorksiteMark;
import org.everbuild.unbound.marker.WorksitePoi;
import org.everbuild.unbound.marker.WorksitePoiType;
import org.everbuild.unbound.minecolonies.MarkedBuildingTileEntity;

/** Renders a committed residence volume and its type flag while a marker is held. */
public final class SurvivalResidenceRenderer<T extends MarkedBuildingTileEntity>
        implements BlockEntityRenderer<T> {
    private static final float RED = 1.00F;
    private static final float GREEN = 0.48F;
    private static final float BLUE = 0.08F;
    private static final ResourceLocation FLAG_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            ColoniesUnbound.MOD_ID, "textures/marker/flag_base.png");

    public SurvivalResidenceRenderer(final BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(
            final T tile,
            final float partialTick,
            final PoseStack poseStack,
            final MultiBufferSource buffers,
            final int packedLight,
            final int packedOverlay) {
        final CommittedWorksiteMark mark = tile.committedMark();
        if (mark == null || !MarkerOverlayRenderer.isMarkerHeld(Minecraft.getInstance())) {
            return;
        }

        final VertexConsumer lines = buffers.getBuffer(RenderType.lines());
        final BlockPos origin = tile.getBlockPos();
        poseStack.pushPose();
        poseStack.translate(-origin.getX(), -origin.getY(), -origin.getZ());
        LevelRenderer.renderLineBox(
                poseStack, lines, mark.bounds().asAabb().inflate(0.004), RED, GREEN, BLUE, 0.95F);
        poseStack.popPose();

        renderFlag(
                poseStack, buffers, mark.type().id(), 0.5, 2.0, 0.5, 1.25F);
        final java.util.List<WorksitePoi> patrolNodes = mark.pois().stream()
                .filter(point -> point.type() == WorksitePoiType.PATROL)
                .toList();
        renderPatrolRoute(poseStack, buffers, origin, patrolNodes);
        int patrolIndex = 0;
        for (final WorksitePoi poi : mark.pois()) {
            final double flagX = poi.position().getX() - origin.getX() + 0.5;
            final double flagY = poi.position().getY() - origin.getY() + 1.6;
            final double flagZ = poi.position().getZ() - origin.getZ() + 0.5;
            renderFlag(
                    poseStack,
                    buffers,
                    poi.type().id(),
                    flagX,
                    flagY,
                    flagZ,
                    0.75F);
            if (poi.type() == WorksitePoiType.PATROL) {
                renderRouteNumber(poseStack, buffers, ++patrolIndex, flagX, flagY + 0.34, flagZ);
            }
        }
    }

    private static void renderPatrolRoute(
            final PoseStack poseStack,
            final MultiBufferSource buffers,
            final BlockPos origin,
            final java.util.List<WorksitePoi> nodes) {
        if (nodes.size() < 2) {
            return;
        }
        final VertexConsumer lines = buffers.getBuffer(RenderType.lines());
        for (int index = 1; index < nodes.size(); index++) {
            renderRouteSegment(poseStack, lines, origin, nodes.get(index - 1), nodes.get(index));
        }
        if (nodes.size() > 2) {
            renderRouteSegment(poseStack, lines, origin, nodes.getLast(), nodes.getFirst());
        }
    }

    private static void renderRouteSegment(
            final PoseStack poseStack,
            final VertexConsumer lines,
            final BlockPos origin,
            final WorksitePoi from,
            final WorksitePoi to) {
        final float fromX = from.position().getX() - origin.getX() + 0.5F;
        final float fromY = from.position().getY() - origin.getY() + 1.05F;
        final float fromZ = from.position().getZ() - origin.getZ() + 0.5F;
        final float toX = to.position().getX() - origin.getX() + 0.5F;
        final float toY = to.position().getY() - origin.getY() + 1.05F;
        final float toZ = to.position().getZ() - origin.getZ() + 0.5F;
        final float length = Math.max(0.001F, (float) Math.sqrt(
                (toX - fromX) * (toX - fromX)
                        + (toY - fromY) * (toY - fromY)
                        + (toZ - fromZ) * (toZ - fromZ)));
        final float normalX = (toX - fromX) / length;
        final float normalY = (toY - fromY) / length;
        final float normalZ = (toZ - fromZ) / length;
        final PoseStack.Pose pose = poseStack.last();
        lines.addVertex(pose, fromX, fromY, fromZ)
                .setColor(210, 66, 66, 230)
                .setNormal(pose, normalX, normalY, normalZ);
        lines.addVertex(pose, toX, toY, toZ)
                .setColor(210, 66, 66, 230)
                .setNormal(pose, normalX, normalY, normalZ);
    }

    private static void renderRouteNumber(
            final PoseStack poseStack,
            final MultiBufferSource buffers,
            final int number,
            final double x,
            final double y,
            final double z) {
        final Minecraft minecraft = Minecraft.getInstance();
        final String label = Integer.toString(number);
        poseStack.pushPose();
        poseStack.translate(x, y, z);
        poseStack.mulPose(Axis.YP.rotationDegrees(
                180.0F - minecraft.gameRenderer.getMainCamera().getYRot()));
        poseStack.scale(0.025F, -0.025F, 0.025F);
        minecraft.font.drawInBatch(
                label,
                -minecraft.font.width(label) / 2.0F,
                0.0F,
                0xFFFFFFFF,
                true,
                poseStack.last().pose(),
                buffers,
                Font.DisplayMode.NORMAL,
                0x60000000,
                LightTexture.FULL_BRIGHT);
        poseStack.popPose();
    }

    private static void renderFlag(
            final PoseStack poseStack,
            final MultiBufferSource buffers,
            final String iconId,
            final double x,
            final double y,
            final double z,
            final float scale) {
        final Minecraft minecraft = Minecraft.getInstance();
        final String textureIconId = switch (iconId) {
            case "sheep_pen", "chicken_pen", "pig_pen", "rabbit_hutch", "stable", "stall" -> "animal_pen";
            case "apiary", "hive" -> "farm";
            case "blacksmith", "sawmill", "stonemason", "fletcher", "mechanic",
                    "concrete_mixer", "crusher", "sifter", "bakery", "kitchen", "smeltery",
                    "stone_smelter", "glassblower", "dyer", "alchemist", "farmer", "plantation",
                    "fisherman", "lumberjack", "florist", "composter" -> "worksite";
            default -> iconId;
        };
        final ResourceLocation iconTexture = ResourceLocation.fromNamespaceAndPath(
                ColoniesUnbound.MOD_ID,
                "textures/marker/icons/" + textureIconId + ".png");

        poseStack.pushPose();
        poseStack.translate(x, y, z);
        poseStack.mulPose(Axis.YP.rotationDegrees(
                180.0F - minecraft.gameRenderer.getMainCamera().getYRot()));
        poseStack.scale(scale, scale, scale);

        if (minecraft.getResourceManager().getResource(FLAG_TEXTURE).isPresent()) {
            renderTexturedQuad(
                    poseStack,
                    buffers.getBuffer(RenderType.entityTranslucent(FLAG_TEXTURE)),
                    0.0F,
                    FlagColor.forIcon(iconId));
        }
        if (minecraft.getResourceManager().getResource(iconTexture).isPresent()) {
            renderTexturedQuad(
                    poseStack,
                    buffers.getBuffer(RenderType.entityTranslucent(iconTexture)),
                    0.004F,
                    FlagColor.WHITE);
        }
        poseStack.popPose();
    }

    private static void renderTexturedQuad(
            final PoseStack poseStack,
            final VertexConsumer consumer,
            final float depth,
            final FlagColor color) {
        final PoseStack.Pose pose = poseStack.last();
        vertex(consumer, pose, -0.5F, -0.5F, depth, 0.0F, 1.0F, color);
        vertex(consumer, pose, 0.5F, -0.5F, depth, 1.0F, 1.0F, color);
        vertex(consumer, pose, 0.5F, 0.5F, depth, 1.0F, 0.0F, color);
        vertex(consumer, pose, -0.5F, 0.5F, depth, 0.0F, 0.0F, color);
    }

    private static void vertex(
            final VertexConsumer consumer,
            final PoseStack.Pose pose,
            final float x,
            final float y,
            final float z,
            final float u,
            final float v,
            final FlagColor color) {
        consumer.addVertex(pose, x, y, z)
                .setColor(color.red(), color.green(), color.blue(), 255)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(pose, 0.0F, 0.0F, 1.0F);
    }

    private record FlagColor(int red, int green, int blue) {
        private static final FlagColor WHITE = new FlagColor(255, 255, 255);

        private static FlagColor forIcon(final String iconId) {
            return switch (iconId) {
                case "residence" -> new FlagColor(63, 115, 230);
                case "bed" -> new FlagColor(77, 184, 255);
                case "storage" -> new FlagColor(227, 166, 47);
                case "worksite", "furnace" -> new FlagColor(240, 122, 43);
                case "farm", "animal_pen" -> new FlagColor(91, 176, 72);
                case "sheep_pen" -> new FlagColor(120, 185, 106);
                case "chicken_pen" -> new FlagColor(228, 201, 90);
                case "pig_pen" -> new FlagColor(229, 141, 152);
                case "rabbit_hutch" -> new FlagColor(183, 149, 120);
                case "stable", "stall" -> new FlagColor(155, 108, 66);
                case "apiary", "hive" -> new FlagColor(225, 167, 45);
                case "restaurant", "interaction" -> new FlagColor(180, 96, 210);
                case "guard", "patrol", "target" -> new FlagColor(210, 66, 66);
                case "entrance" -> new FlagColor(64, 190, 170);
                case "invalid" -> new FlagColor(235, 48, 48);
                default -> new FlagColor(150, 160, 175);
            };
        }
    }

    @Override
    public boolean shouldRenderOffScreen(final T tile) {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(final T tile) {
        final AABB anchorBounds = new AABB(tile.getBlockPos()).inflate(2.0);
        final CommittedWorksiteMark mark = tile.committedMark();
        if (mark == null) {
            return anchorBounds;
        }

        // NeoForge still frustum-tests globally rendered block entities. Cover the
        // complete marked volume so a visible POI flag is not culled merely because
        // the building anchor itself has left the camera frustum.
        AABB renderBounds = mark.bounds().asAabb().inflate(2.0).minmax(anchorBounds);
        for (final WorksitePoi point : mark.pois()) {
            renderBounds = renderBounds.minmax(new AABB(point.position()).inflate(2.0));
        }
        return renderBounds;
    }

    @Override
    public int getViewDistance() {
        return 128;
    }
}
