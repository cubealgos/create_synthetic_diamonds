package synthetic_diamonds.gametest;

import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.content.kinetics.press.MechanicalPressBlockEntity;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Items;

/**
 * `docs/spec/domains/recipe.md` `RECIPE-REQ-005`: a datapack that replaces one of the three
 * shipped recipe files is used verbatim, decoded through the same codec as the shipped default.
 * This game-test session's own datapack (this class's companion mod,
 * {@code synthetic_diamonds_gametest}) ships its own
 * {@code data/synthetic_diamonds/recipe/weighted_pressing/charcoal.json} at the exact same
 * resource location as the shipped one, with {@code diamond_chance} retuned to {@code 1.0}
 * (`flint_chance`/`gunpowder_chance` both {@code 0.0}) — proving retuning via data alone, with no
 * Java change, by asserting every charcoal press yields a diamond in this environment.
 *
 * <p>This override is global to the whole {@code runGameTest} session (one dedicated server, one
 * data pack reload, shared by every {@code @GameTest} method here), so every other class in this
 * module that presses charcoal only ever asserts "one of the three outcomes at the right count" —
 * never "specifically flint or gunpowder" — precisely so an always-diamond charcoal here does not
 * make them flaky. {@link synthetic_diamonds.gametest.WeightedPressingDistributionGameTest}'s own
 * 2,000-roll charcoal distribution check is unaffected for the same reason it is unaffected by
 * datapack state at all: it builds its {@code WeightedPressingRecipe} directly from literals
 * rather than loading it through {@code RecipeManager}.
 */
public final class RecipeOverrideGameTest {
    private static final int ROLLS = 20;

    @GameTest
    public void aDatapackOverrideOfCharcoalJsonIsUsedVerbatim(GameTestHelper helper) {
        BlockPos pressPos = new BlockPos(1, 1, 1);
        helper.setBlock(pressPos, AllBlocks.MECHANICAL_PRESS.defaultBlockState());
        MechanicalPressBlockEntity press = helper.getBlockEntity(pressPos, MechanicalPressBlockEntity.class);

        for (int i = 0; i < ROLLS; i++) {
            ItemEntity charcoal = helper.spawnItem(Items.CHARCOAL, pressPos.above());
            boolean applied = press.tryProcessInWorld(charcoal, false);
            helper.assertTrue(applied, "roll " + i + ": the press did not find the overridden charcoal recipe");
            helper.assertTrue(
                charcoal.getItem().is(Items.DIAMOND),
                "roll " + i + ": the datapack override was not honoured, pressed to " + charcoal.getItem() + " instead of a diamond"
            );
            helper.assertTrue(charcoal.getItem().getCount() == 1, "roll " + i + ": diamond count was " + charcoal.getItem().getCount() + ", not 1");
            charcoal.discard();
        }
        helper.succeed();
    }
}
