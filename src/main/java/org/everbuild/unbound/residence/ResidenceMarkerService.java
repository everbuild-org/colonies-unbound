package org.everbuild.unbound.residence;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.colony.IColonyManager;
import com.minecolonies.api.colony.permissions.Action;
import com.minecolonies.api.colony.buildings.IBuilding;
import com.minecolonies.core.colony.buildings.modules.BedHandlingModule;
import com.minecolonies.core.colony.buildings.modules.BuildingModules;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.everbuild.unbound.marker.AreaBounds;
import org.everbuild.unbound.marker.CommittedWorksiteMark;
import org.everbuild.unbound.marker.MarkerSelection;
import org.everbuild.unbound.marker.MarkerType;
import org.everbuild.unbound.marker.WorksitePoi;
import org.everbuild.unbound.marker.WorksitePoiType;
import org.everbuild.unbound.minecolonies.SurvivalResidenceTileEntity;
import org.everbuild.unbound.minecolonies.SurvivalResidenceBuilding;

/** Validates MineColonies ownership before committing a Residence marker to world data. */
public final class ResidenceMarkerService {
    private ResidenceMarkerService() {
    }

    public static Result register(
            final ServerLevel level,
            final ServerPlayer player,
            final MarkerSelection selection,
            final ResidenceInspection inspection) {
        final AreaBounds bounds = inspection.bounds();
        final IColony colony = IColonyManager.getInstance().getColonyByPosFromWorld(level, bounds.min());
        if (colony == null) {
            return Result.OUTSIDE_COLONY;
        }
        if (!colony.getPermissions().hasPermission(player, Action.MANAGE_HUTS)) {
            return Result.NO_PERMISSION;
        }
        if (!cornersBelongTo(level, bounds, colony)) {
            return Result.CROSSES_COLONY_BORDER;
        }

        final BlockPos plaque = inspection.plaquePositions().getFirst();
        final IBuilding building = IColonyManager.getInstance().getBuilding(level, plaque);
        if (!(building instanceof SurvivalResidenceBuilding)
                || !(level.getBlockEntity(plaque) instanceof SurvivalResidenceTileEntity residenceTile)) {
            return Result.PLAQUE_NOT_REGISTERED;
        }

        final UUID markerId = residenceTile.committedMark() == null
                ? selection.markerId()
                : residenceTile.committedMark().id();

        building.setCorners(bounds.min(), bounds.max());
        building.setBuildingLevel(1);
        final BedHandlingModule bedModule = building.getModule(BuildingModules.BED);
        for (final BlockPos oldBed : List.copyOf(bedModule.getRegisteredBlocks())) {
            bedModule.removeBed(oldBed);
        }
        for (final BlockPos bed : inspection.bedHeads()) {
            bedModule.onBlockPlacedInBuilding(level.getBlockState(bed), bed, level);
        }
        building.markDirty();
        colony.markDirty();

        final List<WorksitePoi> existingPois = residenceTile.committedMark() == null
                ? List.of()
                : residenceTile.committedMark().pois();
        final List<WorksitePoi> retainedPois = existingPois.stream()
                .filter(poi -> bounds.contains(poi.position()))
                .toList();
        residenceTile.setCommittedMark(new CommittedWorksiteMark(
                markerId,
                MarkerType.RESIDENCE,
                bounds,
                mergeBedPois(retainedPois, inspection.bedHeads())));

        ResidenceMarkerData.get(level).put(new SurvivalResidenceMarker(
                markerId,
                colony.getID(),
                player.getUUID(),
                bounds,
                inspection.bedHeads(),
                level.getGameTime()));
        return Result.SAVED;
    }

    public static RemovalResult removeCommittedMark(
            final ServerLevel level,
            final ServerPlayer player,
            final SurvivalResidenceTileEntity residenceTile) {
        if (residenceTile.committedMark() == null) {
            return RemovalResult.NO_MARK;
        }
        final IColony colony = IColonyManager.getInstance()
                .getColonyByPosFromWorld(level, residenceTile.getBlockPos());
        if (colony == null) {
            return RemovalResult.OUTSIDE_COLONY;
        }
        if (!colony.getPermissions().hasPermission(player, Action.MANAGE_HUTS)) {
            return RemovalResult.NO_PERMISSION;
        }

        ResidenceMarkerData.get(level).remove(residenceTile.committedMark().id());
        residenceTile.clearCommittedMark();
        return RemovalResult.REMOVED;
    }

