package org.everbuild.unbound.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.everbuild.unbound.ColoniesUnbound;
import org.everbuild.unbound.marker.AreaBounds;
import org.everbuild.unbound.marker.MarkerSelection;
import org.everbuild.unbound.registry.ModItems;

/** World-space preview shown only while a Worksite Marker is held. */
@EventBusSubscriber(modid = ColoniesUnbound.MOD_ID, value = Dist.CLIENT)
public final class MarkerOverlayRenderer {
    private static final float CYAN_RED = 0.18F;
    private static final float CYAN_GREEN = 0.90F;
    private static final float CYAN_BLUE = 1.00F;

    private MarkerOverlayRenderer() {
    }

    @SubscribeEvent
    public static void render(final RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }

        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) {
            return;
        }

        final ItemStack marker = heldMarker(minecraft);
        if (marker.isEmpty()) {
            return;
        }

        final MarkerSelection selection = MarkerSelection.read(marker);
        if (selection == null) {
            return;
        }

        final ResourceLocation currentDimension = minecraft.level.dimension().location();
        if (!selection.dimension().equals(currentDimension)) {
            return;
        }

        final PoseStack poseStack = event.getPoseStack();
        final Vec3 camera = event.getCamera().getPosition();
        poseStack.pushPose();
        poseStack.translate(-camera.x, -camera.y, -camera.z);

        final MultiBufferSource.BufferSource buffers = minecraft.renderBuffers().bufferSource();
        final VertexConsumer lines = buffers.getBuffer(RenderType.lines());

        renderPin(poseStack, lines, selection.firstCorner(), CYAN_RED, CYAN_GREEN, CYAN_BLUE);

        final AreaBounds completed = selection.bounds();
        if (completed != null) {
            renderBounds(poseStack, lines, completed, 1.00F, 0.67F, 0.10F, 0.95F);
            renderPin(poseStack, lines, selection.secondCorner(), 1.00F, 0.67F, 0.10F);
        } else if (minecraft.hitResult instanceof BlockHitResult hit) {
            final AreaBounds preview = AreaBounds.between(selection.firstCorner(), hit.getBlockPos());
            renderBounds(poseStack, lines, preview, CYAN_RED, CYAN_GREEN, CYAN_BLUE, 0.65F);
        }

        buffers.endBatch(RenderType.lines());
        poseStack.popPose();
    }

    public static boolean isMarkerHeld(final Minecraft minecraft) {
        return !heldMarker(minecraft).isEmpty();
    }

    private static ItemStack heldMarker(final Minecraft minecraft) {
        final ItemStack mainHand = minecraft.player.getMainHandItem();
        if (mainHand.is(ModItems.WORKSITE_MARKER.get())) {
            return mainHand;
        }

        final ItemStack offHand = minecraft.player.getOffhandItem();
        return offHand.is(ModItems.WORKSITE_MARKER.get()) ? offHand : ItemStack.EMPTY;
    }

    private static void renderBounds(
            final PoseStack poseStack,
            final VertexConsumer lines,
            final AreaBounds bounds,
            final float red,
            final float green,
            final float blue,
            final float alpha) {
        LevelRenderer.renderLineBox(poseStack, lines, bounds.asAabb().inflate(0.004), red, green, blue, alpha);
    }

    /** A compact pin head and stem whose tip terminates at the selected block's top center. */
    private static void renderPin(
            final PoseStack poseStack,
            final VertexConsumer lines,
            final BlockPos target,
            final float red,
            final float green,
            final float blue) {
        final double centerX = target.getX() + 0.5;
        final double centerZ = target.getZ() + 0.5;
        final double tipY = target.getY() + 1.02;
        final AABB stem = new AABB(
                centerX - 0.015, tipY, centerZ - 0.015,
                centerX + 0.015, tipY + 0.55, centerZ + 0.015);
        final AABB head = new AABB(
                centerX - 0.24, tipY + 0.50, centerZ - 0.24,
                centerX + 0.24, tipY + 0.98, centerZ + 0.24);
        LevelRenderer.renderLineBox(poseStack, lines, stem, red, green, blue, 1.0F);
        LevelRenderer.renderLineBox(poseStack, lines, head, red, green, blue, 1.0F);
    }
}
