package synthetic_diamonds.gametest;

import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.content.kinetics.press.MechanicalPressBlockEntity;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Items;

/**
 * `docs/spec/operations/testing.md` `TEST-REQ-003`: this mod's recipe class coexists in the same
 * {@code AllRecipeTypes.PRESSING} {@code RecipeMap} bucket as Create Fly's own vanilla {@code
 * PressingRecipe} instances without disturbing either side's roll behaviour — the reason
 * `04-architecture.md` `ARCH-DEC-002` rejected a shared-method mixin on {@code
 * ProcessingOutput.rollOutput} in favour of this mod's own {@code Recipe}/{@code RecipeSerializer}
 * pair.
 *
 * <p>Presses Create Fly's own shipped {@code minecraft:sugar_cane} pressing recipe
 * ({@code create:pressing/sugar_cane.json}, a plain 100%-of-the-time {@code minecraft:paper}
 * result — no exclusive roll of its own) interleaved with this mod's {@code coal} recipe on the
 * same press, confirming each recipe's own outcome is exactly what it would be pressed alone: the
 * vanilla recipe always yields paper, and this mod's own recipe still yields exactly one of
 * {diamond, flint, gunpowder} every cycle.
 */
public final class RecipeCoexistenceGameTest {
    private static final int ROLLS = 25;

    @GameTest
    public void vanillaAndThisModsRecipeCoexistInTheSamePressingBucket(GameTestHelper helper) {
        BlockPos pressPos = new BlockPos(1, 1, 1);
        helper.setBlock(pressPos, AllBlocks.MECHANICAL_PRESS.defaultBlockState());
        MechanicalPressBlockEntity press = helper.getBlockEntity(pressPos, MechanicalPressBlockEntity.class);

        for (int i = 0; i < ROLLS; i++) {
            ItemEntity sugarCane = helper.spawnItem(Items.SUGAR_CANE, pressPos.above());
            boolean sugarCaneApplied = press.tryProcessInWorld(sugarCane, false);
            helper.assertTrue(sugarCaneApplied, "roll " + i + ": the press did not find Create Fly's own sugar_cane recipe");
            helper.assertTrue(
                sugarCane.getItem().is(Items.PAPER),
                "roll " + i + ": Create Fly's sugar_cane recipe was disturbed, pressed to " + sugarCane.getItem() + " instead of paper"
            );
            sugarCane.discard();

            ItemEntity coal = helper.spawnItem(Items.COAL, pressPos.above());
            boolean coalApplied = press.tryProcessInWorld(coal, false);
            helper.assertTrue(coalApplied, "roll " + i + ": the press did not find this mod's coal recipe");
            RecipeAssertions.assertExpectedOutcomeAndCount(helper, coal.getItem(), 1, 1, 1);
            coal.discard();
        }
        helper.succeed();
    }
}