    /** Refreshes accurately discoverable POIs after block changes in a committed residence. */
    public static ReconcileResult reconcilePois(
            final ServerLevel level,
            final SurvivalResidenceMarker marker) {
        final ResidenceInspection inspection = ResidenceInspector.inspect(level, marker.bounds());
        if (inspection.status() == ResidenceInspection.Status.AREA_NOT_LOADED) {
            return ReconcileResult.AREA_NOT_LOADED;
        }
        if (inspection.plaquePositions().isEmpty()) {
            return ReconcileResult.MISSING_PLAQUE;
        }
        if (inspection.plaquePositions().size() > 1) {
            return ReconcileResult.MULTIPLE_PLAQUES;
        }

        final BlockPos plaque = inspection.plaquePositions().getFirst();
        if (!(level.getBlockEntity(plaque) instanceof SurvivalResidenceTileEntity residenceTile)) {
            return ReconcileResult.MISSING_PLAQUE;
        }
        if (residenceTile.committedMark() == null
                || !residenceTile.committedMark().id().equals(marker.id())) {
            return ReconcileResult.MARK_MISMATCH;
        }
        final IBuilding building = IColonyManager.getInstance().getBuilding(level, plaque);
        final IColony colony = IColonyManager.getInstance().getColonyByPosFromWorld(level, plaque);
        if (!(building instanceof SurvivalResidenceBuilding)
                || colony == null
                || colony.getID() != marker.colonyId()) {
            return ReconcileResult.BUILDING_UNAVAILABLE;
        }

        final BedHandlingModule bedModule = building.getModule(BuildingModules.BED);
        final Set<BlockPos> previousBeds = Set.copyOf(bedModule.getRegisteredBlocks());
        final Set<BlockPos> discoveredBeds = Set.copyOf(inspection.bedHeads());
        final List<WorksitePoi> discoveredPois = mergeBedPois(
                residenceTile.committedMark().pois(), inspection.bedHeads());
        if (previousBeds.equals(discoveredBeds)
                && Set.copyOf(marker.bedHeads()).equals(discoveredBeds)
                && Set.copyOf(residenceTile.committedMark().pois()).equals(Set.copyOf(discoveredPois))) {
            return ReconcileResult.UNCHANGED;
        }

        for (final BlockPos removedBed : previousBeds) {
            if (!discoveredBeds.contains(removedBed)) {
                bedModule.removeBed(removedBed);
            }
        }
        for (final BlockPos addedBed : discoveredBeds) {
            if (!previousBeds.contains(addedBed)) {
                bedModule.onBlockPlacedInBuilding(level.getBlockState(addedBed), addedBed, level);
            }
        }

        residenceTile.setCommittedMark(new CommittedWorksiteMark(
                marker.id(), MarkerType.RESIDENCE, marker.bounds(), discoveredPois));

        building.markDirty();
        colony.markDirty();
        ResidenceMarkerData.get(level).put(new SurvivalResidenceMarker(
                marker.id(),
                marker.colonyId(),
                marker.ownerId(),
                marker.bounds(),
                inspection.bedHeads(),
                level.getGameTime()));
        return ReconcileResult.UPDATED;
    }

    public static void removeOrphanedMarker(
            final ServerLevel level,
            final SurvivalResidenceMarker marker) {
        ResidenceMarkerData.get(level).remove(marker.id());
        final ResidenceInspection inspection = ResidenceInspector.inspect(level, marker.bounds());
        for (final BlockPos plaque : inspection.plaquePositions()) {
            if (level.getBlockEntity(plaque) instanceof SurvivalResidenceTileEntity residenceTile
                    && residenceTile.committedMark() != null
                    && residenceTile.committedMark().id().equals(marker.id())) {
                residenceTile.clearCommittedMark();
            }
        }
    }

