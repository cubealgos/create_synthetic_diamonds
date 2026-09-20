package synthetic_diamonds.debug;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.zurrtum.create.AllRecipeTypes;
import com.zurrtum.create.content.kinetics.press.PressingRecipe;
import java.util.Locale;
import java.util.Optional;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import synthetic_diamonds.SyntheticDiamonds;
import synthetic_diamonds.model.WeightedPick;
import synthetic_diamonds.recipe.WeightedPressingRecipe;

/**
 * Development-only: {@code /synthetic_diamonds debug press <count> [<item>]} looks up the live
 * {@code synthetic_diamonds:weighted_pressing} recipe for {@code item} (charcoal, {@code
 * ROLL-REQ-004}'s "identical parameter" default, when omitted) through the same {@code
 * RecipeManager}/{@code AllRecipeTypes.PRESSING} lookup a real {@code
 * MechanicalPressBlockEntity} uses ({@code synthetic_diamonds.recipe.WeightedPressingRecipe}'s own
 * Javadoc; `docs/spec/domains/roll.md` {@code ROLL-REQ-005}), rolls {@link WeightedPick#pick}
 * {@code count} times against that recipe's own weights using the level's {@link RandomSource},
 * and prints the tally — makes the exclusivity guarantee ({@code ROLL-REQ-001}) and the
 * real-world odds directly observable without waiting through hundreds of real 240-tick press
 * cycles (`docs/spec/operations/testing.md`'s "Development tool" row; SD-4). No new screen,
 * tooltip, item or persisted state ({@code UI-REQ-001}) — a command only. Registered only when
 * Fabric reports a development environment; never present in a released jar (mirrors {@code
 * create_metered_motor}'s {@code MM-8} and {@code create_villager_customers}'s {@code VC-5}).
 */
public final class DebugCommand {
    private static final DynamicCommandExceptionType NO_RECIPE = new DynamicCommandExceptionType(
        item -> Component.translatable("command.synthetic_diamonds.debug.press.no_recipe", item));

    private DebugCommand() {
    }

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, context, selection) -> dispatcher.register(
            Commands.literal(SyntheticDiamonds.MOD_ID).then(Commands.literal("debug")
                .then(Commands.literal("press")
                    .then(Commands.argument("count", IntegerArgumentType.integer(1, 100000))
                        .executes(c -> press(c.getSource(), IntegerArgumentType.getInteger(c, "count"), Items.CHARCOAL))
                        .then(Commands.argument("item", ItemArgument.item(context))
                            .executes(c -> press(c.getSource(), IntegerArgumentType.getInteger(c, "count"), itemOf(c)))))))));
    }

    private static Item itemOf(CommandContext<CommandSourceStack> context) {
        ItemInput input = ItemArgument.getItem(context, "item");
        return input.item().value();
    }

    /**
     * Looks up {@code synthetic_diamonds:weighted_pressing}'s live recipe for {@code item} the
     * same way {@code WeightedPressingRecipe}'s own Javadoc documents {@code
     * MechanicalPressBlockEntity.getRecipe} doing it — {@code ServerLevel.recipeAccess()
     * .getRecipeFor(AllRecipeTypes.PRESSING, ...)} — then rolls {@link WeightedPick#pick} {@code
     * count} times against that recipe's own (unnormalized) chance fields and reports the tally.
     */
    static int press(CommandSourceStack source, int count, Item item) throws CommandSyntaxException {
        ServerLevel level = source.getLevel();
        ItemStack stack = new ItemStack(item);
        Optional<RecipeHolder<PressingRecipe>> holder =
            level.recipeAccess().getRecipeFor(AllRecipeTypes.PRESSING, new SingleRecipeInput(stack), level);
        // Widened to the Recipe<?> interface, never to the unrelated PressingRecipe class: javac
        // rejects `instanceof WeightedPressingRecipe` directly against a PressingRecipe-typed
        // value as statically inconvertible, and at runtime PressingRecipe and
        // WeightedPressingRecipe share no relation either (see WeightedPressingRecipe's own
        // Javadoc on why MechanicalPressBlockEntity itself checkcasts to CreateRollableRecipe,
        // never PressingRecipe).
        Recipe<?> found = holder.isEmpty() ? null : holder.get().value();
        if (!(found instanceof WeightedPressingRecipe recipe)) {
            throw NO_RECIPE.create(BuiltInRegistries.ITEM.getKey(item));
        }

        // A fresh WeightedPick, not the recipe's own private one (ROLL-REQ-003): the recipe
        // already normalized and logged once at load, so this reconstruction is silent.
        WeightedPick pick = WeightedPick.of(recipe.diamondChance(), recipe.flintChance(), recipe.gunpowderChance(), warning -> {
        });
        RandomSource random = level.getRandom();
        int diamonds = 0;
        int flints = 0;
        int gunpowders = 0;
        for (int i = 0; i < count; i++) {
            switch (pick.pick(random.nextFloat())) {
                case DIAMOND -> diamonds++;
                case FLINT -> flints++;
                case GUNPOWDER -> gunpowders++;
            }
        }

        int diamondTally = diamonds;
        int flintTally = flints;
        int gunpowderTally = gunpowders;
        source.sendSuccess(() -> Component.translatable(
            "command.synthetic_diamonds.debug.press.tally", diamondTally, flintTally, gunpowderTally, count,
            format(recipe.diamondChance()), format(recipe.flintChance()), format(recipe.gunpowderChance())), false);
        return count;
    }

    private static String format(float weight) {
        return String.format(Locale.ROOT, "%.3f", weight);
    }
}
