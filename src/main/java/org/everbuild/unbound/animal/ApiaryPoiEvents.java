package org.everbuild.unbound.animal;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.buildings.IBuilding;
import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
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
import org.everbuild.unbound.minecolonies.SurvivalApiaryBuilding;
import org.everbuild.unbound.minecolonies.SurvivalApiaryTileEntity;

/** Reconciles scanner-owned hives without forcing chunk loads. */
@EventBusSubscriber(modid = ColoniesUnbound.MOD_ID)
public final class ApiaryPoiEvents {
    private static final long BLOCK_CHANGE_DELAY = 10L;
    private static final long CHUNK_LOAD_DELAY = 20L;
    private static final Map<ServerLevel, DebouncedRescanQueue<BlockPos>> PENDING = new IdentityHashMap<>();

    private ApiaryPoiEvents() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockPlaced(final BlockEvent.EntityPlaceEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (event instanceof BlockEvent.EntityMultiPlaceEvent multiPlace) {
            for (final BlockSnapshot snapshot : multiPlace.getReplacedBlockSnapshots()) {
                scheduleAt(level, snapshot.getPos(), BLOCK_CHANGE_DELAY);
            }
        } else {
            scheduleAt(level, event.getPos(), BLOCK_CHANGE_DELAY);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockBroken(final BlockEvent.BreakEvent event) {
        if (event.getLevel() instanceof ServerLevel level) scheduleAt(level, event.getPos(), BLOCK_CHANGE_DELAY);
    }

    @SubscribeEvent
    public static void onChunkLoaded(final ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        final ChunkAccess chunk = event.getChunk();
        final long deadline = level.getGameTime() + CHUNK_LOAD_DELAY;
        for (final IColony colony : IColonyManager.getInstance().getColonies(level)) {
            for (final IBuilding building : colony.getServerBuildingManager().getBuildings().values()) {
                if (building instanceof SurvivalApiaryBuilding apiary) {
                    final SurvivalApiaryTileEntity tile = noLoadTile(level, apiary.getPosition(), chunk);
                    if (tile != null && tile.committedMark().bounds().intersectsChunk(
                            chunk.getPos().getMinBlockX(), chunk.getPos().getMinBlockZ(),
                            chunk.getPos().getMaxBlockX(), chunk.getPos().getMaxBlockZ())) {
                        schedule(level, tile.getBlockPos(), deadline);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLevelTick(final LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        final DebouncedRescanQueue<BlockPos> queue = PENDING.get(level);
        if (queue == null) return;
        for (final BlockPos plaque : queue.drainDue(level.getGameTime())) {
            final IBuilding building = IColonyManager.getInstance().getBuilding(level, plaque);
            final SurvivalApiaryTileEntity tile = noLoadTile(level, plaque, null);
            if (building instanceof SurvivalApiaryBuilding apiary && tile != null) {
                SurvivalApiaryMarkerService.reconcilePois(level, apiary, tile);
            }
        }
        if (queue.isEmpty()) PENDING.remove(level);
    }

    @SubscribeEvent
    public static void onLevelUnload(final LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level) PENDING.remove(level);
    }

    private static void scheduleAt(final ServerLevel level, final BlockPos position, final long delay) {
        final IColony colony = IColonyManager.getInstance().getColonyByPosFromWorld(level, position);
        if (colony == null) return;
        final long deadline = level.getGameTime() + delay;
        for (final IBuilding building : colony.getServerBuildingManager().getBuildings().values()) {
            if (building instanceof SurvivalApiaryBuilding apiary) {
                final SurvivalApiaryTileEntity tile = noLoadTile(level, apiary.getPosition(), null);
                if (tile != null && tile.committedMark().bounds().contains(position)) {
                    schedule(level, tile.getBlockPos(), deadline);
                }
            }
        }
    }

    private static SurvivalApiaryTileEntity noLoadTile(
            final ServerLevel level, final BlockPos position, final ChunkAccess loadedChunk) {
        final int chunkX = SectionPos.blockToSectionCoord(position.getX());
        final int chunkZ = SectionPos.blockToSectionCoord(position.getZ());
        final ChunkAccess chunk = loadedChunk != null
                        && loadedChunk.getPos().x == chunkX && loadedChunk.getPos().z == chunkZ
                ? loadedChunk : level.getChunkSource().getChunkNow(chunkX, chunkZ);
        return chunk != null
                        && chunk.getBlockEntity(position) instanceof SurvivalApiaryTileEntity tile
                        && tile.committedMark() != null
                ? tile : null;
    }

    private static void schedule(final ServerLevel level, final BlockPos plaque, final long deadline) {
        PENDING.computeIfAbsent(level, ignored -> new DebouncedRescanQueue<>())
                .schedule(plaque.immutable(), deadline);
    }
}
