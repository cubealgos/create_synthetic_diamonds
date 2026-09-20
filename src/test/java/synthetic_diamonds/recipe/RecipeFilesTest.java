package synthetic_diamonds.recipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

/**
 * The three shipped recipe files (`docs/spec/domains/recipe.md` `RECIPE-REQ-001`–`003`) exist and
 * carry the spec's exact numbers. Checked against the raw JSON text with a field-level regex
 * (mirroring {@code SourceSurfaceTest}'s own source-tree-as-data approach) rather than decoded
 * through {@link WeightedPressingRecipeSerializer}'s codec, which needs a bootstrapped registry
 * environment this plain-JUnit source set does not have; the codec itself is exercised for real by
 * the SD-3 game tests under {@code src/gametest}, which press these exact files through a real
 * {@code MechanicalPressBlockEntity}.
 */
final class RecipeFilesTest {
    private static final Path RECIPE_DIR = Path.of("src/main/resources/data/synthetic_diamonds/recipe/weighted_pressing");

    private static final String DIAMOND_CHANCE = "0.005";
    private static final String FLINT_CHANCE = "0.95";
    private static final String GUNPOWDER_CHANCE = "0.045";

    @Test
    void charcoalJsonExistsAndCarriesTheSpecsNumbers() throws IOException {
        String json = readFile("charcoal.json");
        assertField(json, "type", "synthetic_diamonds:weighted_pressing");
        assertField(json, "ingredient", "minecraft:charcoal");
        assertWeightsAndCounts(json, "1", "1", "1");
    }

    @Test
    void coalJsonExistsAndCarriesTheSpecsNumbers() throws IOException {
        String json = readFile("coal.json");
        assertField(json, "type", "synthetic_diamonds:weighted_pressing");
        assertField(json, "ingredient", "minecraft:coal");
        assertWeightsAndCounts(json, "1", "1", "1");
    }

    @Test
    void coalBlockJsonExistsAndCarriesTheSpecsNumbers() throws IOException {
        // RECIPE-REQ-003: the same odds as a single item, matched via the c:storage_blocks/coal
        // tag, but with the by-product counts scaled x9 (Kevin's ruling,
        // decisions/DEC-007-inputs-and-block-variants.md) -- diamond count stays 1.
        String json = readFile("coal_block.json");
        assertField(json, "type", "synthetic_diamonds:weighted_pressing");
        assertField(json, "ingredient", "#c:storage_blocks/coal");
        assertWeightsAndCounts(json, "1", "9", "9");
    }

    @Test
    void noCharcoalBlockRecipeExists() {
        // RECIPE-REQ-004: deliberate, not an oversight -- neither vanilla nor Create Fly defines a
        // charcoal block to press, so there is nothing to add a fourth recipe for.
        assertTrue(
            Files.notExists(RECIPE_DIR.resolve("charcoal_block.json")),
            "no charcoal_block.json should exist (RECIPE-REQ-004)"
        );
    }

    @Test
    void exactlyThreeRecipeFilesAreShipped() throws IOException {
        try (var files = Files.list(RECIPE_DIR)) {
            long count = files.filter(p -> p.toString().endsWith(".json")).count();
            assertEquals(3, count, "exactly three shipped recipe JSON files (charcoal, coal, coal_block)");
        }
    }

    private static void assertWeightsAndCounts(String json, String diamondCount, String flintCount, String gunpowderCount) {
        assertField(json, "diamond_chance", DIAMOND_CHANCE);
        assertField(json, "diamond_count", diamondCount);
        assertField(json, "flint_chance", FLINT_CHANCE);
        assertField(json, "flint_count", flintCount);
        assertField(json, "gunpowder_chance", GUNPOWDER_CHANCE);
        assertField(json, "gunpowder_count", gunpowderCount);
    }

    private static void assertField(String json, String field, String expected) {
        // Matches both a quoted string value ("minecraft:charcoal") and a bare number (0.005), the
        // only two field shapes this recipe's codec declares.
        Pattern pattern = Pattern.compile("\"" + field + "\"\\s*:\\s*\"?([^\",}\\s]+)\"?");
        Matcher matcher = pattern.matcher(json);
        assertTrue(matcher.find(), "field \"" + field + "\" not found in " + json);
        assertEquals(expected, matcher.group(1), "field \"" + field + "\"");
    }

    private static String readFile(String fileName) throws IOException {
        Path path = RECIPE_DIR.resolve(fileName);
        assertTrue(Files.exists(path), path + " should exist");
        return Files.readString(path);
    }
}
