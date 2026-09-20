package synthetic_diamonds.gametest;

import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;

/**
 * Shared assertions for the SD-3 recipe game tests (`docs/spec/domains/recipe.md`
 * `RECIPE-REQ-001`–`003`, `docs/spec/domains/roll.md` `ROLL-REQ-001`): a press cycle's result is
 * exactly one of {diamond, flint, gunpowder}, at the count that outcome's recipe declares — never
 * zero outcomes, never a fourth item, never the wrong count.
 */
final class RecipeAssertions {
    private RecipeAssertions() {
    }

    /**
     * Asserts {@code result} is exactly one stack, of one of the three outcome items, at that
     * outcome's expected count.
     */
    static void assertSingleExclusiveOutcome(
        GameTestHelper helper,
        java.util.List<ItemStack> result,
        int diamondCount,
        int flintCount,
        int gunpowderCount
    ) {
        helper.assertTrue(result.size() == 1, "expected exactly one result stack, got " + result.size() + ": " + result);
        assertExpectedOutcomeAndCount(helper, result.get(0), diamondCount, flintCount, gunpowderCount);
    }

    /** As {@link #assertSingleExclusiveOutcome}, but for a single already-pressed stack. */
    static void assertExpectedOutcomeAndCount(GameTestHelper helper, ItemStack stack, int diamondCount, int flintCount, int gunpowderCount) {
        if (stack.is(Items.DIAMOND)) {
            helper.assertTrue(stack.getCount() == diamondCount, "diamond count was " + stack.getCount() + ", expected " + diamondCount);
        } else if (stack.is(Items.FLINT)) {
            helper.assertTrue(stack.getCount() == flintCount, "flint count was " + stack.getCount() + ", expected " + flintCount);
        } else if (stack.is(Items.GUNPOWDER)) {
            helper.assertTrue(stack.getCount() == gunpowderCount, "gunpowder count was " + stack.getCount() + ", expected " + gunpowderCount);
        } else {
            helper.fail("unexpected outcome item, neither diamond, flint nor gunpowder: " + stack);
        }
    }
}
