package org.everbuild.unbound.registry;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.everbuild.unbound.ColoniesUnbound;
import org.everbuild.unbound.item.WorksiteMarkerItem;

public final class ModItems {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ColoniesUnbound.MOD_ID);

    public static final DeferredItem<WorksiteMarkerItem> WORKSITE_MARKER = ITEMS.registerItem(
            "worksite_marker", WorksiteMarkerItem::new, new Item.Properties());

    private ModItems() {
    }

    public static void register(final IEventBus modBus) {
        ITEMS.register(modBus);
        modBus.addListener(ModItems::addToCreativeTabs);
    }

    private static void addToCreativeTabs(final BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(WORKSITE_MARKER);
        }
    }
}
