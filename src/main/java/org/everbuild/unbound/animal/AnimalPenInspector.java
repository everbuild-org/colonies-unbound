package org.everbuild.unbound.animal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceGateBlock;
import org.everbuild.unbound.marker.AreaBounds;
import org.everbuild.unbound.residence.ResidenceInspector;

/** Discovers plaque, gate, and pasture requirements shared by native herder adapters. */
public final class AnimalPenInspector {
    private AnimalPenInspector() {
    }

    public static AnimalPenInspection inspect(
            final Level level,
            final AreaBounds bounds,
            final Block expectedPlaque) {
        return inspect(
                bounds,
                chunksAreLoaded(level, bounds),
                position -> level.getBlockState(position).is(expectedPlaque),
                position -> level.getBlockState(position).getBlock() instanceof FenceGateBlock,
                position -> level.getBlockState(position).is(BlockTags.DIRT));
    }

    public static AnimalPenInspection inspect(
            final Level level,
            final AreaBounds bounds,
            final Collection<? extends Block> expectedPlaques) {
        return inspect(
                bounds,
                chunksAreLoaded(level, bounds),
                position -> expectedPlaques.contains(level.getBlockState(position).getBlock()),
                position -> level.getBlockState(position).getBlock() instanceof FenceGateBlock,
                position -> level.getBlockState(position).is(BlockTags.DIRT));
    }

    static AnimalPenInspection inspect(
            final AreaBounds bounds,
            final boolean areaLoaded,
            final Predicate<BlockPos> isPlaque,
            final Predicate<BlockPos> isGate,
            final Predicate<BlockPos> isPasture) {
        if (bounds.volume() > ResidenceInspector.MAXIMUM_INSPECTION_VOLUME) {
            return result(AnimalPenInspection.Status.AREA_TOO_LARGE, bounds);
        }
        if (!areaLoaded) {
            return result(AnimalPenInspection.Status.AREA_NOT_LOADED, bounds);
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

        final AnimalPenInspection.Status status = plaques.isEmpty()
                ? AnimalPenInspection.Status.NO_PLAQUE
                : plaques.size() > 1
                        ? AnimalPenInspection.Status.MULTIPLE_PLAQUES
                        : gates.isEmpty()
                                ? AnimalPenInspection.Status.NO_GATE
                                : pasture.isEmpty()
                                        ? AnimalPenInspection.Status.NO_PASTURE
                                        : AnimalPenInspection.Status.VALID;
        return new AnimalPenInspection(status, bounds, plaques, gates, pasture);
    }

    private static AnimalPenInspection result(
            final AnimalPenInspection.Status status,
            final AreaBounds bounds) {
        return new AnimalPenInspection(status, bounds, List.of(), List.of(), List.of());
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
