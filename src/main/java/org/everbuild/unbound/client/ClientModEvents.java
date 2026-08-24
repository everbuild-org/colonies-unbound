package org.everbuild.unbound.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import org.everbuild.unbound.ColoniesUnbound;
import org.everbuild.unbound.minecolonies.MineColoniesIntegration;

/** Client-only mod-bus registrations. */
@EventBusSubscriber(modid = ColoniesUnbound.MOD_ID, value = Dist.CLIENT)
public final class ClientModEvents {
    private ClientModEvents() {
    }

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                MineColoniesIntegration.SURVIVAL_RESIDENCE_TILE.get(),
                SurvivalResidenceRenderer::new);
        event.registerBlockEntityRenderer(
                MineColoniesIntegration.SURVIVAL_COOK_TILE.get(),
                SurvivalResidenceRenderer::new);
        event.registerBlockEntityRenderer(
                MineColoniesIntegration.SURVIVAL_GUARD_TILE.get(),
                SurvivalResidenceRenderer::new);
        event.registerBlockEntityRenderer(
                MineColoniesIntegration.SURVIVAL_COW_PEN_TILE.get(),
                SurvivalResidenceRenderer::new);
        event.registerBlockEntityRenderer(
                MineColoniesIntegration.SURVIVAL_SHEEP_PEN_TILE.get(),
                SurvivalResidenceRenderer::new);
    }
}
