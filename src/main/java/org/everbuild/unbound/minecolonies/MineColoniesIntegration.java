package org.everbuild.unbound.minecolonies;

import com.minecolonies.api.colony.buildings.registry.BuildingEntry;
import com.minecolonies.core.colony.buildings.modules.BuildingModules;
import com.minecolonies.core.colony.buildings.modules.HomeBuildingModule;
import com.minecolonies.core.colony.buildings.moduleviews.LivingBuildingModuleView;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingCook;
import com.minecolonies.core.colony.buildings.views.EmptyView;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingGuardTower;
import com.minecolonies.core.colony.buildings.workerbuildings.BuildingBeekeeper;
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

    public static final DeferredBlock<SurvivalChickenPenBlock> SURVIVAL_CHICKEN_PEN_BLOCK = BLOCKS.registerBlock(
            "survival_chicken_pen_plaque", SurvivalChickenPenBlock::new,
            BlockBehaviour.Properties.of().strength(3.0F).sound(SoundType.WOOD));
    public static final DeferredItem<BlockItem> SURVIVAL_CHICKEN_PEN_ITEM = ITEMS.registerSimpleBlockItem(
            SURVIVAL_CHICKEN_PEN_BLOCK, new Item.Properties().stacksTo(1));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SurvivalChickenPenTileEntity>>
            SURVIVAL_CHICKEN_PEN_TILE = BLOCK_ENTITIES.register(
                    "survival_chicken_pen_plaque",
                    () -> BlockEntityType.Builder.of(SurvivalChickenPenTileEntity::new, SURVIVAL_CHICKEN_PEN_BLOCK.get())
                            .build(null));

    public static final DeferredBlock<SurvivalPigPenBlock> SURVIVAL_PIG_PEN_BLOCK = BLOCKS.registerBlock(
            "survival_pig_pen_plaque", SurvivalPigPenBlock::new,
            BlockBehaviour.Properties.of().strength(3.0F).sound(SoundType.WOOD));
    public static final DeferredItem<BlockItem> SURVIVAL_PIG_PEN_ITEM = ITEMS.registerSimpleBlockItem(
            SURVIVAL_PIG_PEN_BLOCK, new Item.Properties().stacksTo(1));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SurvivalPigPenTileEntity>>
            SURVIVAL_PIG_PEN_TILE = BLOCK_ENTITIES.register(
                    "survival_pig_pen_plaque",
                    () -> BlockEntityType.Builder.of(SurvivalPigPenTileEntity::new, SURVIVAL_PIG_PEN_BLOCK.get())
                            .build(null));

    public static final DeferredBlock<SurvivalRabbitHutchBlock> SURVIVAL_RABBIT_HUTCH_BLOCK = BLOCKS.registerBlock(
            "survival_rabbit_hutch_plaque", SurvivalRabbitHutchBlock::new,
            BlockBehaviour.Properties.of().strength(3.0F).sound(SoundType.WOOD));
    public static final DeferredItem<BlockItem> SURVIVAL_RABBIT_HUTCH_ITEM = ITEMS.registerSimpleBlockItem(
            SURVIVAL_RABBIT_HUTCH_BLOCK, new Item.Properties().stacksTo(1));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SurvivalRabbitHutchTileEntity>>
            SURVIVAL_RABBIT_HUTCH_TILE = BLOCK_ENTITIES.register(
                    "survival_rabbit_hutch_plaque",
                    () -> BlockEntityType.Builder.of(SurvivalRabbitHutchTileEntity::new, SURVIVAL_RABBIT_HUTCH_BLOCK.get())
                            .build(null));

    public static final DeferredBlock<SurvivalStableBlock> SURVIVAL_STABLE_BLOCK = BLOCKS.registerBlock(
            "survival_stable_plaque", SurvivalStableBlock::new,
            BlockBehaviour.Properties.of().strength(3.0F).sound(SoundType.WOOD));
    public static final DeferredItem<BlockItem> SURVIVAL_STABLE_ITEM = ITEMS.registerSimpleBlockItem(
            SURVIVAL_STABLE_BLOCK, new Item.Properties().stacksTo(1));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SurvivalStableTileEntity>>
            SURVIVAL_STABLE_TILE = BLOCK_ENTITIES.register(
                    "survival_stable_plaque",
                    () -> BlockEntityType.Builder.of(SurvivalStableTileEntity::new, SURVIVAL_STABLE_BLOCK.get())
                            .build(null));

    public static final DeferredBlock<SurvivalApiaryBlock> SURVIVAL_APIARY_BLOCK = BLOCKS.registerBlock(
            "survival_apiary_plaque", SurvivalApiaryBlock::new,
            BlockBehaviour.Properties.of().strength(3.0F).sound(SoundType.WOOD));
    public static final DeferredItem<BlockItem> SURVIVAL_APIARY_ITEM = ITEMS.registerSimpleBlockItem(
            SURVIVAL_APIARY_BLOCK, new Item.Properties().stacksTo(1));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SurvivalApiaryTileEntity>>
            SURVIVAL_APIARY_TILE = BLOCK_ENTITIES.register(
                    "survival_apiary_plaque",
                    () -> BlockEntityType.Builder.of(SurvivalApiaryTileEntity::new, SURVIVAL_APIARY_BLOCK.get())
                            .build(null));

    public static final DeferredBlock<SurvivalCraftingBlock> SURVIVAL_BLACKSMITH_BLOCK = BLOCKS.registerBlock(
            "survival_blacksmith_plaque",
            properties -> new SurvivalCraftingBlock(properties, "survival_blacksmith",
                    () -> MineColoniesIntegration.SURVIVAL_BLACKSMITH.get()),
            BlockBehaviour.Properties.of().strength(3.0F).sound(SoundType.WOOD));
    public static final DeferredItem<BlockItem> SURVIVAL_BLACKSMITH_ITEM = ITEMS.registerSimpleBlockItem(
            SURVIVAL_BLACKSMITH_BLOCK, new Item.Properties().stacksTo(1));
    public static final DeferredBlock<SurvivalCraftingBlock> SURVIVAL_SAWMILL_BLOCK = BLOCKS.registerBlock(
            "survival_sawmill_plaque",
            properties -> new SurvivalCraftingBlock(properties, "survival_sawmill",
                    () -> MineColoniesIntegration.SURVIVAL_SAWMILL.get()),
            BlockBehaviour.Properties.of().strength(3.0F).sound(SoundType.WOOD));
    public static final DeferredItem<BlockItem> SURVIVAL_SAWMILL_ITEM = ITEMS.registerSimpleBlockItem(
            SURVIVAL_SAWMILL_BLOCK, new Item.Properties().stacksTo(1));
    public static final DeferredBlock<SurvivalCraftingBlock> SURVIVAL_STONEMASON_BLOCK = BLOCKS.registerBlock(
            "survival_stonemason_plaque",
            properties -> new SurvivalCraftingBlock(properties, "survival_stonemason",
                    () -> MineColoniesIntegration.SURVIVAL_STONEMASON.get()),
            BlockBehaviour.Properties.of().strength(3.0F).sound(SoundType.WOOD));
    public static final DeferredItem<BlockItem> SURVIVAL_STONEMASON_ITEM = ITEMS.registerSimpleBlockItem(
            SURVIVAL_STONEMASON_BLOCK, new Item.Properties().stacksTo(1));
    public static final DeferredBlock<SurvivalCraftingBlock> SURVIVAL_FLETCHER_BLOCK = BLOCKS.registerBlock(
            "survival_fletcher_plaque",
            properties -> new SurvivalCraftingBlock(properties, "survival_fletcher",
                    () -> MineColoniesIntegration.SURVIVAL_FLETCHER.get()),
            BlockBehaviour.Properties.of().strength(3.0F).sound(SoundType.WOOD));
    public static final DeferredItem<BlockItem> SURVIVAL_FLETCHER_ITEM = ITEMS.registerSimpleBlockItem(
            SURVIVAL_FLETCHER_BLOCK, new Item.Properties().stacksTo(1));
    public static final DeferredBlock<SurvivalCraftingBlock> SURVIVAL_MECHANIC_BLOCK = BLOCKS.registerBlock(
            "survival_mechanic_plaque",
            properties -> new SurvivalCraftingBlock(properties, "survival_mechanic",
                    () -> MineColoniesIntegration.SURVIVAL_MECHANIC.get()),
            BlockBehaviour.Properties.of().strength(3.0F).sound(SoundType.WOOD));
    public static final DeferredItem<BlockItem> SURVIVAL_MECHANIC_ITEM = ITEMS.registerSimpleBlockItem(
            SURVIVAL_MECHANIC_BLOCK, new Item.Properties().stacksTo(1));
    public static final DeferredBlock<SurvivalCraftingBlock> SURVIVAL_CONCRETE_MIXER_BLOCK = BLOCKS.registerBlock(
            "survival_concrete_mixer_plaque",
            properties -> new SurvivalCraftingBlock(properties, "survival_concrete_mixer",
                    () -> MineColoniesIntegration.SURVIVAL_CONCRETE_MIXER.get()),
            BlockBehaviour.Properties.of().strength(3.0F).sound(SoundType.WOOD));
    public static final DeferredItem<BlockItem> SURVIVAL_CONCRETE_MIXER_ITEM = ITEMS.registerSimpleBlockItem(
            SURVIVAL_CONCRETE_MIXER_BLOCK, new Item.Properties().stacksTo(1));
    public static final DeferredBlock<SurvivalCraftingBlock> SURVIVAL_CRUSHER_BLOCK = BLOCKS.registerBlock(
            "survival_crusher_plaque",
            properties -> new SurvivalCraftingBlock(properties, "survival_crusher",
                    () -> MineColoniesIntegration.SURVIVAL_CRUSHER.get()),
            BlockBehaviour.Properties.of().strength(3.0F).sound(SoundType.WOOD));
    public static final DeferredItem<BlockItem> SURVIVAL_CRUSHER_ITEM = ITEMS.registerSimpleBlockItem(
            SURVIVAL_CRUSHER_BLOCK, new Item.Properties().stacksTo(1));
    public static final DeferredBlock<SurvivalCraftingBlock> SURVIVAL_SIFTER_BLOCK = BLOCKS.registerBlock(
            "survival_sifter_plaque",
            properties -> new SurvivalCraftingBlock(properties, "survival_sifter",
                    () -> MineColoniesIntegration.SURVIVAL_SIFTER.get()),
            BlockBehaviour.Properties.of().strength(3.0F).sound(SoundType.WOOD));
    public static final DeferredItem<BlockItem> SURVIVAL_SIFTER_ITEM = ITEMS.registerSimpleBlockItem(
            SURVIVAL_SIFTER_BLOCK, new Item.Properties().stacksTo(1));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SurvivalCraftingTileEntity>>
            SURVIVAL_CRAFTING_TILE = BLOCK_ENTITIES.register(
                    "survival_crafting_plaque",
                    () -> BlockEntityType.Builder.of(
                            SurvivalCraftingTileEntity::new,
                            SURVIVAL_BLACKSMITH_BLOCK.get(),
                            SURVIVAL_SAWMILL_BLOCK.get(),
                            SURVIVAL_STONEMASON_BLOCK.get(),
                            SURVIVAL_FLETCHER_BLOCK.get(),
                            SURVIVAL_MECHANIC_BLOCK.get(),
                            SURVIVAL_CONCRETE_MIXER_BLOCK.get(),
                            SURVIVAL_CRUSHER_BLOCK.get(),
                            SURVIVAL_SIFTER_BLOCK.get())
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

    public static final DeferredHolder<BuildingEntry, BuildingEntry> SURVIVAL_CHICKEN_PEN = BUILDINGS.register(
            "survival_chicken_pen",
            () -> new BuildingEntry.Builder()
                    .setRegistryName(ResourceLocation.fromNamespaceAndPath(ColoniesUnbound.MOD_ID, "survival_chicken_pen"))
                    .setBuildingBlock(SURVIVAL_CHICKEN_PEN_BLOCK.get())
                    .setBuildingProducer(SurvivalChickenPenBuilding::new)
                    .setBuildingViewProducer(() -> EmptyView::new)
                    .addBuildingModuleProducer(BuildingModules.CHICKENHERDER_WORK)
                    .addBuildingModuleProducer(BuildingModules.CHICKENHERDER_HERDING)
                    .addBuildingModuleProducer(BuildingModules.CHICKENHERDER_SETTINGS_BREEDING)
                    .addBuildingModuleProducer(BuildingModules.MIN_STOCK)
                    .addBuildingModuleProducer(BuildingModules.STATS_MODULE)
                    .createBuildingEntry());

    public static final DeferredHolder<BuildingEntry, BuildingEntry> SURVIVAL_PIG_PEN = BUILDINGS.register(
            "survival_pig_pen",
            () -> new BuildingEntry.Builder()
                    .setRegistryName(ResourceLocation.fromNamespaceAndPath(ColoniesUnbound.MOD_ID, "survival_pig_pen"))
                    .setBuildingBlock(SURVIVAL_PIG_PEN_BLOCK.get())
                    .setBuildingProducer(SurvivalPigPenBuilding::new)
                    .setBuildingViewProducer(() -> EmptyView::new)
                    .addBuildingModuleProducer(BuildingModules.SWINEHERDER_WORK)
                    .addBuildingModuleProducer(BuildingModules.SWINEHERDER_HERDING)
                    .addBuildingModuleProducer(BuildingModules.SWINEHERDER_SETTINGS)
                    .addBuildingModuleProducer(BuildingModules.MIN_STOCK)
                    .addBuildingModuleProducer(BuildingModules.STATS_MODULE)
                    .createBuildingEntry());

    public static final DeferredHolder<BuildingEntry, BuildingEntry> SURVIVAL_RABBIT_HUTCH = BUILDINGS.register(
            "survival_rabbit_hutch",
            () -> new BuildingEntry.Builder()
                    .setRegistryName(ResourceLocation.fromNamespaceAndPath(ColoniesUnbound.MOD_ID, "survival_rabbit_hutch"))
                    .setBuildingBlock(SURVIVAL_RABBIT_HUTCH_BLOCK.get())
                    .setBuildingProducer(SurvivalRabbitHutchBuilding::new)
                    .setBuildingViewProducer(() -> EmptyView::new)
                    .addBuildingModuleProducer(BuildingModules.RABBITHERDER_WORK)
                    .addBuildingModuleProducer(BuildingModules.RABBITHERDER_HERDING)
                    .addBuildingModuleProducer(BuildingModules.RABBITHERDER_SETTINGS)
                    .addBuildingModuleProducer(BuildingModules.MIN_STOCK)
                    .addBuildingModuleProducer(BuildingModules.STATS_MODULE)
                    .createBuildingEntry());

    public static final DeferredHolder<BuildingEntry, BuildingEntry> SURVIVAL_STABLE = BUILDINGS.register(
            "survival_stable",
            () -> new BuildingEntry.Builder()
                    .setRegistryName(ResourceLocation.fromNamespaceAndPath(ColoniesUnbound.MOD_ID, "survival_stable"))
                    .setBuildingBlock(SURVIVAL_STABLE_BLOCK.get())
                    .setBuildingProducer(SurvivalStableBuilding::new)
                    .setBuildingViewProducer(() -> EmptyView::new)
                    .addBuildingModuleProducer(BuildingModules.CAVALRY_STABLE_WORK)
                    .addBuildingModuleProducer(BuildingModules.STABLEMASTER_WORK)
                    .addBuildingModuleProducer(BuildingModules.STABLEMASTER_HERDING)
                    .addBuildingModuleProducer(BuildingModules.GUARD_ENTITY_LIST)
                    .addBuildingModuleProducer(BuildingModules.STABLE_SETTINGS)
                    .addBuildingModuleProducer(BuildingModules.MIN_STOCK)
                    .addBuildingModuleProducer(BuildingModules.STATS_MODULE)
                    .createBuildingEntry());

    public static final DeferredHolder<BuildingEntry, BuildingEntry> SURVIVAL_APIARY = BUILDINGS.register(
            "survival_apiary",
            () -> new BuildingEntry.Builder()
                    .setRegistryName(ResourceLocation.fromNamespaceAndPath(ColoniesUnbound.MOD_ID, "survival_apiary"))
                    .setBuildingBlock(SURVIVAL_APIARY_BLOCK.get())
                    .setBuildingProducer(SurvivalApiaryBuilding::new)
                    .setBuildingViewProducer(() -> BuildingBeekeeper.View::new)
                    .addBuildingModuleProducer(BuildingModules.BEEKEEPER_WORK)
                    .addBuildingModuleProducer(BuildingModules.BEEKEEPER_TOOL)
                    .addBuildingModuleProducer(BuildingModules.BEEKEEPER_HERDING)
                    .addBuildingModuleProducer(BuildingModules.BEEKEEPER_SETTINGS)
                    .addBuildingModuleProducer(BuildingModules.ITEMLIST_FLOWER)
                    .addBuildingModuleProducer(BuildingModules.MIN_STOCK)
                    .addBuildingModuleProducer(BuildingModules.STATS_MODULE)
                    .createBuildingEntry());

    public static final DeferredHolder<BuildingEntry, BuildingEntry> SURVIVAL_BLACKSMITH = BUILDINGS.register(
            "survival_blacksmith", () -> new BuildingEntry.Builder()
                    .setRegistryName(ResourceLocation.fromNamespaceAndPath(ColoniesUnbound.MOD_ID, "survival_blacksmith"))
                    .setBuildingBlock(SURVIVAL_BLACKSMITH_BLOCK.get()).setBuildingProducer(SurvivalBlacksmithBuilding::new)
                    .setBuildingViewProducer(() -> EmptyView::new)
                    .addBuildingModuleProducer(BuildingModules.BLACKSMITH_WORK).addBuildingModuleProducer(BuildingModules.BLACKSMITH_CRAFT)
                    .addBuildingModuleProducer(BuildingModules.SETTINGS_CRAFTER_RECIPE).addBuildingModuleProducer(BuildingModules.CRAFT_TASK_VIEW)
                    .addBuildingModuleProducer(BuildingModules.STATS_MODULE).createBuildingEntry());
    public static final DeferredHolder<BuildingEntry, BuildingEntry> SURVIVAL_SAWMILL = BUILDINGS.register(
            "survival_sawmill", () -> new BuildingEntry.Builder()
                    .setRegistryName(ResourceLocation.fromNamespaceAndPath(ColoniesUnbound.MOD_ID, "survival_sawmill"))
                    .setBuildingBlock(SURVIVAL_SAWMILL_BLOCK.get()).setBuildingProducer(SurvivalSawmillBuilding::new)
                    .setBuildingViewProducer(() -> EmptyView::new)
                    .addBuildingModuleProducer(BuildingModules.SAWMILL_WORK).addBuildingModuleProducer(BuildingModules.SAWMILL_CRAFT)
                    .addBuildingModuleProducer(BuildingModules.SAWMILL_DO_CRAFT).addBuildingModuleProducer(BuildingModules.SETTINGS_CRAFTER_RECIPE)
                    .addBuildingModuleProducer(BuildingModules.CRAFT_TASK_VIEW).addBuildingModuleProducer(BuildingModules.STATS_MODULE)
                    .createBuildingEntry());
    public static final DeferredHolder<BuildingEntry, BuildingEntry> SURVIVAL_STONEMASON = BUILDINGS.register(
            "survival_stonemason", () -> new BuildingEntry.Builder()
                    .setRegistryName(ResourceLocation.fromNamespaceAndPath(ColoniesUnbound.MOD_ID, "survival_stonemason"))
                    .setBuildingBlock(SURVIVAL_STONEMASON_BLOCK.get()).setBuildingProducer(SurvivalStonemasonBuilding::new)
                    .setBuildingViewProducer(() -> EmptyView::new)
                    .addBuildingModuleProducer(BuildingModules.STONEMASON_WORK).addBuildingModuleProducer(BuildingModules.STONEMASON_CRAFT)
                    .addBuildingModuleProducer(BuildingModules.STONEMASON_DO_CRAFT).addBuildingModuleProducer(BuildingModules.SETTINGS_CRAFTER_RECIPE)
                    .addBuildingModuleProducer(BuildingModules.CRAFT_TASK_VIEW).addBuildingModuleProducer(BuildingModules.STATS_MODULE)
                    .createBuildingEntry());
    public static final DeferredHolder<BuildingEntry, BuildingEntry> SURVIVAL_FLETCHER = BUILDINGS.register(
            "survival_fletcher", () -> new BuildingEntry.Builder()
                    .setRegistryName(ResourceLocation.fromNamespaceAndPath(ColoniesUnbound.MOD_ID, "survival_fletcher"))
                    .setBuildingBlock(SURVIVAL_FLETCHER_BLOCK.get()).setBuildingProducer(SurvivalFletcherBuilding::new)
                    .setBuildingViewProducer(() -> EmptyView::new)
                    .addBuildingModuleProducer(BuildingModules.FLETCHER_WORK).addBuildingModuleProducer(BuildingModules.FLETCHER_CRAFT)
                    .addBuildingModuleProducer(BuildingModules.FLETCHER_DO_CRAFT).addBuildingModuleProducer(BuildingModules.SETTINGS_CRAFTER_RECIPE)
                    .addBuildingModuleProducer(BuildingModules.CRAFT_TASK_VIEW).addBuildingModuleProducer(BuildingModules.STATS_MODULE)
                    .createBuildingEntry());
    public static final DeferredHolder<BuildingEntry, BuildingEntry> SURVIVAL_MECHANIC = BUILDINGS.register(
            "survival_mechanic", () -> new BuildingEntry.Builder()
                    .setRegistryName(ResourceLocation.fromNamespaceAndPath(ColoniesUnbound.MOD_ID, "survival_mechanic"))
                    .setBuildingBlock(SURVIVAL_MECHANIC_BLOCK.get()).setBuildingProducer(SurvivalMechanicBuilding::new)
                    .setBuildingViewProducer(() -> EmptyView::new)
                    .addBuildingModuleProducer(BuildingModules.MECHANIC_WORK).addBuildingModuleProducer(BuildingModules.MECHANIC_CRAFT)
                    .addBuildingModuleProducer(BuildingModules.MECHANIC_DO_CRAFT).addBuildingModuleProducer(BuildingModules.SETTINGS_CRAFTER_RECIPE)
                    .addBuildingModuleProducer(BuildingModules.CRAFT_TASK_VIEW).addBuildingModuleProducer(BuildingModules.STATS_MODULE)
                    .createBuildingEntry());
    public static final DeferredHolder<BuildingEntry, BuildingEntry> SURVIVAL_CONCRETE_MIXER = BUILDINGS.register(
            "survival_concrete_mixer", () -> new BuildingEntry.Builder()
                    .setRegistryName(ResourceLocation.fromNamespaceAndPath(ColoniesUnbound.MOD_ID, "survival_concrete_mixer"))
                    .setBuildingBlock(SURVIVAL_CONCRETE_MIXER_BLOCK.get()).setBuildingProducer(SurvivalConcreteMixerBuilding::new)
                    .setBuildingViewProducer(() -> EmptyView::new)
                    .addBuildingModuleProducer(BuildingModules.CONCRETEMIXER_WORK).addBuildingModuleProducer(BuildingModules.CONCRETEMIXER_CRAFT)
                    .addBuildingModuleProducer(BuildingModules.SETTINGS_CRAFTER_RECIPE).addBuildingModuleProducer(BuildingModules.CRAFT_TASK_VIEW)
                    .addBuildingModuleProducer(BuildingModules.STATS_MODULE).createBuildingEntry());
    public static final DeferredHolder<BuildingEntry, BuildingEntry> SURVIVAL_CRUSHER = BUILDINGS.register(
            "survival_crusher", () -> new BuildingEntry.Builder()
                    .setRegistryName(ResourceLocation.fromNamespaceAndPath(ColoniesUnbound.MOD_ID, "survival_crusher"))
                    .setBuildingBlock(SURVIVAL_CRUSHER_BLOCK.get()).setBuildingProducer(SurvivalCrusherBuilding::new)
                    .setBuildingViewProducer(() -> EmptyView::new)
                    .addBuildingModuleProducer(BuildingModules.CRUSHER_WORK).addBuildingModuleProducer(BuildingModules.CRUSHER_CRAFT)
                    .addBuildingModuleProducer(BuildingModules.CRUSHER_SETTINGS).addBuildingModuleProducer(BuildingModules.CRAFT_TASK_VIEW)
                    .addBuildingModuleProducer(BuildingModules.STATS_MODULE).createBuildingEntry());
    public static final DeferredHolder<BuildingEntry, BuildingEntry> SURVIVAL_SIFTER = BUILDINGS.register(
            "survival_sifter", () -> new BuildingEntry.Builder()
                    .setRegistryName(ResourceLocation.fromNamespaceAndPath(ColoniesUnbound.MOD_ID, "survival_sifter"))
                    .setBuildingBlock(SURVIVAL_SIFTER_BLOCK.get()).setBuildingProducer(SurvivalSifterBuilding::new)
                    .setBuildingViewProducer(() -> EmptyView::new)
                    .addBuildingModuleProducer(BuildingModules.SIFTER_WORK).addBuildingModuleProducer(BuildingModules.SIFTER_CRAFT)
                    .addBuildingModuleProducer(BuildingModules.MIN_STOCK).addBuildingModuleProducer(BuildingModules.STATS_MODULE)
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
            event.accept(SURVIVAL_CHICKEN_PEN_ITEM);
            event.accept(SURVIVAL_PIG_PEN_ITEM);
            event.accept(SURVIVAL_RABBIT_HUTCH_ITEM);
            event.accept(SURVIVAL_STABLE_ITEM);
            event.accept(SURVIVAL_APIARY_ITEM);
            event.accept(SURVIVAL_BLACKSMITH_ITEM);
            event.accept(SURVIVAL_SAWMILL_ITEM);
            event.accept(SURVIVAL_STONEMASON_ITEM);
            event.accept(SURVIVAL_FLETCHER_ITEM);
            event.accept(SURVIVAL_MECHANIC_ITEM);
            event.accept(SURVIVAL_CONCRETE_MIXER_ITEM);
            event.accept(SURVIVAL_CRUSHER_ITEM);
            event.accept(SURVIVAL_SIFTER_ITEM);
        }
    }
}
