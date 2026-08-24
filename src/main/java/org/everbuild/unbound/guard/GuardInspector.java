package org.everbuild.unbound.guard;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import org.everbuild.unbound.marker.AreaBounds;
import org.everbuild.unbound.minecolonies.MineColoniesIntegration;
import org.everbuild.unbound.residence.ResidenceInspector;

/** Finds the single Guard Tower plaque that owns a selected survival structure. */
public final class GuardInspector {
    private GuardInspector() {
    }

    public static GuardInspection inspect(final Level level, final AreaBounds bounds) {
        return inspect(
                bounds,
                chunksAreLoaded(level, bounds),
                position -> level.getBlockState(position).is(MineColoniesIntegration.SURVIVAL_GUARD_BLOCK.get()));
    }

    static GuardInspection inspect(
            final AreaBounds bounds,
            final boolean areaLoaded,
            final Predicate<BlockPos> isGuardPlaque) {
        if (bounds.volume() > ResidenceInspector.MAXIMUM_INSPECTION_VOLUME) {
            return new GuardInspection(GuardInspection.Status.AREA_TOO_LARGE, bounds, List.of());
        }
        if (!areaLoaded) {
            return new GuardInspection(GuardInspection.Status.AREA_NOT_LOADED, bounds, List.of());
        }
        final List<BlockPos> plaques = new ArrayList<>();
        for (final BlockPos position : BlockPos.betweenClosed(bounds.min(), bounds.max())) {
            if (isGuardPlaque.test(position)) {
                plaques.add(position.immutable());
            }
        }
        final GuardInspection.Status status = plaques.isEmpty()
                ? GuardInspection.Status.NO_PLAQUE
                : plaques.size() == 1
                        ? GuardInspection.Status.VALID
                        : GuardInspection.Status.MULTIPLE_PLAQUES;
        return new GuardInspection(status, bounds, plaques);
    }

    private static boolean chunksAreLoaded(final Level level, final AreaBounds bounds) {
        final int minimumChunkX = SectionPos.blockToSectionCoord(bounds.min().getX());
        final int maximumChunkX = SectionPos.blockToSectionCoord(bounds.max().getX());
        final int minimumChunkZ = SectionPos.blockToSectionCoord(bounds.min().getZ());
        final int maximumChunkZ = SectionPos.blockToSectionCoord(bounds.max().getZ());
        for (int chunkX = minimumChunkX; chunkX <= maximumChunkX; chunkX++) {
            for (int chunkZ = minimumChunkZ; chunkZ <= maximumChunkZ; chunkZ++) {
                if (!level.hasChunk(chunkX, chunkZ)) {
                    return false;
                }
            }
        }
        return true;
    }
}
