package org.everbuild.unbound.workplace;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.HayBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.material.Fluids;
import org.everbuild.unbound.marker.MarkerType;
import org.everbuild.unbound.minecolonies.MineColoniesIntegration;
import org.everbuild.unbound.minecolonies.SurvivalBlacksmithBuilding;
import org.everbuild.unbound.minecolonies.SurvivalAlchemistBuilding;
import org.everbuild.unbound.minecolonies.SurvivalBakerBuilding;
import org.everbuild.unbound.minecolonies.SurvivalConcreteMixerBuilding;
import org.everbuild.unbound.minecolonies.SurvivalCraftingBuilding;
import org.everbuild.unbound.minecolonies.SurvivalCrusherBuilding;
import org.everbuild.unbound.minecolonies.SurvivalFletcherBuilding;
import org.everbuild.unbound.minecolonies.SurvivalDyerBuilding;
import org.everbuild.unbound.minecolonies.SurvivalGlassblowerBuilding;
import org.everbuild.unbound.minecolonies.SurvivalKitchenBuilding;
import org.everbuild.unbound.minecolonies.SurvivalMechanicBuilding;
import org.everbuild.unbound.minecolonies.SurvivalSawmillBuilding;
import org.everbuild.unbound.minecolonies.SurvivalSifterBuilding;
import org.everbuild.unbound.minecolonies.SurvivalSmelteryBuilding;
import org.everbuild.unbound.minecolonies.SurvivalStoneSmelteryBuilding;
import org.everbuild.unbound.minecolonies.SurvivalStonemasonBuilding;
import org.everbuild.unbound.minecolonies.SurvivalComposterBuilding;
import org.everbuild.unbound.minecolonies.SurvivalFarmerBuilding;
import org.everbuild.unbound.minecolonies.SurvivalFishermanBuilding;
import org.everbuild.unbound.minecolonies.SurvivalFloristBuilding;
import org.everbuild.unbound.minecolonies.SurvivalLumberjackBuilding;
import org.everbuild.unbound.minecolonies.SurvivalPlantationBuilding;
import org.everbuild.unbound.minecolonies.SurvivalHospitalBuilding;
import org.everbuild.unbound.minecolonies.SurvivalSchoolBuilding;
import org.everbuild.unbound.minecolonies.SurvivalLibraryBuilding;
import org.everbuild.unbound.minecolonies.SurvivalUniversityBuilding;
import org.everbuild.unbound.minecolonies.SurvivalTavernBuilding;
import org.everbuild.unbound.minecolonies.SurvivalGraveyardBuilding;
import org.everbuild.unbound.minecolonies.SurvivalEnchanterBuilding;
import org.everbuild.unbound.minecolonies.SurvivalNetherWorkerBuilding;
import org.everbuild.unbound.minecolonies.SurvivalArcheryBuilding;
import org.everbuild.unbound.minecolonies.SurvivalCombatAcademyBuilding;
import org.everbuild.unbound.minecolonies.SurvivalWarehouseBuilding;
import org.everbuild.unbound.minecolonies.SurvivalPostBoxBuilding;
import org.everbuild.unbound.minecolonies.SurvivalDeliverymanBuilding;
import org.everbuild.unbound.minecolonies.SurvivalBarracksBuilding;
import org.everbuild.unbound.minecolonies.SurvivalBarracksTowerBuilding;
import org.everbuild.unbound.minecolonies.SurvivalGateHouseBuilding;
import org.everbuild.unbound.minecolonies.SurvivalBuilderBuilding;
import org.everbuild.unbound.minecolonies.SurvivalMinerBuilding;
import org.everbuild.unbound.minecolonies.SurvivalQuarryBuilding;
import org.everbuild.unbound.minecolonies.SurvivalTownHallBuilding;
import org.everbuild.unbound.minecolonies.SurvivalStashBuilding;
import org.everbuild.unbound.minecolonies.SurvivalMysticalSiteBuilding;

