package org.everbuild.unbound.workplace;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import org.everbuild.unbound.marker.AreaBounds;
import org.everbuild.unbound.residence.ResidenceInspector;

/** Locates one crafting plaque and all compatible workstations in its volume. */
public final class CraftingWorkplaceInspector {
    private CraftingWorkplaceInspector() { }

    public static CraftingWorkplaceInspection inspect(final Level level, final AreaBounds bounds) {
        if (bounds.volume() > ResidenceInspector.MAXIMUM_INSPECTION_VOLUME) {
            return empty(CraftingWorkplaceInspection.Status.AREA_TOO_LARGE, bounds);
        }
        if (!chunksAreLoaded(level, bounds)) {
            return empty(CraftingWorkplaceInspection.Status.AREA_NOT_LOADED, bounds);
        }
        final List<CraftingWorkplaceDefinition> definitions = CraftingWorkplaceDefinition.all();
        final List<BlockPos> plaques = new ArrayList<>();
        CraftingWorkplaceDefinition selected = null;
        for (final BlockPos position : BlockPos.betweenClosed(bounds.min(), bounds.max())) {
            for (final CraftingWorkplaceDefinition definition : definitions) {
                if (level.getBlockState(position).is(definition.plaque().get())) {
                    plaques.add(position.immutable());
                    selected = definition;
                }
            }
        }
        if (plaques.isEmpty()) return empty(CraftingWorkplaceInspection.Status.NO_PLAQUE, bounds);
        if (plaques.size() > 1) {
            return new CraftingWorkplaceInspection(
                    CraftingWorkplaceInspection.Status.MULTIPLE_PLAQUES, bounds, plaques, List.of(), null);
        }
        final List<BlockPos> workstations = new ArrayList<>();
        for (final BlockPos position : BlockPos.betweenClosed(bounds.min(), bounds.max())) {
            if (workstations.size() < selected.maximumWorkstations()
                    && selected.isWorkstation().test(level, position)) {
                workstations.add(position.immutable());
            }
        }
        return new CraftingWorkplaceInspection(
                workstations.isEmpty() ? CraftingWorkplaceInspection.Status.NO_WORKSTATION
                        : CraftingWorkplaceInspection.Status.VALID,
                bounds, plaques, workstations, selected);
    }

    private static CraftingWorkplaceInspection empty(
            final CraftingWorkplaceInspection.Status status, final AreaBounds bounds) {
        return new CraftingWorkplaceInspection(status, bounds, List.of(), List.of(), null);
    }

    private static boolean chunksAreLoaded(final Level level, final AreaBounds bounds) {
        for (int x = SectionPos.blockToSectionCoord(bounds.min().getX());
                x <= SectionPos.blockToSectionCoord(bounds.max().getX()); x++) {
            for (int z = SectionPos.blockToSectionCoord(bounds.min().getZ());
                    z <= SectionPos.blockToSectionCoord(bounds.max().getZ()); z++) {
                if (!level.hasChunk(x, z)) return false;
            }
        }
        return true;
    }
}
