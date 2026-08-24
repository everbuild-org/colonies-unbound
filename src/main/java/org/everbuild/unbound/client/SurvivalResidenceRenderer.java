package org.everbuild.unbound.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import org.everbuild.unbound.marker.CommittedWorksiteMark;
import org.everbuild.unbound.minecolonies.SurvivalResidenceTileEntity;

/** Renders a committed residence volume and its type flag while a marker is held. */
public final class SurvivalResidenceRenderer implements BlockEntityRenderer<SurvivalResidenceTileEntity> {
    private static final float RED = 1.00F;
    private static final float GREEN = 0.48F;
    private static final float BLUE = 0.08F;

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

        renderResidenceFlag(poseStack, lines);
    }

    private static void renderResidenceFlag(final PoseStack poseStack, final VertexConsumer lines) {
        final AABB pole = new AABB(0.485, 0.55, 0.485, 0.515, 1.75, 0.515);
        final AABB flag = new AABB(0.51, 1.25, 0.47, 1.05, 1.72, 0.53);
        LevelRenderer.renderLineBox(poseStack, lines, pole, RED, GREEN, BLUE, 1.0F);
        LevelRenderer.renderLineBox(poseStack, lines, flag, RED, GREEN, BLUE, 1.0F);
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
