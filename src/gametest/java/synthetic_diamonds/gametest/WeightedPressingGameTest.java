package synthetic_diamonds.gametest;

import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.content.kinetics.press.MechanicalPressBlockEntity;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Items;

/**
 * Proves a real {@code MechanicalPressBlockEntity} finds and runs this mod's
 * {@code synthetic_diamonds:weighted_pressing} recipe class through its ordinary
 * {@code RecipeManager}/{@code RecipeMap} lookup, with zero mixin (`docs/spec/domains/roll.md`
 * `ROLL-REQ-002`, `ROLL-REQ-005`; `docs/spec/04-architecture.md` `ARCH-DEC-002`).
 *
 * <p>Drives {@link MechanicalPressBlockEntity#tryProcessInWorld} directly — the same method
 * {@code PressingBehaviour.tick()} calls once a real press's cycle completes — rather than
 * building and powering a real kinetic network (water wheel, shafts, ...): this ticket's
 * {@code javap -p -c} of {@code tryProcessInWorld} (recorded in the ticket's {@code ## Findings})
 * shows its recipe lookup and roll never touch {@code getKineticSpeed()}/{@code canProcessInBulk()}
 * or any power state before applying the recipe, so calling it directly on a real, placed block
 * entity still exercises the real lookup-and-roll path end to end, just without simulating RPM.
 *
 * <p>The test recipe (diamond weight 1.0, deterministic) is a datapack shipped only in this
 * game-test source set ({@code data/synthetic_diamonds_gametest/recipe/weighted_pressing/}), not
 * a mod-shipped recipe — those three (charcoal/coal/coal_block) are SD-3's scope.
 *
 * <p>Its ingredient is {@code minecraft:blaze_powder}, not {@code minecraft:charcoal} as SD-2
 * first wrote it (SD-3 finding): once SD-3 ships a real {@code synthetic_diamonds:weighted_pressing/charcoal}
 * recipe, the game-test environment would hold two recipes both matching {@code minecraft:charcoal}
 * under {@code AllRecipeTypes.PRESSING} at once (this class's own always-diamond fixture and the
 * shipped ~0.5%-diamond recipe), and {@code RecipeMap.getRecipesFor(...).findFirst()} picks
 * whichever one the data pack happened to load first — insertion order into the underlying
 * {@code ImmutableMultimap}, not alphabetical or otherwise specified (`RecipeMap.create`,
 * confirmed by `javap -p -c`). Keeping this fixture's own ingredient disjoint from every
 * mod-shipped recipe's ingredient avoids relying on that unspecified order for this test's own
 * determinism, matching `docs/spec/domains/recipe.md` `RECIPE-FAIL-004`'s own point that ordinary
 * vanilla conflict resolution is not special-cased by this mod — including for this mod's own two
 * recipes.
 */
public final class WeightedPressingGameTest {
    @GameTest
    public void aRealPressFindsAndRunsTheWeightedPressingRecipe(GameTestHelper helper) {
        BlockPos pressPos = new BlockPos(1, 1, 1);
        helper.setBlock(pressPos, AllBlocks.MECHANICAL_PRESS.defaultBlockState());
        MechanicalPressBlockEntity press = helper.getBlockEntity(pressPos, MechanicalPressBlockEntity.class);

        // spawnItem(Item, BlockPos) translates relative-to-absolute internally (it funnels through
        // spawnItem(Item, Vec3)'s absoluteVec call) — a test-relative BlockPos here, not an
        // already-absolute one (helper.absolutePos would double-translate it).
        ItemEntity blazePowder = helper.spawnItem(Items.BLAZE_POWDER, pressPos.above());

        boolean applied = press.tryProcessInWorld(blazePowder, false);
        helper.assertTrue(applied, "the press found and applied the weighted pressing recipe");
        // A count-1 stack is pressed in place (RecipeApplier.applyRecipeOn(ItemEntity, ...) mutates
        // the same entity's stack rather than spawning a new one, confirmed by this ticket's
        // javap -p -c of tryProcessInWorld), so the same entity now holds the diamond.
        helper.assertTrue(blazePowder.getItem().is(Items.DIAMOND), "the pressed item became a diamond, but was " + blazePowder.getItem());
        helper.succeed();
    }
}
