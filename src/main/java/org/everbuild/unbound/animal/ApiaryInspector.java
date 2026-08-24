package org.everbuild.unbound.animal;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BeehiveBlock;
import org.everbuild.unbound.marker.AreaBounds;
import org.everbuild.unbound.minecolonies.MineColoniesIntegration;
import org.everbuild.unbound.residence.ResidenceInspector;

/** Discovers vanilla bee nests and beehives owned by a survival apiary. */
public final class ApiaryInspector {
    private ApiaryInspector() {
    }

    public static ApiaryInspection inspect(final Level level, final AreaBounds bounds) {
        return inspect(
                bounds,
                chunksAreLoaded(level, bounds),
                position -> level.getBlockState(position).is(MineColoniesIntegration.SURVIVAL_APIARY_BLOCK.get()),
                position -> level.getBlockState(position).getBlock() instanceof BeehiveBlock);
    }

    static ApiaryInspection inspect(
            final AreaBounds bounds,
            final boolean areaLoaded,
            final Predicate<BlockPos> isPlaque,
            final Predicate<BlockPos> isHive) {
        if (bounds.volume() > ResidenceInspector.MAXIMUM_INSPECTION_VOLUME) {
            return result(ApiaryInspection.Status.AREA_TOO_LARGE, bounds);
        }
        if (!areaLoaded) {
            return result(ApiaryInspection.Status.AREA_NOT_LOADED, bounds);
        }
        final List<BlockPos> plaques = new ArrayList<>();
        final List<BlockPos> hives = new ArrayList<>();
        for (final BlockPos position : BlockPos.betweenClosed(bounds.min(), bounds.max())) {
            if (isPlaque.test(position)) {
                plaques.add(position.immutable());
            }
            if (isHive.test(position)) {
                hives.add(position.immutable());
            }
        }
        final ApiaryInspection.Status status = plaques.isEmpty()
                ? ApiaryInspection.Status.NO_PLAQUE
                : plaques.size() > 1
                        ? ApiaryInspection.Status.MULTIPLE_PLAQUES
                        : hives.isEmpty() ? ApiaryInspection.Status.NO_HIVES : ApiaryInspection.Status.VALID;
        return new ApiaryInspection(status, bounds, plaques, hives);
    }

    private static ApiaryInspection result(final ApiaryInspection.Status status, final AreaBounds bounds) {
        return new ApiaryInspection(status, bounds, List.of(), List.of());
    }

    private static boolean chunksAreLoaded(final Level level, final AreaBounds bounds) {
        for (int chunkX = SectionPos.blockToSectionCoord(bounds.min().getX());
                chunkX <= SectionPos.blockToSectionCoord(bounds.max().getX());
                chunkX++) {
            for (int chunkZ = SectionPos.blockToSectionCoord(bounds.min().getZ());
                    chunkZ <= SectionPos.blockToSectionCoord(bounds.max().getZ());
                    chunkZ++) {
                if (!level.hasChunk(chunkX, chunkZ)) {
                    return false;
                }
            }
        }
        return true;
    }
}
