package org.everbuild.unbound.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import org.everbuild.unbound.ColoniesUnbound;
import org.everbuild.unbound.marker.CommittedWorksiteMark;
import org.everbuild.unbound.marker.WorksitePoi;
import org.everbuild.unbound.minecolonies.SurvivalResidenceTileEntity;

/** Renders a committed residence volume and its type flag while a marker is held. */
public final class SurvivalResidenceRenderer implements BlockEntityRenderer<SurvivalResidenceTileEntity> {
    private static final float RED = 1.00F;
    private static final float GREEN = 0.48F;
    private static final float BLUE = 0.08F;
    private static final ResourceLocation FLAG_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            ColoniesUnbound.MOD_ID, "textures/marker/flag_base.png");

    public SurvivalResidenceRenderer(final BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(
            final SurvivalResidenceTileEntity tile,
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
        for (final WorksitePoi poi : mark.pois()) {
            renderFlag(
                    poseStack,
                    buffers,
                    poi.type().id(),
                    poi.position().getX() - origin.getX() + 0.5,
                    poi.position().getY() - origin.getY() + 1.6,
                    poi.position().getZ() - origin.getZ() + 0.5,
                    0.75F);
        }
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
        final ResourceLocation iconTexture = ResourceLocation.fromNamespaceAndPath(
                ColoniesUnbound.MOD_ID,
                "textures/marker/icons/" + iconId + ".png");

        poseStack.pushPose();
        poseStack.translate(x, y, z);
        poseStack.mulPose(Axis.YP.rotationDegrees(-minecraft.gameRenderer.getMainCamera().getYRot()));
        poseStack.scale(scale, scale, scale);

        if (minecraft.getResourceManager().getResource(FLAG_TEXTURE).isPresent()) {
            renderTexturedQuad(
                    poseStack,
                    buffers.getBuffer(RenderType.entityCutoutNoCull(FLAG_TEXTURE)),
                    0.0F);
        }
        if (minecraft.getResourceManager().getResource(iconTexture).isPresent()) {
            renderTexturedQuad(
                    poseStack,
                    buffers.getBuffer(RenderType.entityCutoutNoCull(iconTexture)),
                    0.002F);
        }
        poseStack.popPose();
    }

    private static void renderTexturedQuad(
            final PoseStack poseStack,
            final VertexConsumer consumer,
            final float depth) {
        final PoseStack.Pose pose = poseStack.last();
        vertex(consumer, pose, -0.5F, -0.5F, depth, 0.0F, 1.0F);
        vertex(consumer, pose, 0.5F, -0.5F, depth, 1.0F, 1.0F);
        vertex(consumer, pose, 0.5F, 0.5F, depth, 1.0F, 0.0F);
        vertex(consumer, pose, -0.5F, 0.5F, depth, 0.0F, 0.0F);
    }

    private static void vertex(
            final VertexConsumer consumer,
            final PoseStack.Pose pose,
            final float x,
            final float y,
            final float z,
            final float u,
            final float v) {
        consumer.addVertex(pose, x, y, z)
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(pose, 0.0F, 0.0F, 1.0F);
    }

    @Override
    public boolean shouldRenderOffScreen(final SurvivalResidenceTileEntity tile) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 128;
    }
}
