package org.everbuild.unbound.animal;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FenceGateBlock;
import org.everbuild.unbound.marker.AreaBounds;
import org.everbuild.unbound.minecolonies.MineColoniesIntegration;
import org.everbuild.unbound.residence.ResidenceInspector;

/** Discovers the unambiguous physical requirements of a survival cattle and goat pen. */
public final class CowPenInspector {
    private CowPenInspector() {
    }

    public static CowPenInspection inspect(final Level level, final AreaBounds bounds) {
        return inspect(
                bounds,
                chunksAreLoaded(level, bounds),
                position -> level.getBlockState(position).is(MineColoniesIntegration.SURVIVAL_COW_PEN_BLOCK.get()),
                position -> level.getBlockState(position).getBlock() instanceof FenceGateBlock,
                position -> level.getBlockState(position).is(BlockTags.DIRT));
    }

    static CowPenInspection inspect(
            final AreaBounds bounds,
            final boolean areaLoaded,
            final Predicate<BlockPos> isPlaque,
            final Predicate<BlockPos> isGate,
            final Predicate<BlockPos> isPasture) {
        if (bounds.volume() > ResidenceInspector.MAXIMUM_INSPECTION_VOLUME) {
            return result(CowPenInspection.Status.AREA_TOO_LARGE, bounds);
        }
        if (!areaLoaded) {
            return result(CowPenInspection.Status.AREA_NOT_LOADED, bounds);
        }

        final List<BlockPos> plaques = new ArrayList<>();
        final List<BlockPos> gates = new ArrayList<>();
        final List<BlockPos> pasture = new ArrayList<>();
        for (final BlockPos position : BlockPos.betweenClosed(bounds.min(), bounds.max())) {
            if (isPlaque.test(position)) {
                plaques.add(position.immutable());
            }
            if (isGate.test(position)) {
                gates.add(position.immutable());
            }
            if (isPasture.test(position)) {
                pasture.add(position.immutable());
            }
        }

        final CowPenInspection.Status status = plaques.isEmpty()
                ? CowPenInspection.Status.NO_PLAQUE
                : plaques.size() > 1
                        ? CowPenInspection.Status.MULTIPLE_PLAQUES
                        : gates.isEmpty()
                                ? CowPenInspection.Status.NO_GATE
                                : pasture.isEmpty()
                                        ? CowPenInspection.Status.NO_PASTURE
                                        : CowPenInspection.Status.VALID;
        return new CowPenInspection(status, bounds, plaques, gates, pasture);
    }

    private static CowPenInspection result(
            final CowPenInspection.Status status,
            final AreaBounds bounds) {
        return new CowPenInspection(status, bounds, List.of(), List.of(), List.of());
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
