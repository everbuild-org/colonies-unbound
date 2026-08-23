package org.everbuild.unbound.residence;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import org.everbuild.unbound.marker.AreaBounds;

/** Discovers the MineColonies-relevant resources inside a selected survival residence. */
public final class ResidenceInspector {
    /** Keeps synchronous inspection comfortably below a full 128-cubed selection. */
    public static final long MAXIMUM_INSPECTION_VOLUME = 262_144L;

    private ResidenceInspector() {
    }

    public static ResidenceInspection inspect(final Level level, final AreaBounds bounds) {
        return inspect(bounds, chunksAreLoaded(level, bounds), pos -> isBedHead(level.getBlockState(pos)));
    }

    /** Pure inspection seam used by tests and future cached/block-snapshot adapters. */
    public static ResidenceInspection inspect(
            final AreaBounds bounds,
            final boolean areaLoaded,
            final Predicate<BlockPos> isBedHead) {
        if (bounds.volume() > MAXIMUM_INSPECTION_VOLUME) {
            return new ResidenceInspection(ResidenceInspection.Status.AREA_TOO_LARGE, bounds, List.of());
        }
        if (!areaLoaded) {
            return new ResidenceInspection(ResidenceInspection.Status.AREA_NOT_LOADED, bounds, List.of());
        }

        final List<BlockPos> bedHeads = new ArrayList<>();
        for (final BlockPos position : BlockPos.betweenClosed(bounds.min(), bounds.max())) {
            if (isBedHead.test(position)) {
                bedHeads.add(position.immutable());
            }
        }

        final ResidenceInspection.Status status = bedHeads.isEmpty()
                ? ResidenceInspection.Status.NO_BEDS
                : ResidenceInspection.Status.VALID;
        return new ResidenceInspection(status, bounds, bedHeads);
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

    private static boolean isBedHead(final BlockState state) {
        return state.getBlock() instanceof BedBlock && state.getValue(BedBlock.PART) == BedPart.HEAD;
    }
}
