package org.everbuild.unbound.item;

import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.everbuild.unbound.ColoniesUnbound;
import org.everbuild.unbound.marker.MarkerToolMode;

/** Gives semantic point editing priority over chest, furnace, door, and other block menus. */
@EventBusSubscriber(modid = ColoniesUnbound.MOD_ID)
public final class MarkerInteractionEvents {
    private MarkerInteractionEvents() {
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void useMarkerBeforeBlock(final UseItemOnBlockEvent event) {
        if (event.getUsePhase() != UseItemOnBlockEvent.UsePhase.ITEM_BEFORE_BLOCK
                || !(event.getItemStack().getItem() instanceof WorksiteMarkerItem markerItem)
                || MarkerToolMode.read(event.getItemStack()).isAreaMode()) {
            return;
        }

        markerItem.useOn(event.getUseOnContext());
        event.cancelWithResult(ItemInteractionResult.sidedSuccess(event.getLevel().isClientSide));
    }

    /** Empty main hands normally let a container consume the click before an offhand marker is tried. */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void useOffhandMarkerBeforeBlock(final PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() != InteractionHand.MAIN_HAND
                || !event.getEntity().getMainHandItem().isEmpty()
                || !(event.getEntity().getOffhandItem().getItem() instanceof WorksiteMarkerItem markerItem)
                || MarkerToolMode.read(event.getEntity().getOffhandItem()).isAreaMode()) {
            return;
        }

        markerItem.useOn(new UseOnContext(
                event.getEntity(), InteractionHand.OFF_HAND, event.getHitVec()));
        event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
        event.setCanceled(true);
    }
}
