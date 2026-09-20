package synthetic_diamonds.gametest;

import java.util.Collection;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.crafting.RecipeHolder;

/**
 * `docs/spec/domains/recipe.md` `RECIPE-FAIL-001`/`RECIPE-FAIL-002`: a recipe JSON whose
 * ingredient names an item id that does not exist, or that omits a required {@code *_chance}
 * field, fails to load exactly like any malformed vanilla recipe — the recipe is simply absent
 * from {@code RecipeManager}, no crash, and no other recipe in the same reload is disturbed. The
 * "no crash" half is proven twice over: directly, by this class's own two assertions running to
 * completion at all, and structurally, since every other {@code @GameTest} class in this module
 * runs against the exact same one data-pack reload that ships both malformed fixtures below —
 * had loading either one thrown instead of logging and skipping, the whole game-test session
 * would never have reached any test.
 *
 * <p>Both fixtures ship only in this game-test source set
 * ({@code data/synthetic_diamonds_gametest/recipe/weighted_pressing/}), each on an ingredient no
 * other fixture in this module uses, so neither can accidentally shadow a real assertion
 * elsewhere: {@code test_bad_item_id.json} names a nonexistent item id as its ingredient
 * (`RECIPE-FAIL-001`); {@code test_missing_field.json} omits its {@code gunpowder_chance} field
 * (`RECIPE-FAIL-002`).
 */
public final class RecipeLoadFailureGameTest {
    @GameTest
    public void aRecipeReferencingANonexistentItemIdFailsToLoadWithoutCrashing(GameTestHelper helper) {
        assertRecipeAbsent(helper, "test_bad_item_id");
        helper.succeed();
    }

    @GameTest
    public void aRecipeMissingARequiredChanceFieldFailsToLoadWithoutCrashing(GameTestHelper helper) {
        assertRecipeAbsent(helper, "test_missing_field");
        helper.succeed();
    }

    /** Confirms {@code synthetic_diamonds_gametest:weighted_pressing/<recipePath>} never reached {@code RecipeManager}. */
    private static void assertRecipeAbsent(GameTestHelper helper, String recipePath) {
        Collection<RecipeHolder<?>> all = helper.getLevel().getServer().getRecipeManager().getRecipes();
        boolean present = all.stream().anyMatch(r -> r.id().identifier().getPath().equals("weighted_pressing/" + recipePath));
        helper.assertTrue(!present, "malformed recipe " + recipePath + " should not have loaded, but was found in RecipeManager");
    }
}
