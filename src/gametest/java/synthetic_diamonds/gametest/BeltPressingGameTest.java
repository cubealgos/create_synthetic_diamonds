package synthetic_diamonds.gametest;

import com.zurrtum.create.AllBlocks;
import com.zurrtum.create.content.kinetics.belt.transport.TransportedItemStack;
import com.zurrtum.create.content.kinetics.press.MechanicalPressBlockEntity;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * The belt-mode pressing path (`docs/spec/domains/roll.md` `ROLL-REQ-006`: "identically whether
 * the press runs in belt mode or world/depot mode"), driven headless exactly as
 * {@code ShippedRecipesDepotGameTest} drives world/depot mode: this ticket's {@code javap -p -c} of
 * {@code MechanicalPressBlockEntity.tryProcessOnBelt(TransportedItemStack, List)} shows its own
 * recipe lookup, roll and item-pressed bookkeeping never touch belt physics, kinetic speed or
 * power state — the same finding SD-2 recorded for {@code tryProcessInWorld} — so calling it
 * directly on a real, placed press still exercises the real belt lookup-and-roll path without
 * building and powering an actual belt.
 *
 * <p>Unlike {@code tryProcessInWorld}, {@code tryProcessOnBelt} does not mutate the input {@link
 * TransportedItemStack} in place; it appends the roll's result to the caller-supplied output list
 * instead (confirmed by the same disassembly), so each roll here reads its outcome off that list.
 */
public final class BeltPressingGameTest {
    private static final int ROLLS_PER_RECIPE = 50;

    @GameTest
    public void charcoalOnABeltAlwaysYieldsExactlyOneOutcome(GameTestHelper helper) {
        pressManyTimesOnBelt(helper, Items.CHARCOAL, 1, 1, 1);
        helper.succeed();
    }

    @GameTest
    public void coalOnABeltAlwaysYieldsExactlyOneOutcome(GameTestHelper helper) {
        pressManyTimesOnBelt(helper, Items.COAL, 1, 1, 1);
        helper.succeed();
    }

    @GameTest
    public void coalBlockOnABeltAlwaysYieldsASingleStackOfNineFlintNineGunpowderOrOneDiamond(GameTestHelper helper) {
        pressManyTimesOnBelt(helper, Items.COAL_BLOCK, 1, 9, 9);
        helper.succeed();
    }

    private static void pressManyTimesOnBelt(GameTestHelper helper, Item ingredient, int diamondCount, int flintCount, int gunpowderCount) {
        BlockPos pressPos = new BlockPos(1, 1, 1);
        helper.setBlock(pressPos, AllBlocks.MECHANICAL_PRESS.defaultBlockState());
        MechanicalPressBlockEntity press = helper.getBlockEntity(pressPos, MechanicalPressBlockEntity.class);

        for (int i = 0; i < ROLLS_PER_RECIPE; i++) {
            TransportedItemStack transported = new TransportedItemStack(new ItemStack(ingredient, 1));
            List<ItemStack> output = new ArrayList<>();
            boolean applied = press.tryProcessOnBelt(transported, output);
            helper.assertTrue(applied, "roll " + i + ": the belt press did not find a recipe for " + ingredient);
            RecipeAssertions.assertSingleExclusiveOutcome(helper, output, diamondCount, flintCount, gunpowderCount);
        }
    }
}