    public static PointEditResult editPoint(
            final ServerLevel level,
            final ServerPlayer player,
            final BlockPos position,
            final WorksitePoiType poiType,
            final boolean remove) {
        final List<SurvivalResidenceMarker> containingMarkers = ResidenceMarkerData.get(level).markers().stream()
                .filter(marker -> marker.bounds().contains(position))
                .toList();
        if (containingMarkers.isEmpty()) {
            return PointEditResult.NO_COMMITTED_VOLUME;
        }
        if (containingMarkers.size() > 1) {
            return PointEditResult.AMBIGUOUS_VOLUME;
        }

        final SurvivalResidenceMarker marker = containingMarkers.getFirst();
        final ResidenceInspection inspection = ResidenceInspector.inspect(level, marker.bounds());
        if (inspection.plaquePositions().size() != 1) {
            return PointEditResult.MARK_UNAVAILABLE;
        }
        final BlockPos plaque = inspection.plaquePositions().getFirst();
        if (!(level.getBlockEntity(plaque) instanceof SurvivalResidenceTileEntity residenceTile)
                || residenceTile.committedMark() == null
                || !residenceTile.committedMark().id().equals(marker.id())) {
            return PointEditResult.MARK_UNAVAILABLE;
        }
        final IColony colony = IColonyManager.getInstance().getColonyByPosFromWorld(level, plaque);
        if (colony == null || colony.getID() != marker.colonyId()) {
            return PointEditResult.MARK_UNAVAILABLE;
        }
        if (!colony.getPermissions().hasPermission(player, Action.MANAGE_HUTS)) {
            return PointEditResult.NO_PERMISSION;
        }

        final WorksitePoi editedPoi = new WorksitePoi(poiType, position);
        final List<WorksitePoi> pois = new ArrayList<>(residenceTile.committedMark().pois());
        if (remove) {
            if (!pois.remove(editedPoi)) {
                return PointEditResult.POINT_NOT_FOUND;
            }
        } else if (pois.contains(editedPoi)) {
            return PointEditResult.ALREADY_PRESENT;
        } else {
            pois.add(editedPoi);
        }

        final CommittedWorksiteMark committedMark = residenceTile.committedMark();
        residenceTile.setCommittedMark(new CommittedWorksiteMark(
                committedMark.id(), committedMark.type(), committedMark.bounds(), pois));
        return remove ? PointEditResult.REMOVED : PointEditResult.ADDED;
    }

    private static List<WorksitePoi> bedPois(final List<BlockPos> bedHeads) {
        return bedHeads.stream()
                .map(position -> new WorksitePoi(WorksitePoiType.BED, position))
                .toList();
    }

    static List<WorksitePoi> mergeBedPois(
            final List<WorksitePoi> existingPois,
            final List<BlockPos> bedHeads) {
        final List<WorksitePoi> merged = new ArrayList<>();
        existingPois.stream()
                .filter(poi -> poi.type() != WorksitePoiType.BED)
                .forEach(merged::add);
        merged.addAll(bedPois(bedHeads));
        return List.copyOf(merged);
    }

    private static boolean cornersBelongTo(
            final ServerLevel level,
            final AreaBounds bounds,
            final IColony expectedColony) {
        final List<BlockPos> corners = List.of(
                bounds.min(), bounds.max(),
                new BlockPos(bounds.min().getX(), bounds.min().getY(), bounds.max().getZ()),
                new BlockPos(bounds.min().getX(), bounds.max().getY(), bounds.min().getZ()),
                new BlockPos(bounds.max().getX(), bounds.min().getY(), bounds.min().getZ()),
                new BlockPos(bounds.min().getX(), bounds.max().getY(), bounds.max().getZ()),
                new BlockPos(bounds.max().getX(), bounds.min().getY(), bounds.max().getZ()),
                new BlockPos(bounds.max().getX(), bounds.max().getY(), bounds.min().getZ()));
        return corners.stream().allMatch(pos -> {
            final IColony colony = IColonyManager.getInstance().getColonyByPosFromWorld(level, pos);
            return colony != null && colony.getID() == expectedColony.getID();
        });
    }

    public enum Result {
        SAVED,
        OUTSIDE_COLONY,
        CROSSES_COLONY_BORDER,
        NO_PERMISSION,
        PLAQUE_NOT_REGISTERED
    }

    public enum RemovalResult {
        REMOVED,
        NO_MARK,
        OUTSIDE_COLONY,
        NO_PERMISSION
    }

    public enum ReconcileResult {
        UPDATED,
        UNCHANGED,
        AREA_NOT_LOADED,
        MISSING_PLAQUE,
        MULTIPLE_PLAQUES,
        MARK_MISMATCH,
        BUILDING_UNAVAILABLE
    }

    public enum PointEditResult {
        ADDED,
        REMOVED,
        ALREADY_PRESENT,
        POINT_NOT_FOUND,
        NO_COMMITTED_VOLUME,
        AMBIGUOUS_VOLUME,
        NO_PERMISSION,
        MARK_UNAVAILABLE
    }
}
