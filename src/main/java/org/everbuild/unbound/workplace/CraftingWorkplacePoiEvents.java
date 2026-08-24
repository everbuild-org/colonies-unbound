package org.everbuild.unbound.workplace;

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
import org.everbuild.unbound.minecolonies.SurvivalCraftingBuilding;
import org.everbuild.unbound.minecolonies.SurvivalCraftingTileEntity;

/** Reconciles scanner-owned Wave 2 workstation positions without forcing chunk loads. */
@EventBusSubscriber(modid = ColoniesUnbound.MOD_ID)
public final class CraftingWorkplacePoiEvents {
    private static final Map<ServerLevel, DebouncedRescanQueue<BlockPos>> PENDING = new IdentityHashMap<>();
    private CraftingWorkplacePoiEvents() { }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlace(final BlockEvent.EntityPlaceEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (event instanceof BlockEvent.EntityMultiPlaceEvent multi) {
            for (final BlockSnapshot snapshot : multi.getReplacedBlockSnapshots()) scheduleAt(level, snapshot.getPos(), 10L);
        } else scheduleAt(level, event.getPos(), 10L);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBreak(final BlockEvent.BreakEvent event) {
        if (event.getLevel() instanceof ServerLevel level) scheduleAt(level, event.getPos(), 10L);
    }

    @SubscribeEvent
    public static void onChunkLoad(final ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        final ChunkAccess chunk = event.getChunk();
        final long deadline = level.getGameTime() + 20L;
        for (final IColony colony : IColonyManager.getInstance().getColonies(level)) {
            for (final IBuilding candidate : colony.getServerBuildingManager().getBuildings().values()) {
                if (candidate instanceof SurvivalCraftingBuilding building) {
                    final SurvivalCraftingTileEntity tile = noLoadTile(level, building.getPosition(), chunk);
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
    public static void onTick(final LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        final DebouncedRescanQueue<BlockPos> queue = PENDING.get(level);
        if (queue == null) return;
        for (final BlockPos plaque : queue.drainDue(level.getGameTime())) {
            final IBuilding nativeBuilding = IColonyManager.getInstance().getBuilding(level, plaque);
            final SurvivalCraftingTileEntity tile = noLoadTile(level, plaque, null);
            if (nativeBuilding instanceof SurvivalCraftingBuilding building && tile != null) {
                SurvivalCraftingMarkerService.reconcilePois(level, building, tile);
            }
        }
        if (queue.isEmpty()) PENDING.remove(level);
    }

    @SubscribeEvent
    public static void onUnload(final LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level) PENDING.remove(level);
    }

    private static void scheduleAt(final ServerLevel level, final BlockPos changed, final long delay) {
        final IColony colony = IColonyManager.getInstance().getColonyByPosFromWorld(level, changed);
        if (colony == null) return;
        for (final IBuilding candidate : colony.getServerBuildingManager().getBuildings().values()) {
            if (candidate instanceof SurvivalCraftingBuilding building) {
                final SurvivalCraftingTileEntity tile = noLoadTile(level, building.getPosition(), null);
                if (tile != null && tile.committedMark().bounds().contains(changed)) {
                    schedule(level, tile.getBlockPos(), level.getGameTime() + delay);
                }
            }
        }
    }

    private static SurvivalCraftingTileEntity noLoadTile(
            final ServerLevel level, final BlockPos position, final ChunkAccess loadedChunk) {
        final int x = SectionPos.blockToSectionCoord(position.getX());
        final int z = SectionPos.blockToSectionCoord(position.getZ());
        final ChunkAccess chunk = loadedChunk != null && loadedChunk.getPos().x == x && loadedChunk.getPos().z == z
                ? loadedChunk : level.getChunkSource().getChunkNow(x, z);
        return chunk != null && chunk.getBlockEntity(position) instanceof SurvivalCraftingTileEntity tile
                        && tile.committedMark() != null ? tile : null;
    }

    private static void schedule(final ServerLevel level, final BlockPos plaque, final long deadline) {
        PENDING.computeIfAbsent(level, ignored -> new DebouncedRescanQueue<>()).schedule(plaque.immutable(), deadline);
    }
}
