package org.everbuild.unbound.workplace;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.buildings.IBuilding;
import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.BlockSnapshot;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.everbuild.unbound.ColoniesUnbound;
import org.everbuild.unbound.marker.DebouncedRescanQueue;
import org.everbuild.unbound.minecolonies.SurvivalCookBuilding;
import org.everbuild.unbound.minecolonies.SurvivalCookTileEntity;

/** Keeps accurately discoverable Dining Hall appliances synchronized after world changes. */
@EventBusSubscriber(modid = ColoniesUnbound.MOD_ID)
public final class CookPoiEvents {
    private static final long BLOCK_CHANGE_DELAY = 10L;
    private static final long CHUNK_LOAD_DELAY = 20L;
    private static final Map<ServerLevel, DebouncedRescanQueue<BlockPos>> PENDING = new IdentityHashMap<>();

    private CookPoiEvents() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockPlaced(final BlockEvent.EntityPlaceEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        if (event instanceof BlockEvent.EntityMultiPlaceEvent multiPlace) {
            for (final BlockSnapshot snapshot : multiPlace.getReplacedBlockSnapshots()) {
                scheduleAt(level, snapshot.getPos(), BLOCK_CHANGE_DELAY);
            }
            return;
        }
        scheduleAt(level, event.getPos(), BLOCK_CHANGE_DELAY);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockBroken(final BlockEvent.BreakEvent event) {
        if (event.getLevel() instanceof ServerLevel level) {
            scheduleAt(level, event.getPos(), BLOCK_CHANGE_DELAY);
        }
    }

    @SubscribeEvent
    public static void onChunkLoaded(final ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        final int minimumX = event.getChunk().getPos().getMinBlockX();
        final int minimumZ = event.getChunk().getPos().getMinBlockZ();
        final int maximumX = event.getChunk().getPos().getMaxBlockX();
        final int maximumZ = event.getChunk().getPos().getMaxBlockZ();
        level.getServer().execute(() -> scheduleChunk(
                level, minimumX, minimumZ, maximumX, maximumZ));
    }

    @SubscribeEvent
    public static void onLevelTick(final LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        final DebouncedRescanQueue<BlockPos> queue = PENDING.get(level);
        if (queue == null) {
            return;
        }
        for (final BlockPos plaquePosition : queue.drainDue(level.getGameTime())) {
            final IBuilding building = IColonyManager.getInstance().getBuilding(level, plaquePosition);
            if (building instanceof SurvivalCookBuilding cookBuilding
                    && level.getBlockEntity(plaquePosition) instanceof SurvivalCookTileEntity cookTile) {
                SurvivalCookMarkerService.reconcilePois(level, cookBuilding, cookTile);
            }
        }
        if (queue.isEmpty()) {
            PENDING.remove(level);
        }
    }

    @SubscribeEvent
    public static void onLevelUnload(final LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level) {
            PENDING.remove(level);
        }
    }

    private static void scheduleAt(final ServerLevel level, final BlockPos position, final long delay) {
        final IColony colony = IColonyManager.getInstance().getColonyByPosFromWorld(level, position);
        if (colony == null) {
            return;
        }
        final long deadline = level.getGameTime() + delay;
        for (final IBuilding building : colony.getServerBuildingManager().getBuildings().values()) {
            if (building instanceof SurvivalCookBuilding cookBuilding) {
                final SurvivalCookTileEntity tile = tile(level, cookBuilding);
                if (tile != null && tile.committedMark().bounds().contains(position)) {
                    schedule(level, tile.getBlockPos(), deadline);
                }
            }
        }
    }

    private static void scheduleChunk(
            final ServerLevel level,
            final int minimumX,
            final int minimumZ,
            final int maximumX,
            final int maximumZ) {
        final long deadline = level.getGameTime() + CHUNK_LOAD_DELAY;
        for (final IColony colony : IColonyManager.getInstance().getColonies(level)) {
            for (final IBuilding building : colony.getServerBuildingManager().getBuildings().values()) {
                if (building instanceof SurvivalCookBuilding cookBuilding) {
                    final SurvivalCookTileEntity tile = tile(level, cookBuilding);
                    if (tile != null && tile.committedMark().bounds()
                            .intersectsChunk(minimumX, minimumZ, maximumX, maximumZ)) {
                        schedule(level, tile.getBlockPos(), deadline);
                    }
                }
            }
        }
    }

    private static SurvivalCookTileEntity tile(
            final ServerLevel level,
            final SurvivalCookBuilding building) {
        return level.getBlockEntity(building.getPosition()) instanceof SurvivalCookTileEntity tile
                        && tile.committedMark() != null
                ? tile
                : null;
    }

    private static void schedule(
            final ServerLevel level,
            final BlockPos plaquePosition,
            final long deadline) {
        PENDING.computeIfAbsent(level, ignored -> new DebouncedRescanQueue<>())
                .schedule(plaquePosition.immutable(), deadline);
    }
}
