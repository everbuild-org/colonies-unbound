package org.everbuild.unbound.recipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.everbuild.unbound.marker.MarkerType;
import org.junit.jupiter.api.Test;

class PlaqueRecipeCoverageTest {
    private static final Map<MarkerType, RecipeSpec> RECIPES = Map.ofEntries(
            Map.entry(MarkerType.RESIDENCE, new RecipeSpec("survival_residence_plaque", "blockhutcitizen")),
            Map.entry(MarkerType.RESTAURANT, new RecipeSpec("survival_cook_plaque", "blockhutcook")),
            Map.entry(MarkerType.GUARD, new RecipeSpec("survival_guard_plaque", "blockhutguardtower")),
            Map.entry(MarkerType.ANIMAL_PEN, new RecipeSpec("survival_cow_pen_plaque", "blockhutcowboy")),
            Map.entry(MarkerType.SHEEP_PEN, new RecipeSpec("survival_sheep_pen_plaque", "blockhutshepherd")),
            Map.entry(MarkerType.CHICKEN_PEN, new RecipeSpec("survival_chicken_pen_plaque", "blockhutchickenherder")),
            Map.entry(MarkerType.PIG_PEN, new RecipeSpec("survival_pig_pen_plaque", "blockhutswineherder")),
            Map.entry(MarkerType.RABBIT_HUTCH, new RecipeSpec("survival_rabbit_hutch_plaque", "blockhutrabbithutch")),
            Map.entry(MarkerType.STABLE, new RecipeSpec("survival_stable_plaque", "blockhutstable")),
            Map.entry(MarkerType.APIARY, new RecipeSpec("survival_apiary_plaque", "blockhutbeekeeper")),
            Map.entry(MarkerType.BLACKSMITH, new RecipeSpec("survival_blacksmith_plaque", "blockhutblacksmith")),
            Map.entry(MarkerType.SAWMILL, new RecipeSpec("survival_sawmill_plaque", "blockhutsawmill")),
            Map.entry(MarkerType.STONEMASON, new RecipeSpec("survival_stonemason_plaque", "blockhutstonemason")),
            Map.entry(MarkerType.FLETCHER, new RecipeSpec("survival_fletcher_plaque", "blockhutfletcher")),
            Map.entry(MarkerType.MECHANIC, new RecipeSpec("survival_mechanic_plaque", "blockhutmechanic")),
            Map.entry(MarkerType.CONCRETE_MIXER, new RecipeSpec("survival_concrete_mixer_plaque", "blockhutconcretemixer")),
            Map.entry(MarkerType.CRUSHER, new RecipeSpec("survival_crusher_plaque", "blockhutcrusher")),
            Map.entry(MarkerType.SIFTER, new RecipeSpec("survival_sifter_plaque", "blockhutsifter")),
            Map.entry(MarkerType.BAKERY, new RecipeSpec("survival_bakery_plaque", "blockhutbaker")),
            Map.entry(MarkerType.KITCHEN, new RecipeSpec("survival_kitchen_plaque", "blockhutkitchen")),
            Map.entry(MarkerType.SMELTERY, new RecipeSpec("survival_smeltery_plaque", "blockhutsmeltery")),
            Map.entry(MarkerType.STONE_SMELTER, new RecipeSpec("survival_stone_smelter_plaque", "blockhutstonesmeltery")),
            Map.entry(MarkerType.GLASSBLOWER, new RecipeSpec("survival_glassblower_plaque", "blockhutglassblower")),
            Map.entry(MarkerType.DYER, new RecipeSpec("survival_dyer_plaque", "blockhutdyer")),
            Map.entry(MarkerType.ALCHEMIST, new RecipeSpec("survival_alchemist_plaque", "blockhutalchemist")),
            Map.entry(MarkerType.FARMER, new RecipeSpec("survival_farmer_plaque", "blockhutfarmer")),
            Map.entry(MarkerType.PLANTATION, new RecipeSpec("survival_plantation_plaque", "blockhutplantation")),
            Map.entry(MarkerType.FISHERMAN, new RecipeSpec("survival_fisherman_plaque", "blockhutfisherman")),
            Map.entry(MarkerType.LUMBERJACK, new RecipeSpec("survival_lumberjack_plaque", "blockhutlumberjack")),
            Map.entry(MarkerType.FLORIST, new RecipeSpec("survival_florist_plaque", "blockhutflorist")),
            Map.entry(MarkerType.COMPOSTER, new RecipeSpec("survival_composter_plaque", "blockhutcomposter")),
            Map.entry(MarkerType.HOSPITAL, new RecipeSpec("survival_hospital_plaque", "blockhuthospital")),
            Map.entry(MarkerType.SCHOOL, new RecipeSpec("survival_school_plaque", "blockhutschool")),
            Map.entry(MarkerType.LIBRARY, new RecipeSpec("survival_library_plaque", "blockhutlibrary")),
            Map.entry(MarkerType.UNIVERSITY, new RecipeSpec("survival_university_plaque", "blockhutuniversity")),
            Map.entry(MarkerType.TAVERN, new RecipeSpec("survival_tavern_plaque", "blockhuttavern")),
            Map.entry(MarkerType.GRAVEYARD, new RecipeSpec("survival_graveyard_plaque", "blockhutgraveyard")),
            Map.entry(MarkerType.ENCHANTER, new RecipeSpec("survival_enchanter_plaque", "blockhutenchanter")),
            Map.entry(MarkerType.NETHER_WORKER, new RecipeSpec("survival_nether_worker_plaque", "blockhutnetherworker")),
            Map.entry(MarkerType.ARCHERY, new RecipeSpec("survival_archery_plaque", "blockhutarchery")),
            Map.entry(MarkerType.COMBAT_ACADEMY, new RecipeSpec("survival_combat_academy_plaque", "blockhutcombatacademy")),
            Map.entry(MarkerType.WAREHOUSE, new RecipeSpec("survival_warehouse_plaque", "blockhutwarehouse")),
            Map.entry(MarkerType.POST_BOX, new RecipeSpec("survival_post_box_plaque", "blockpostbox")),
            Map.entry(MarkerType.DELIVERYMAN, new RecipeSpec("survival_deliveryman_plaque", "blockhutdeliveryman")),
            Map.entry(MarkerType.BARRACKS, new RecipeSpec("survival_barracks_plaque", "blockhutbarracks")),
            Map.entry(MarkerType.BARRACKS_TOWER, new RecipeSpec("survival_barracks_tower_plaque", "blockhutbarrackstower")),
            Map.entry(MarkerType.GATE_HOUSE, new RecipeSpec("survival_gate_house_plaque", "blockhutgatehouse")),
            Map.entry(MarkerType.BUILDER, new RecipeSpec("survival_builder_plaque", "blockhutbuilder")),
            Map.entry(MarkerType.MINER, new RecipeSpec("survival_miner_plaque", "blockhutminer")),
            Map.entry(MarkerType.SIMPLE_QUARRY, new RecipeSpec("survival_simple_quarry_plaque", "simplequarry")),
            Map.entry(MarkerType.MEDIUM_QUARRY, new RecipeSpec("survival_medium_quarry_plaque", "mediumquarry")),
            Map.entry(MarkerType.TOWN_HALL, new RecipeSpec("survival_town_hall_plaque", "blockhuttownhall")),
            Map.entry(MarkerType.STASH, new RecipeSpec("survival_stash_plaque", "blockstash")),
            Map.entry(MarkerType.MYSTICAL_SITE, new RecipeSpec("survival_mystical_site_plaque", "blockhutmysticalsite")));

