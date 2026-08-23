package org.everbuild.unbound;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.everbuild.unbound.registry.ModItems;
import org.slf4j.Logger;

/**
 * NeoForge entry point for Colonies Unbound.
 */
@Mod(ColoniesUnbound.MOD_ID)
public final class ColoniesUnbound {
    public static final String MOD_ID = "coloniesunbound";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ColoniesUnbound(final IEventBus modBus) {
        ModItems.register(modBus);
        LOGGER.info("Colonies Unbound is loading");
    }
}
