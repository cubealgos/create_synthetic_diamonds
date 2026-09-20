package synthetic_diamonds.gametest;

import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.content.kinetics.press.MechanicalPressBlockEntity;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

/**
 * A real {@code MechanicalPressBlockEntity} finds and runs all three shipped recipes
 * (`docs/spec/domains/recipe.md` `RECIPE-REQ-001`–`003`) in world/depot mode via
 * {@code tryProcessInWorld}, the same lookup-and-roll path SD-2's `## Findings` disassembled.
 * Each recipe is pressed {@link #ROLLS_PER_RECIPE} times, spawning a fresh single-item stack each
 * cycle: every cycle must consume the one input and yield exactly one of {diamond, flint,
 * gunpowder} at that recipe's own outcome count, never zero and never two
 * (`docs/spec/domains/roll.md` `ROLL-REQ-001`) — the repeated rolls (not a single press) are what
 * give this exclusivity guarantee real confidence, per `docs/spec/operations/testing.md`.
 */
public final class ShippedRecipesDepotGameTest {
    private static final int ROLLS_PER_RECIPE = 200;

    @GameTest
    public void charcoalIsFoundAndAlwaysYieldsExactlyOneOutcome(GameTestHelper helper) {
        pressManyTimes(helper, Items.CHARCOAL, 1, 1, 1);
        helper.succeed();
    }

    @GameTest
    public void coalIsFoundAndAlwaysYieldsExactlyOneOutcome(GameTestHelper helper) {
        pressManyTimes(helper, Items.COAL, 1, 1, 1);
        helper.succeed();
    }

    @GameTest
    public void coalBlockIsFoundAndAlwaysYieldsExactlyOneOutcomeAtItsOwnCounts(GameTestHelper helper) {
        // RECIPE-REQ-003: same odds as a single item, but the by-product counts scale x9
        // (Kevin's ruling, decisions/DEC-007-inputs-and-block-variants.md) — diamond stays 1.
        pressManyTimes(helper, Items.COAL_BLOCK, 1, 9, 9);
        helper.succeed();
    }

    private static void pressManyTimes(GameTestHelper helper, Item ingredient, int diamondCount, int flintCount, int gunpowderCount) {
        BlockPos pressPos = new BlockPos(1, 1, 1);
        helper.setBlock(pressPos, AllBlocks.MECHANICAL_PRESS.defaultBlockState());
        MechanicalPressBlockEntity press = helper.getBlockEntity(pressPos, MechanicalPressBlockEntity.class);

        for (int i = 0; i < ROLLS_PER_RECIPE; i++) {
            ItemEntity item = helper.spawnItem(ingredient, pressPos.above());
            boolean applied = press.tryProcessInWorld(item, false);
            helper.assertTrue(applied, "roll " + i + ": the press did not find a recipe for " + ingredient);
            RecipeAssertions.assertExpectedOutcomeAndCount(helper, item.getItem(), diamondCount, flintCount, gunpowderCount);
            item.discard();
        }
    }
}
