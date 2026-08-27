package org.everbuild.unbound.marker;

import static org.junit.jupiter.api.Assertions.assertEquals;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Test;

class MarkerToolModeTest {
    @Test
    void defaultsToAreaAndPersistsSelectedMode() {
        final ItemStack stack = new ItemStack(Items.STICK);

        assertEquals(MarkerToolMode.RESIDENCE_AREA, MarkerToolMode.read(stack));

        MarkerToolMode.write(stack, MarkerToolMode.STORAGE);
        assertEquals(MarkerToolMode.STORAGE, MarkerToolMode.read(stack));
    }

    @Test
    void cyclesBackToAreaAfterAllPointModes() {
        assertEquals(MarkerToolMode.STALL, MarkerToolMode.INTERACTION.next());
        assertEquals(MarkerToolMode.BUILDING_LEVEL, MarkerToolMode.STALL.next());
        assertEquals(MarkerToolMode.PATROL_ROUTE, MarkerToolMode.BUILDING_LEVEL.next());
        assertEquals(MarkerToolMode.RESIDENCE_AREA, MarkerToolMode.PATROL_ROUTE.next());
    }
}
