package synthetic_diamonds.gametest;

import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.content.kinetics.press.MechanicalPressBlockEntity;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Items;

/**
 * `docs/spec/domains/recipe.md` `RECIPE-FAIL-003`: a recipe JSON whose {@code diamond_chance} is
 * above the valid {@code [0, 1]} range loads anyway -- clamped to {@code 1.0} and logged once,
 * rather than rejected -- and every press cycle against it still yields exactly one outcome
 * (`docs/spec/domains/roll.md` `ROLL-REQ-001`), here always a single diamond since the other two
 * weights are {@code 0.0}.
 *
 * <p>{@code test_diamond_weight_clamp.json} ({@code diamond_chance: 2.0}, {@code flint_chance}
 * and {@code gunpowder_chance} both {@code 0.0}) ships only in this game-test source set
 * ({@code data/synthetic_diamonds_gametest/recipe/weighted_pressing/}), on {@code
 * minecraft:glowstone_dust} -- an ingredient no other fixture or shipped recipe in this module
 * uses -- so it cannot shadow or be shadowed by another recipe's lookup. A real
 * {@code MechanicalPressBlockEntity#tryProcessInWorld} call finding and applying this recipe at
 * all (rather than {@link WeightedPressingRecipeSerializer} failing to decode {@code 2.0} through
 * {@code Codec.FLOAT}, or the recipe never reaching {@code RecipeManager}) is itself the proof
 * that the out-of-range weight loaded rather than rejecting the recipe file, matching the
 * positive half of what {@link RecipeLoadFailureGameTest} proves in the negative for
 * {@code RECIPE-FAIL-001}/{@code RECIPE-FAIL-002}.
 */
public final class WeightedPressingClampGameTest {
    private static final int ROLLS = 50;

    @GameTest
    public void anOutOfRangeDiamondWeightIsClampedAndEveryRollYieldsExactlyOneDiamond(GameTestHelper helper) {
        BlockPos pressPos = new BlockPos(1, 1, 1);
        helper.setBlock(pressPos, AllBlocks.MECHANICAL_PRESS.defaultBlockState());
        MechanicalPressBlockEntity press = helper.getBlockEntity(pressPos, MechanicalPressBlockEntity.class);

        for (int roll = 0; roll < ROLLS; roll++) {
            ItemEntity glowstoneDust = helper.spawnItem(Items.GLOWSTONE_DUST, pressPos.above());
            boolean applied = press.tryProcessInWorld(glowstoneDust, false);
            helper.assertTrue(applied, "roll " + roll + ": the press did not find the clamped weighted-pressing recipe -- did it fail to load?");
            helper.assertTrue(
                glowstoneDust.getItem().is(Items.DIAMOND),
                "roll " + roll + ": expected a diamond, got " + glowstoneDust.getItem()
            );
            helper.assertTrue(glowstoneDust.getItem().getCount() == 1, "roll " + roll + ": diamond count was " + glowstoneDust.getItem().getCount() + ", not 1");
            glowstoneDust.discard();
        }
        helper.succeed();
    }
}
