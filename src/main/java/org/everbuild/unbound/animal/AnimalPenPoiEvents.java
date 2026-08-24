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
import org.everbuild.unbound.minecolonies.MarkedBuildingTileEntity;
import org.everbuild.unbound.minecolonies.SurvivalAnimalPenBuilding;

/** Reconciles scanner-owned livestock gates and pasture without forcing chunk loads. */
@EventBusSubscriber(modid = ColoniesUnbound.MOD_ID)
public final class AnimalPenPoiEvents {
    private static final long BLOCK_CHANGE_DELAY = 10L;
    private static final long CHUNK_LOAD_DELAY = 20L;
    private static final Map<ServerLevel, DebouncedRescanQueue<BlockPos>> PENDING = new IdentityHashMap<>();

    private AnimalPenPoiEvents() {
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
        scheduleChunk(
                level,
                event.getChunk(),
                event.getChunk().getPos().getMinBlockX(),
                event.getChunk().getPos().getMinBlockZ(),
                event.getChunk().getPos().getMaxBlockX(),
                event.getChunk().getPos().getMaxBlockZ());
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
            final MarkedBuildingTileEntity tile = noLoadTile(level, plaquePosition, null);
            if (building instanceof SurvivalAnimalPenBuilding animalPen && tile != null) {
                SurvivalAnimalPenMarkerService.reconcilePois(level, animalPen, tile);
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
            if (building instanceof SurvivalAnimalPenBuilding animalPen) {
                final MarkedBuildingTileEntity tile = noLoadTile(level, animalPen.getPosition(), null);
                if (tile != null && tile.committedMark().bounds().contains(position)) {
                    schedule(level, tile.getBlockPos(), deadline);
                }
            }
        }
    }

    private static void scheduleChunk(
            final ServerLevel level,
            final ChunkAccess loadedChunk,
            final int minimumX,
            final int minimumZ,
            final int maximumX,
            final int maximumZ) {
        final long deadline = level.getGameTime() + CHUNK_LOAD_DELAY;
        for (final IColony colony : IColonyManager.getInstance().getColonies(level)) {
            for (final IBuilding building : colony.getServerBuildingManager().getBuildings().values()) {
                if (building instanceof SurvivalAnimalPenBuilding animalPen) {
                    final MarkedBuildingTileEntity tile = noLoadTile(level, animalPen.getPosition(), loadedChunk);
                    if (tile != null && tile.committedMark().bounds()
                            .intersectsChunk(minimumX, minimumZ, maximumX, maximumZ)) {
                        schedule(level, tile.getBlockPos(), deadline);
                    }
                }
            }
        }
    }

    private static MarkedBuildingTileEntity noLoadTile(
            final ServerLevel level,
            final BlockPos position,
            final ChunkAccess loadedChunk) {
        final int chunkX = SectionPos.blockToSectionCoord(position.getX());
        final int chunkZ = SectionPos.blockToSectionCoord(position.getZ());
        final ChunkAccess chunk = loadedChunk != null
                        && loadedChunk.getPos().x == chunkX
                        && loadedChunk.getPos().z == chunkZ
                ? loadedChunk
                : level.getChunkSource().getChunkNow(chunkX, chunkZ);
        return chunk != null
                        && chunk.getBlockEntity(position) instanceof MarkedBuildingTileEntity tile
                        && tile.committedMark() != null
                        && tile.getBuilding() instanceof SurvivalAnimalPenBuilding
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
