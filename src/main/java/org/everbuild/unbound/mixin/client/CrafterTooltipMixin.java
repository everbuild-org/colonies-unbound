package org.everbuild.unbound.mixin.client;

import com.minecolonies.api.IMinecoloniesAPI;
import com.minecolonies.api.colony.buildings.registry.BuildingEntry;
import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Keeps native buildings as the tooltip representatives for shared recipe modules. */
@Mixin(targets = "com.minecolonies.core.event.ClientEventHandler", remap = false)
public abstract class CrafterTooltipMixin {
    @Redirect(
            method = "buildCrafterToBuildingMap",
            at = @At(value = "INVOKE", target = "Lcom/minecolonies/api/colony/buildings/registry/BuildingEntry;getModuleProducers()Ljava/util/List;"),
            require = 1)
    private static List<BuildingEntry.ModuleProducer> coloniesunbound$tooltipModules(final BuildingEntry building) {
        final List<BuildingEntry.ModuleProducer> modules = building.getModuleProducers();
        if (!"coloniesunbound".equals(building.getRegistryName().getNamespace())) {
            return modules;
        }

        // Filter only producers also used by native buildings, and only in this lookup.
        // Recipe keys, persisted module keys and the actual building modules stay intact.
        return modules.stream().filter(module -> {
            for (final BuildingEntry candidate : IMinecoloniesAPI.getInstance().getBuildingRegistry()) {
                if ("minecolonies".equals(candidate.getRegistryName().getNamespace())
                        && candidate.getModuleProducers().contains(module)) {
                    return false;
                }
            }
            return true;
        }).toList();
    }
}