    @Test
    void everyMarkerTypeHasAProgressionPreservingPlaqueRecipe() throws Exception {
        assertEquals(MarkerType.values().length, RECIPES.size());

        for (final MarkerType type : MarkerType.values()) {
            final RecipeSpec spec = RECIPES.get(type);
            assertNotNull(spec, type::name);

            final String path = "/data/coloniesunbound/recipe/" + spec.output() + ".json";
            try (InputStream stream = PlaqueRecipeCoverageTest.class.getResourceAsStream(path)) {
                assertNotNull(stream, path);
                final JsonObject recipe = JsonParser.parseReader(
                        new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();

                assertEquals("minecraft:crafting_shapeless", recipe.get("type").getAsString(), type::name);
                assertEquals("building", recipe.get("category").getAsString(), type::name);
                assertEquals("minecolonies:" + spec.input(),
                        recipe.getAsJsonArray("ingredients").get(0).getAsJsonObject().get("item").getAsString(),
                        type::name);
                assertEquals("minecraft:feather",
                        recipe.getAsJsonArray("ingredients").get(1).getAsJsonObject().get("item").getAsString(),
                        type::name);
                assertEquals("coloniesunbound:" + spec.output(),
                        recipe.getAsJsonObject("result").get("id").getAsString(),
                        type::name);
                assertEquals(1, recipe.getAsJsonObject("result").get("count").getAsInt(), type::name);
            }
        }
    }

    private record RecipeSpec(String output, String input) {
    }
}
