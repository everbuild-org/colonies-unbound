package org.everbuild.unbound.minecolonies;

import com.minecolonies.api.colony.IColony;
import com.minecolonies.api.inventory.InventoryCitizen;
import com.minecolonies.api.tileentities.AbstractTileEntityRack;
import com.minecolonies.api.tileentities.AbstractTileEntityWareHouse;
import com.minecolonies.api.tileentities.MinecoloniesTileEntities;
import com.minecolonies.api.util.InventoryUtils;
import com.minecolonies.api.util.Tuple;
import com.minecolonies.core.blocks.BlockMinecoloniesRack;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingWareHouse;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

/** Native Warehouse whose scanned racks are exposed through the courier-facing warehouse tile contract. */
public final class SurvivalWarehouseBuilding extends BuildingWareHouse implements SurvivalCraftingBuilding {
    private WarehouseInventoryAdapter inventoryAdapter;
    public SurvivalWarehouseBuilding(final IColony colony, final BlockPos position) { super(colony, position); }
    @Override public boolean canBeBuiltByBuilder(final int level) { return false; }
    @Override public boolean canDeconstruct() { return false; }
    @Override public boolean isBuilt() { return getBuildingLevel() > 0; }
    @Override public void requestUpgrade(final Player player, final BlockPos builder) { }
    @Override public void requestRepair(final BlockPos builder) { }
    @Override public void registerWorkstation(final Level level, final BlockPos position) {
        addContainerPosition(position); registerBlockPosition(level.getBlockState(position).getBlock(), position, level);
    }
    @Override public void removeWorkstation(final BlockPos position) {
        final BlockEntity entity = getColony().getWorld().getBlockEntity(position);
        if (entity instanceof AbstractTileEntityRack rack) rack.setInWarehouse(false);
        removeContainerPosition(position);
    }
    @Override public AbstractTileEntityWareHouse getTileEntity() {
        if (inventoryAdapter == null) inventoryAdapter = new WarehouseInventoryAdapter();
        inventoryAdapter.setLevel(getColony().getWorld()); return inventoryAdapter;
    }

    private List<AbstractTileEntityRack> racks() {
        final List<AbstractTileEntityRack> racks = new ArrayList<>();
        for (final BlockPos position : getContainers()) {
            if (getColony().getWorld().getBlockEntity(position) instanceof AbstractTileEntityRack rack) racks.add(rack);
        }
        return racks;
    }

    private final class WarehouseInventoryAdapter extends AbstractTileEntityWareHouse {
        @SuppressWarnings("unchecked")
        private WarehouseInventoryAdapter() {
            super((BlockEntityType<? extends AbstractTileEntityWareHouse>) MinecoloniesTileEntities.WAREHOUSE.get(),
                    SurvivalWarehouseBuilding.this.getPosition(),
                    SurvivalWarehouseBuilding.this.getColony().getWorld()
                            .getBlockState(SurvivalWarehouseBuilding.this.getPosition()));
        }
        @Override public boolean hasMatchingItemStackInWarehouse(final Predicate<ItemStack> predicate, final int count) {
            int found = 0; for (final AbstractTileEntityRack rack : racks()) {
                found += rack.getItemCount(predicate); if (found >= count) return true;
            } return false;
        }
        @Override public boolean hasMatchingItemStackInWarehouse(final ItemStack stack, final int count, final boolean ignoreNbt) {
            return hasMatchingItemStackInWarehouse(stack, count, ignoreNbt, true, 0);
        }
        @Override public boolean hasMatchingItemStackInWarehouse(
                final ItemStack stack, final int count, final boolean ignoreNbt, final int leftOver) {
            return hasMatchingItemStackInWarehouse(stack, count, ignoreNbt, true, leftOver);
        }
        @Override public boolean hasMatchingItemStackInWarehouse(
                final ItemStack stack, final int count, final boolean ignoreNbt,
                final boolean ignoreDamage, final int leftOver) {
            int found = -leftOver; for (final AbstractTileEntityRack rack : racks()) {
                found += rack.getCount(stack, ignoreDamage, ignoreNbt); if (found >= count) return true;
            } return false;
        }
        @Override public List<Tuple<ItemStack, BlockPos>> getMatchingItemStacksInWarehouse(final Predicate<ItemStack> predicate) {
            final List<Tuple<ItemStack, BlockPos>> matches = new ArrayList<>();
            for (final AbstractTileEntityRack rack : racks()) {
                for (final ItemStack stack : InventoryUtils.filterItemHandler(rack.getInventory(), predicate)) {
                    matches.add(new Tuple<>(stack, rack.getBlockPos()));
                }
            } return matches;
        }
        @Override public void dumpInventoryIntoWareHouse(final InventoryCitizen inventory) {
            for (int slot = 0; slot < inventory.getSlots(); slot++) {
                if (inventory.getStackInSlot(slot).isEmpty()) continue;
                final AbstractTileEntityRack target = racks().stream().filter(rack -> rack.getFreeSlots() > 0).findFirst().orElse(null);
                if (target == null) return;
                InventoryUtils.transferItemStackIntoNextBestSlotInItemHandler(inventory, slot, target.getItemHandlerCap());
            }
        }
    }
}
