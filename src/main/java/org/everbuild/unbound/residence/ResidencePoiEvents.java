package org.everbuild.unbound.residence;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;
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

/** Schedules MineColonies POI reconciliation when committed residence volumes change. */
@EventBusSubscriber(modid = ColoniesUnbound.MOD_ID)
public final class ResidencePoiEvents {
    private static final long BLOCK_CHANGE_DELAY = 10L;
    private static final long CHUNK_LOAD_DELAY = 20L;
    private static final long ORPHAN_RETRY_DELAY = 100L;
    private static final int ORPHAN_REMOVAL_THRESHOLD = 3;
    private static final Map<ServerLevel, MarkerRescanQueue> PENDING = new IdentityHashMap<>();
    private static final Map<ServerLevel, OrphanRetryTracker> ORPHAN_RETRIES = new IdentityHashMap<>();

    private ResidencePoiEvents() {
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

    private static void scheduleChunk(
            final ServerLevel level,
            final int minimumX,
            final int minimumZ,
            final int maximumX,
            final int maximumZ) {
        final long deadline = level.getGameTime() + CHUNK_LOAD_DELAY;
        for (final SurvivalResidenceMarker marker : ResidenceMarkerData.get(level).markers()) {
            if (marker.bounds().intersectsChunk(minimumX, minimumZ, maximumX, maximumZ)) {
                schedule(level, marker.id(), deadline);
            }
        }
    }

    @SubscribeEvent
    public static void onLevelTick(final LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        final MarkerRescanQueue queue = PENDING.get(level);
        if (queue == null) {
            return;
        }
        final ResidenceMarkerData data = ResidenceMarkerData.get(level);
        for (final UUID markerId : queue.drainDue(level.getGameTime())) {
            final SurvivalResidenceMarker marker = data.marker(markerId);
            if (marker != null) {
                handleResult(level, marker, ResidenceMarkerService.reconcilePois(level, marker));
            } else {
                orphanTracker(level).clear(markerId);
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
            ORPHAN_RETRIES.remove(level);
        }
    }

    private static void handleResult(
            final ServerLevel level,
            final SurvivalResidenceMarker marker,
            final ResidenceMarkerService.ReconcileResult result) {
        switch (result) {
            case UPDATED, UNCHANGED -> orphanTracker(level).clear(marker.id());
            case AREA_NOT_LOADED, MULTIPLE_PLAQUES -> {
                // A later chunk load or block change will make this safe to inspect again.
            }
            case MISSING_PLAQUE, MARK_MISMATCH, BUILDING_UNAVAILABLE -> {
                final OrphanRetryTracker tracker = orphanTracker(level);
                if (tracker.recordFailure(marker.id())) {
                    ResidenceMarkerService.removeOrphanedMarker(level, marker);
                    tracker.clear(marker.id());
                } else {
                    schedule(level, marker.id(), level.getGameTime() + ORPHAN_RETRY_DELAY);
                }
            }
        }
    }

    private static void scheduleAt(final ServerLevel level, final BlockPos position, final long delay) {
        final long deadline = level.getGameTime() + delay;
        for (final SurvivalResidenceMarker marker : ResidenceMarkerData.get(level).markers()) {
            if (marker.bounds().contains(position)) {
                schedule(level, marker.id(), deadline);
            }
        }
    }

    private static void schedule(final ServerLevel level, final UUID markerId, final long deadline) {
        PENDING.computeIfAbsent(level, ignored -> new MarkerRescanQueue()).schedule(markerId, deadline);
    }

    private static OrphanRetryTracker orphanTracker(final ServerLevel level) {
        return ORPHAN_RETRIES.computeIfAbsent(
                level, ignored -> new OrphanRetryTracker(ORPHAN_REMOVAL_THRESHOLD));
    }
}
