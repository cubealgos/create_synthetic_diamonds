package synthetic_diamonds.gametest;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import synthetic_diamonds.model.WeightedPick.Outcome;
import synthetic_diamonds.recipe.WeightedPressingRecipe;

/**
 * The exclusive-roll guarantee (`docs/spec/domains/roll.md` `ROLL-REQ-001`) held over
 * {@value #ROLLS} rolls of the shipped charcoal recipe's own weights, seeded for determinism
 * (`docs/spec/operations/testing.md`'s game-test row): every roll produces exactly one stack, of
 * one of the three outcome items, and the observed distribution lands within a wide, statistically
 * safe ballpark of the declared 0.5% / 95% / 4.5% weights.
 *
 * <p>Calls {@link WeightedPressingRecipe#assemble} directly with a fixed {@link RandomSource}
 * rather than driving 2,000 real press cycles through a {@code MechanicalPressBlockEntity}: {@code
 * assemble} is the exact method {@code tryProcessInWorld}/{@code tryProcessOnBelt} call after their
 * recipe lookup (SD-2's `## Findings`), so this exercises the identical roll logic without 2,000
 * real world ticks. The recipe instance below is built directly from the same six literals as the
 * shipped {@code charcoal.json} (not loaded through {@code RecipeManager}), so this test's tally is
 * unaffected by {@link RecipeOverrideGameTest}'s datapack override of that same file elsewhere in
 * this same game-test session.
 */
public final class WeightedPressingDistributionGameTest {
    /** Arbitrary, fixed for reproducibility — today's date as a seed, nothing more meaningful. */
    private static final long SEED = 20260920L;
    private static final int ROLLS = 2000;

    @GameTest
    public void chargedCharcoalRollsStayExclusiveAndInBallpark(GameTestHelper helper) {
        WeightedPressingRecipe recipe = new WeightedPressingRecipe(
            Ingredient.of(Items.CHARCOAL),
            0.005f, 1,
            0.95f, 1,
            0.045f, 1
        );
        SingleRecipeInput input = new SingleRecipeInput(new ItemStack(Items.CHARCOAL));
        RandomSource random = RandomSource.create(SEED);

        Map<Outcome, Integer> tally = new EnumMap<>(Outcome.class);
        for (Outcome outcome : Outcome.values()) {
            tally.put(outcome, 0);
        }

        for (int roll = 0; roll < ROLLS; roll++) {
            List<ItemStack> result = recipe.assemble(input, random);
            helper.assertTrue(result.size() == 1, "roll " + roll + " produced " + result.size() + " stacks, not exactly one");
            ItemStack stack = result.get(0);
            Outcome outcome;
            if (stack.is(Items.DIAMOND)) {
                outcome = Outcome.DIAMOND;
            } else if (stack.is(Items.FLINT)) {
                outcome = Outcome.FLINT;
            } else if (stack.is(Items.GUNPOWDER)) {
                outcome = Outcome.GUNPOWDER;
            } else {
                helper.fail("roll " + roll + " produced an unexpected item: " + stack);
                return;
            }
            helper.assertTrue(stack.getCount() == 1, "roll " + roll + "'s " + outcome + " stack had count " + stack.getCount() + ", not 1");
            tally.merge(outcome, 1, Integer::sum);
        }

        int diamonds = tally.get(Outcome.DIAMOND);
        int flints = tally.get(Outcome.FLINT);
        int gunpowders = tally.get(Outcome.GUNPOWDER);
        helper.assertTrue(diamonds + flints + gunpowders == ROLLS, "tally " + tally + " does not sum to " + ROLLS);

        double diamondShare = diamonds / (double) ROLLS;
        double flintShare = flints / (double) ROLLS;
        double gunpowderShare = gunpowders / (double) ROLLS;
        String summary = "seed=" + SEED + " rolls=" + ROLLS + " diamond=" + diamonds + " (" + diamondShare + ") flint=" + flints + " (" + flintShare
            + ") gunpowder=" + gunpowders + " (" + gunpowderShare + ")";

        // Wide bands around the declared 0.5% / 95% / 4.5% weights -- several binomial standard
        // deviations wide at n=2000 -- so this is a ballpark/exclusivity check, not a precision
        // check on the RNG itself (`operations/testing.md`: "the right ballpark", not exact).
        helper.assertTrue(diamondShare >= 0.001 && diamondShare <= 0.012, "diamond share outside [0.001, 0.012]: " + summary);
        helper.assertTrue(flintShare >= 0.92 && flintShare <= 0.98, "flint share outside [0.92, 0.98]: " + summary);
        helper.assertTrue(gunpowderShare >= 0.02 && gunpowderShare <= 0.07, "gunpowder share outside [0.02, 0.07]: " + summary);

        synthetic_diamonds.SyntheticDiamonds.LOGGER.info("SD-3 distribution: {}", summary);
        helper.succeed();
    }
}