/** Declarative physical and native bindings for one crafting workplace. */
public record CraftingWorkplaceDefinition(
        String id,
        MarkerType markerType,
        Supplier<? extends Block> plaque,
        Class<? extends SurvivalCraftingBuilding> buildingType,
        BiPredicate<Level, BlockPos> isWorkstation,
        int maximumWorkstations,
        boolean requiresStorage,
        boolean requiresWorkstation) {

    public static List<CraftingWorkplaceDefinition> all() {
        return List.of(
                definition("blacksmith", MarkerType.BLACKSMITH, MineColoniesIntegration.SURVIVAL_BLACKSMITH_BLOCK,
                        SurvivalBlacksmithBuilding.class, (level, pos) -> level.getBlockState(pos).getBlock() instanceof AnvilBlock
                                || level.getBlockState(pos).is(Blocks.SMITHING_TABLE)),
                definition("sawmill", MarkerType.SAWMILL, MineColoniesIntegration.SURVIVAL_SAWMILL_BLOCK,
                        SurvivalSawmillBuilding.class, (level, pos) -> level.getBlockState(pos).is(Blocks.CRAFTING_TABLE)
                                || level.getBlockState(pos).is(Blocks.STONECUTTER)),
                definition("stonemason", MarkerType.STONEMASON, MineColoniesIntegration.SURVIVAL_STONEMASON_BLOCK,
                        SurvivalStonemasonBuilding.class, (level, pos) -> level.getBlockState(pos).is(Blocks.STONECUTTER)),
                definition("fletcher", MarkerType.FLETCHER, MineColoniesIntegration.SURVIVAL_FLETCHER_BLOCK,
                        SurvivalFletcherBuilding.class, (level, pos) -> level.getBlockState(pos).is(Blocks.FLETCHING_TABLE)),
                definition("mechanic", MarkerType.MECHANIC, MineColoniesIntegration.SURVIVAL_MECHANIC_BLOCK,
                        SurvivalMechanicBuilding.class, (level, pos) -> level.getBlockState(pos).is(Blocks.CRAFTING_TABLE)),
                definition("concrete_mixer", MarkerType.CONCRETE_MIXER, MineColoniesIntegration.SURVIVAL_CONCRETE_MIXER_BLOCK,
                        SurvivalConcreteMixerBuilding.class, (level, pos) -> {
                            final var fluid = level.getBlockState(pos).getFluidState();
                            return fluid.getType() == Fluids.FLOWING_WATER && fluid.getAmount() <= 5;
                        }),
                definition("crusher", MarkerType.CRUSHER, MineColoniesIntegration.SURVIVAL_CRUSHER_BLOCK,
                        SurvivalCrusherBuilding.class, (level, pos) -> level.getBlockState(pos).getBlock() instanceof AnvilBlock),
                definition("sifter", MarkerType.SIFTER, MineColoniesIntegration.SURVIVAL_SIFTER_BLOCK,
                        SurvivalSifterBuilding.class, (level, pos) -> level.getBlockState(pos).is(Blocks.SCAFFOLDING)),
                furnaceDefinition("bakery", MarkerType.BAKERY, MineColoniesIntegration.SURVIVAL_BAKERY_BLOCK,
                        SurvivalBakerBuilding.class),
                furnaceDefinition("kitchen", MarkerType.KITCHEN, MineColoniesIntegration.SURVIVAL_KITCHEN_BLOCK,
                        SurvivalKitchenBuilding.class),
                furnaceDefinition("smeltery", MarkerType.SMELTERY, MineColoniesIntegration.SURVIVAL_SMELTERY_BLOCK,
                        SurvivalSmelteryBuilding.class),
                furnaceDefinition("stone_smelter", MarkerType.STONE_SMELTER,
                        MineColoniesIntegration.SURVIVAL_STONE_SMELTER_BLOCK, SurvivalStoneSmelteryBuilding.class),
                furnaceDefinition("glassblower", MarkerType.GLASSBLOWER,
                        MineColoniesIntegration.SURVIVAL_GLASSBLOWER_BLOCK, SurvivalGlassblowerBuilding.class),
                furnaceDefinition("dyer", MarkerType.DYER, MineColoniesIntegration.SURVIVAL_DYER_BLOCK,
                        SurvivalDyerBuilding.class),
                definition("alchemist", MarkerType.ALCHEMIST, MineColoniesIntegration.SURVIVAL_ALCHEMIST_BLOCK,
                        SurvivalAlchemistBuilding.class, (level, pos) -> level.getBlockState(pos).is(Blocks.BREWING_STAND)),
                definition("farmer", MarkerType.FARMER, MineColoniesIntegration.SURVIVAL_FARMER_BLOCK,
                        SurvivalFarmerBuilding.class,
                        (level, pos) -> level.getBlockState(pos).is(com.minecolonies.api.blocks.ModBlocks.blockScarecrow), 5),
                definition("plantation", MarkerType.PLANTATION, MineColoniesIntegration.SURVIVAL_PLANTATION_BLOCK,
                        SurvivalPlantationBuilding.class,
                        (level, pos) -> level.getBlockState(pos).is(com.minecolonies.api.blocks.ModBlocks.blockPlantationField), 5),
                definition("fisherman", MarkerType.FISHERMAN, MineColoniesIntegration.SURVIVAL_FISHERMAN_BLOCK,
                        SurvivalFishermanBuilding.class,
                        (level, pos) -> level.getFluidState(pos).is(FluidTags.WATER)
                                && level.getFluidState(pos).isSource(), 1),
                definition("lumberjack", MarkerType.LUMBERJACK, MineColoniesIntegration.SURVIVAL_LUMBERJACK_BLOCK,
                        SurvivalLumberjackBuilding.class,
                        (level, pos) -> level.getBlockState(pos).is(BlockTags.LOGS), 1),
                definition("florist", MarkerType.FLORIST, MineColoniesIntegration.SURVIVAL_FLORIST_BLOCK,
                        SurvivalFloristBuilding.class,
                        (level, pos) -> level.getBlockState(pos).is(com.minecolonies.api.blocks.ModBlocks.blockCompostedDirt)),
                definition("composter", MarkerType.COMPOSTER, MineColoniesIntegration.SURVIVAL_COMPOSTER_BLOCK,
                        SurvivalComposterBuilding.class,
                        (level, pos) -> level.getBlockState(pos).is(com.minecolonies.api.blocks.ModBlocks.blockBarrel)),
                definition("hospital", MarkerType.HOSPITAL, MineColoniesIntegration.SURVIVAL_HOSPITAL_BLOCK,
                        SurvivalHospitalBuilding.class, CraftingWorkplaceDefinition::isBedHead),
                definition("school", MarkerType.SCHOOL, MineColoniesIntegration.SURVIVAL_SCHOOL_BLOCK,
                        SurvivalSchoolBuilding.class,
                        (level, pos) -> level.getBlockState(pos).getBlock() instanceof WoolCarpetBlock),
                definition("library", MarkerType.LIBRARY, MineColoniesIntegration.SURVIVAL_LIBRARY_BLOCK,
                        SurvivalLibraryBuilding.class,
                        (level, pos) -> level.getBlockState(pos).is(net.neoforged.neoforge.common.Tags.Blocks.BOOKSHELVES)),
                definition("university", MarkerType.UNIVERSITY, MineColoniesIntegration.SURVIVAL_UNIVERSITY_BLOCK,
                        SurvivalUniversityBuilding.class,
                        (level, pos) -> level.getBlockState(pos).is(net.neoforged.neoforge.common.Tags.Blocks.BOOKSHELVES)),
                definition("tavern", MarkerType.TAVERN, MineColoniesIntegration.SURVIVAL_TAVERN_BLOCK,
                        SurvivalTavernBuilding.class, CraftingWorkplaceDefinition::isBedHead),
                definition("graveyard", MarkerType.GRAVEYARD, MineColoniesIntegration.SURVIVAL_GRAVEYARD_BLOCK,
                        SurvivalGraveyardBuilding.class,
                        (level, pos) -> level.getBlockState(pos).is(com.minecolonies.api.blocks.ModBlocks.blockGrave)
                                || level.getBlockState(pos).is(com.minecolonies.api.blocks.ModBlocks.blockNamedGrave)),
                definition("enchanter", MarkerType.ENCHANTER, MineColoniesIntegration.SURVIVAL_ENCHANTER_BLOCK,
                        SurvivalEnchanterBuilding.class,
                        (level, pos) -> level.getBlockState(pos).is(Blocks.ENCHANTING_TABLE)),
                definition("nether_worker", MarkerType.NETHER_WORKER, MineColoniesIntegration.SURVIVAL_NETHER_WORKER_BLOCK,
                        SurvivalNetherWorkerBuilding.class,
                        (level, pos) -> level.getBlockState(pos).is(Blocks.NETHER_PORTAL), 1),
                definition("archery", MarkerType.ARCHERY, MineColoniesIntegration.SURVIVAL_ARCHERY_BLOCK,
                        SurvivalArcheryBuilding.class, (level, pos) -> level.getBlockState(pos).is(Blocks.TARGET)),
                definition("combat_academy", MarkerType.COMBAT_ACADEMY, MineColoniesIntegration.SURVIVAL_COMBAT_ACADEMY_BLOCK,
                        SurvivalCombatAcademyBuilding.class, (level, pos) -> level.getBlockState(pos).getBlock() instanceof CarvedPumpkinBlock
                                && level.getBlockState(pos.below()).getBlock() instanceof HayBlock),
                definition("warehouse", MarkerType.WAREHOUSE, MineColoniesIntegration.SURVIVAL_WAREHOUSE_BLOCK,
                        SurvivalWarehouseBuilding.class, (level, pos) -> level.getBlockState(pos).getBlock()
                                instanceof com.minecolonies.core.blocks.BlockMinecoloniesRack,
                        Integer.MAX_VALUE, false, true),
                definition("post_box", MarkerType.POST_BOX, MineColoniesIntegration.SURVIVAL_POST_BOX_BLOCK,
                        SurvivalPostBoxBuilding.class, (level, pos) -> false, 0, false, false),
                definition("deliveryman", MarkerType.DELIVERYMAN, MineColoniesIntegration.SURVIVAL_DELIVERYMAN_BLOCK,
                        SurvivalDeliverymanBuilding.class, (level, pos) -> false, 0, true, false),
                definition("barracks", MarkerType.BARRACKS, MineColoniesIntegration.SURVIVAL_BARRACKS_BLOCK,
                        SurvivalBarracksBuilding.class, (level, pos) -> false, 0, true, false),
                definition("barracks_tower", MarkerType.BARRACKS_TOWER, MineColoniesIntegration.SURVIVAL_BARRACKS_TOWER_BLOCK,
                        SurvivalBarracksTowerBuilding.class, CraftingWorkplaceDefinition::isBedHead),
                definition("gate_house", MarkerType.GATE_HOUSE, MineColoniesIntegration.SURVIVAL_GATE_HOUSE_BLOCK,
                        SurvivalGateHouseBuilding.class, (level, pos) -> level.getBlockState(pos).is(Blocks.TARGET), 4),
                definition("builder", MarkerType.BUILDER, MineColoniesIntegration.SURVIVAL_BUILDER_BLOCK,
                        SurvivalBuilderBuilding.class, (level, pos) -> level.getBlockState(pos).is(Blocks.CRAFTING_TABLE)),
                definition("miner", MarkerType.MINER, MineColoniesIntegration.SURVIVAL_MINER_BLOCK,
                        SurvivalMinerBuilding.class, (level, pos) -> level.getBlockState(pos).getBlock() instanceof LadderBlock, 1),
                definition("simple_quarry", MarkerType.SIMPLE_QUARRY, MineColoniesIntegration.SURVIVAL_SIMPLE_QUARRY_BLOCK,
                        SurvivalQuarryBuilding.class, (level, pos) -> false, 0, false, false),
                definition("medium_quarry", MarkerType.MEDIUM_QUARRY, MineColoniesIntegration.SURVIVAL_MEDIUM_QUARRY_BLOCK,
                        SurvivalQuarryBuilding.class, (level, pos) -> false, 0, false, false),
                definition("town_hall", MarkerType.TOWN_HALL, MineColoniesIntegration.SURVIVAL_TOWN_HALL_BLOCK,
                        SurvivalTownHallBuilding.class, (level, pos) -> false, 0, false, false),
                definition("stash", MarkerType.STASH, MineColoniesIntegration.SURVIVAL_STASH_BLOCK,
                        SurvivalStashBuilding.class, (level, pos) -> false, 0, false, false),
                definition("mystical_site", MarkerType.MYSTICAL_SITE, MineColoniesIntegration.SURVIVAL_MYSTICAL_SITE_BLOCK,
                        SurvivalMysticalSiteBuilding.class, (level, pos) -> false, 0, false, false));
    }

    private static CraftingWorkplaceDefinition definition(
            final String id, final MarkerType type, final Supplier<? extends Block> plaque,
            final Class<? extends SurvivalCraftingBuilding> buildingType,
            final BiPredicate<Level, BlockPos> workstation) {
        return definition(id, type, plaque, buildingType, workstation, Integer.MAX_VALUE);
    }

    private static CraftingWorkplaceDefinition definition(
            final String id, final MarkerType type, final Supplier<? extends Block> plaque,
            final Class<? extends SurvivalCraftingBuilding> buildingType,
            final BiPredicate<Level, BlockPos> workstation, final int maximumWorkstations) {
        return new CraftingWorkplaceDefinition(id, type, plaque, buildingType, workstation,
                maximumWorkstations, true, true);
    }

    private static CraftingWorkplaceDefinition definition(
            final String id, final MarkerType type, final Supplier<? extends Block> plaque,
            final Class<? extends SurvivalCraftingBuilding> buildingType,
            final BiPredicate<Level, BlockPos> workstation, final int maximumWorkstations,
            final boolean requiresStorage, final boolean requiresWorkstation) {
        return new CraftingWorkplaceDefinition(id, type, plaque, buildingType, workstation,
                maximumWorkstations, requiresStorage, requiresWorkstation);
    }

    private static CraftingWorkplaceDefinition furnaceDefinition(
            final String id, final MarkerType type, final Supplier<? extends Block> plaque,
            final Class<? extends SurvivalCraftingBuilding> buildingType) {
        return definition(id, type, plaque, buildingType,
                (level, pos) -> level.getBlockState(pos).getBlock() instanceof FurnaceBlock);
    }

    private static boolean isBedHead(final Level level, final BlockPos position) {
        final var state = level.getBlockState(position);
        return state.getBlock() instanceof BedBlock && state.getValue(BedBlock.PART) == BedPart.HEAD;
    }
}
