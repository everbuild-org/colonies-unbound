package org.everbuild.unbound.marker;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

/** Inclusive block bounds selected by two marker clicks. */
public record AreaBounds(BlockPos min, BlockPos max) {
    public static AreaBounds between(final BlockPos first, final BlockPos second) {
        return new AreaBounds(
                new BlockPos(
                        Math.min(first.getX(), second.getX()),
                        Math.min(first.getY(), second.getY()),
                        Math.min(first.getZ(), second.getZ())),
                new BlockPos(
                        Math.max(first.getX(), second.getX()),
                        Math.max(first.getY(), second.getY()),
                        Math.max(first.getZ(), second.getZ())));
    }

    public int sizeX() {
        return max.getX() - min.getX() + 1;
    }

    public int sizeY() {
        return max.getY() - min.getY() + 1;
    }

    public int sizeZ() {
        return max.getZ() - min.getZ() + 1;
    }

    public long volume() {
        return (long) sizeX() * sizeY() * sizeZ();
    }

    public boolean fitsWithin(final int maximumAxisLength) {
        return sizeX() <= maximumAxisLength
                && sizeY() <= maximumAxisLength
                && sizeZ() <= maximumAxisLength;
    }

    public boolean contains(final BlockPos position) {
        return position.getX() >= min.getX() && position.getX() <= max.getX()
                && position.getY() >= min.getY() && position.getY() <= max.getY()
                && position.getZ() >= min.getZ() && position.getZ() <= max.getZ();
    }

    public boolean intersectsChunk(final int minimumX, final int minimumZ, final int maximumX, final int maximumZ) {
        return max.getX() >= minimumX && min.getX() <= maximumX
                && max.getZ() >= minimumZ && min.getZ() <= maximumZ;
    }

    public AABB asAabb() {
        return new AABB(
                min.getX(), min.getY(), min.getZ(),
                max.getX() + 1.0, max.getY() + 1.0, max.getZ() + 1.0);
    }
}
