package org.everbuild.unbound.minecolonies;

import com.minecolonies.api.colony.buildings.registry.BuildingEntry;
import com.minecolonies.core.colony.buildings.modules.BuildingModules;
import com.minecolonies.core.colony.buildings.modules.HomeBuildingModule;
import com.minecolonies.core.colony.buildings.moduleviews.LivingBuildingModuleView;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingCook;
import com.minecolonies.core.colony.buildings.views.EmptyView;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingGuardTower;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.everbuild.unbound.ColoniesUnbound;

/** All MineColonies-internal coupling for the pinned 1.21.1 integration. */
public final class MineColoniesIntegration {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ColoniesUnbound.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ColoniesUnbound.MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ColoniesUnbound.MOD_ID);
    private static final DeferredRegister<BuildingEntry> BUILDINGS =
            DeferredRegister.create(
                    ResourceLocation.fromNamespaceAndPath("minecolonies", "buildings"),
                    ColoniesUnbound.MOD_ID);

    public static final DeferredBlock<SurvivalResidenceBlock> SURVIVAL_RESIDENCE_BLOCK = BLOCKS.registerBlock(
            "survival_residence_plaque",
            SurvivalResidenceBlock::new,
            BlockBehaviour.Properties.of().strength(3.0F).sound(SoundType.WOOD));
    public static final DeferredItem<BlockItem> SURVIVAL_RESIDENCE_ITEM = ITEMS.registerSimpleBlockItem(
            SURVIVAL_RESIDENCE_BLOCK, new Item.Properties().stacksTo(1));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SurvivalResidenceTileEntity>>
            SURVIVAL_RESIDENCE_TILE = BLOCK_ENTITIES.register(
                    "survival_residence_plaque",
                    () -> BlockEntityType.Builder.of(
                                    MineColoniesIntegration::createTile,
                                    SURVIVAL_RESIDENCE_BLOCK.get())
                            .build(null));

    public static final DeferredBlock<SurvivalCookBlock> SURVIVAL_COOK_BLOCK = BLOCKS.registerBlock(
            "survival_cook_plaque",
            SurvivalCookBlock::new,
            BlockBehaviour.Properties.of().strength(3.0F).sound(SoundType.WOOD));
    public static final DeferredItem<BlockItem> SURVIVAL_COOK_ITEM = ITEMS.registerSimpleBlockItem(
            SURVIVAL_COOK_BLOCK, new Item.Properties().stacksTo(1));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SurvivalCookTileEntity>>
            SURVIVAL_COOK_TILE = BLOCK_ENTITIES.register(
                    "survival_cook_plaque",
                    () -> BlockEntityType.Builder.of(
                                    SurvivalCookTileEntity::new,
                                    SURVIVAL_COOK_BLOCK.get())
                            .build(null));

    public static final DeferredBlock<SurvivalGuardBlock> SURVIVAL_GUARD_BLOCK = BLOCKS.registerBlock(
            "survival_guard_plaque",
            SurvivalGuardBlock::new,
            BlockBehaviour.Properties.of().strength(3.0F).sound(SoundType.WOOD));
    public static final DeferredItem<BlockItem> SURVIVAL_GUARD_ITEM = ITEMS.registerSimpleBlockItem(
            SURVIVAL_GUARD_BLOCK, new Item.Properties().stacksTo(1));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SurvivalGuardTileEntity>>
            SURVIVAL_GUARD_TILE = BLOCK_ENTITIES.register(
                    "survival_guard_plaque",
                    () -> BlockEntityType.Builder.of(
                                    SurvivalGuardTileEntity::new,
                                    SURVIVAL_GUARD_BLOCK.get())
                            .build(null));

    public static final DeferredBlock<SurvivalCowPenBlock> SURVIVAL_COW_PEN_BLOCK = BLOCKS.registerBlock(
            "survival_cow_pen_plaque",
            SurvivalCowPenBlock::new,
            BlockBehaviour.Properties.of().strength(3.0F).sound(SoundType.WOOD));
    public static final DeferredItem<BlockItem> SURVIVAL_COW_PEN_ITEM = ITEMS.registerSimpleBlockItem(
            SURVIVAL_COW_PEN_BLOCK, new Item.Properties().stacksTo(1));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SurvivalCowPenTileEntity>>
            SURVIVAL_COW_PEN_TILE = BLOCK_ENTITIES.register(
                    "survival_cow_pen_plaque",
                    () -> BlockEntityType.Builder.of(
                                    SurvivalCowPenTileEntity::new,
                                    SURVIVAL_COW_PEN_BLOCK.get())
                            .build(null));

    public static final DeferredBlock<SurvivalSheepPenBlock> SURVIVAL_SHEEP_PEN_BLOCK = BLOCKS.registerBlock(
            "survival_sheep_pen_plaque",
            SurvivalSheepPenBlock::new,
            BlockBehaviour.Properties.of().strength(3.0F).sound(SoundType.WOOD));
    public static final DeferredItem<BlockItem> SURVIVAL_SHEEP_PEN_ITEM = ITEMS.registerSimpleBlockItem(
            SURVIVAL_SHEEP_PEN_BLOCK, new Item.Properties().stacksTo(1));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SurvivalSheepPenTileEntity>>
            SURVIVAL_SHEEP_PEN_TILE = BLOCK_ENTITIES.register(
                    "survival_sheep_pen_plaque",
                    () -> BlockEntityType.Builder.of(
                                    SurvivalSheepPenTileEntity::new,
                                    SURVIVAL_SHEEP_PEN_BLOCK.get())
                            .build(null));

    public static final BuildingEntry.ModuleProducer<SurvivalLivingBuildingModule, LivingBuildingModuleView>
            SURVIVAL_LIVING = new BuildingEntry.ModuleProducer<>(
                    ColoniesUnbound.MOD_ID + ":survival_living",
                    SurvivalLivingBuildingModule::new,
                    () -> LivingBuildingModuleView::new);

    public static final DeferredHolder<BuildingEntry, BuildingEntry> SURVIVAL_RESIDENCE = BUILDINGS.register(
            "survival_residence",
            () -> new BuildingEntry.Builder()
                    .setRegistryName(ResourceLocation.fromNamespaceAndPath(
                            ColoniesUnbound.MOD_ID, "survival_residence"))
                    .setBuildingBlock(SURVIVAL_RESIDENCE_BLOCK.get())
                    .setBuildingProducer((colony, position) -> new SurvivalResidenceBuilding(colony, position))
                    .setBuildingViewProducer(() -> HomeBuildingModule.View::new)
                    .addBuildingModuleProducer(BuildingModules.HOME)
                    .addBuildingModuleProducer(SURVIVAL_LIVING)
                    .addBuildingModuleProducer(BuildingModules.BED)
                    .createBuildingEntry());

    public static final DeferredHolder<BuildingEntry, BuildingEntry> SURVIVAL_COOK = BUILDINGS.register(
            "survival_cook",
            () -> new BuildingEntry.Builder()
                    .setRegistryName(ResourceLocation.fromNamespaceAndPath(
                            ColoniesUnbound.MOD_ID, "survival_cook"))
                    .setBuildingBlock(SURVIVAL_COOK_BLOCK.get())
                    .setBuildingProducer(SurvivalCookBuilding::new)
                    .setBuildingViewProducer(() -> BuildingCook.View::new)
                    .addBuildingModuleProducer(BuildingModules.COOK_WORK)
                    .addBuildingModuleProducer(BuildingModules.FURNACE)
                    .addBuildingModuleProducer(BuildingModules.ITEMLIST_FUEL)
                    .addBuildingModuleProducer(BuildingModules.RESTAURANT_MENU)
                    .addBuildingModuleProducer(BuildingModules.STATS_MODULE)
                    .createBuildingEntry());

    public static final DeferredHolder<BuildingEntry, BuildingEntry> SURVIVAL_GUARD = BUILDINGS.register(
            "survival_guard",
            () -> new BuildingEntry.Builder()
                    .setRegistryName(ResourceLocation.fromNamespaceAndPath(
                            ColoniesUnbound.MOD_ID, "survival_guard"))
                    .setBuildingBlock(SURVIVAL_GUARD_BLOCK.get())
                    .setBuildingProducer(SurvivalGuardBuilding::new)
                    .setBuildingViewProducer(() -> BuildingGuardTower.View::new)
                    .addBuildingModuleProducer(BuildingModules.KNIGHT_TOWER_WORK)
                    .addBuildingModuleProducer(BuildingModules.RANGER_TOWER_WORK)
                    .addBuildingModuleProducer(BuildingModules.MARKSMAN_TOWER_WORK)
                    .addBuildingModuleProducer(BuildingModules.HUSCARL_TOWER_WORK)
                    .addBuildingModuleProducer(BuildingModules.GUARD_TOOL)
                    .addBuildingModuleProducer(BuildingModules.GUARD_ENTITY_LIST)
                    .addBuildingModuleProducer(BuildingModules.GUARD_SETTINGS)
                    .addBuildingModuleProducer(BuildingModules.MIN_STOCK)
                    .addBuildingModuleProducer(BuildingModules.BED)
                    .addBuildingModuleProducer(BuildingModules.STATS_MODULE)
                    .createBuildingEntry());

    public static final DeferredHolder<BuildingEntry, BuildingEntry> SURVIVAL_COW_PEN = BUILDINGS.register(
            "survival_cow_pen",
            () -> new BuildingEntry.Builder()
                    .setRegistryName(ResourceLocation.fromNamespaceAndPath(
                            ColoniesUnbound.MOD_ID, "survival_cow_pen"))
                    .setBuildingBlock(SURVIVAL_COW_PEN_BLOCK.get())
                    .setBuildingProducer(SurvivalCowPenBuilding::new)
                    .setBuildingViewProducer(() -> EmptyView::new)
                    .addBuildingModuleProducer(BuildingModules.COWHERDER_WORK)
                    .addBuildingModuleProducer(BuildingModules.COWHERDER_HERDING)
                    .addBuildingModuleProducer(BuildingModules.COWHERDER_SETTINGS)
                    .addBuildingModuleProducer(BuildingModules.MIN_STOCK)
                    .addBuildingModuleProducer(BuildingModules.STATS_MODULE)
                    .createBuildingEntry());

    public static final DeferredHolder<BuildingEntry, BuildingEntry> SURVIVAL_SHEEP_PEN = BUILDINGS.register(
            "survival_sheep_pen",
            () -> new BuildingEntry.Builder()
                    .setRegistryName(ResourceLocation.fromNamespaceAndPath(
                            ColoniesUnbound.MOD_ID, "survival_sheep_pen"))
                    .setBuildingBlock(SURVIVAL_SHEEP_PEN_BLOCK.get())
                    .setBuildingProducer(SurvivalSheepPenBuilding::new)
                    .setBuildingViewProducer(() -> EmptyView::new)
                    .addBuildingModuleProducer(BuildingModules.SHEPERD_WORK)
                    .addBuildingModuleProducer(BuildingModules.SHEPERD_HERDING)
                    .addBuildingModuleProducer(BuildingModules.SHEPERD_SETTINGS)
                    .addBuildingModuleProducer(BuildingModules.MIN_STOCK)
                    .addBuildingModuleProducer(BuildingModules.STATS_MODULE)
                    .createBuildingEntry());

    private MineColoniesIntegration() {
    }

    private static SurvivalResidenceTileEntity createTile(final BlockPos position, final BlockState state) {
        return new SurvivalResidenceTileEntity(position, state);
    }

    public static void register(final IEventBus modBus) {
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        BLOCK_ENTITIES.register(modBus);
        BUILDINGS.register(modBus);
        modBus.addListener(MineColoniesIntegration::addToCreativeTabs);
    }

    private static void addToCreativeTabs(final BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(SURVIVAL_RESIDENCE_ITEM);
            event.accept(SURVIVAL_COOK_ITEM);
            event.accept(SURVIVAL_GUARD_ITEM);
            event.accept(SURVIVAL_COW_PEN_ITEM);
            event.accept(SURVIVAL_SHEEP_PEN_ITEM);
        }
    }
}
