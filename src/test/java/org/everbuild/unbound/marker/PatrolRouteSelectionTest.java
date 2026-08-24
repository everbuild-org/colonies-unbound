package org.everbuild.unbound.marker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Test;

class PatrolRouteSelectionTest {
    @Test
    void persistsAndClearsTheSelectedGuardPlaque() {
        final ItemStack stack = new ItemStack(Items.STICK);
        final PatrolRouteSelection selection = new PatrolRouteSelection(
                ResourceLocation.fromNamespaceAndPath("minecraft", "overworld"),
                new BlockPos(12, 70, -8));

        PatrolRouteSelection.write(stack, selection);
        assertEquals(selection, PatrolRouteSelection.read(stack));

        PatrolRouteSelection.clear(stack);
        assertNull(PatrolRouteSelection.read(stack));
    }
}
