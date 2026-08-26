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

/** Declarative physical and native bindings for one crafting workplace. */
public record CraftingWorkplaceDefinition(
        String id,
        MarkerType markerType,
        Supplier<? extends Block> plaque,
        Class<? extends SurvivalCraftingBuilding> buildingType,
        BiPredicate<Level, BlockPos> isWorkstation,
        int maximumWorkstations) {

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
                        (level, pos) -> level.getBlockState(pos).is(com.minecolonies.api.blocks.ModBlocks.blockBarrel)));
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
        return new CraftingWorkplaceDefinition(id, type, plaque, buildingType, workstation, maximumWorkstations);
    }

    private static CraftingWorkplaceDefinition furnaceDefinition(
            final String id, final MarkerType type, final Supplier<? extends Block> plaque,
            final Class<? extends SurvivalCraftingBuilding> buildingType) {
        return definition(id, type, plaque, buildingType,
                (level, pos) -> level.getBlockState(pos).getBlock() instanceof FurnaceBlock);
    }
}
