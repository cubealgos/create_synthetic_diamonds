package synthetic_diamonds.gametest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

/**
 * SD-4's two acceptance-criteria game tests for {@code synthetic_diamonds.debug.DebugCommand}, run
 * in the game test environment, which is itself a development environment
 * ({@code FabricLoader.isDevelopmentEnvironment()} is true under {@code runGameTest}, exactly as
 * under {@code runClient}), so the command is registered and reachable here — the same finding
 * {@code create_villager_customers}'s own {@code DebugCommandGameTest} (`VC-5`) records:
 * registration itself, gated on that one line in {@code SyntheticDiamonds.onInitialize}, is proven
 * by code review, not a game test, since there is no development/non-development pair of
 * environments a single test run can compare.
 *
 * <p>Every test runs the real command through {@code MinecraftServer.getCommands()
 * .performPrefixedCommand}, against a {@link CommandSourceStack} built with a capturing
 * {@link CommandSource} so the feedback {@code DebugCommand} sends can be asserted on directly, by
 * the translation key and arguments of the {@link TranslatableContents} it carries — robust to a
 * dedicated server's lack of client-side localisation, unlike asserting on the rendered English
 * text.
 *
 * <p>Both tests run against {@code data/synthetic_diamonds_gametest/recipe/weighted_pressing/}'s
 * datapack recipes shipped only in this game-test source set: {@code test_diamond_always.json}
 * (SD-2's own, diamond weight 1.0 for charcoal) and, for the no-recipe case, an item
 * ({@code minecraft:diamond}) this suite ships no {@code weighted_pressing} recipe for at all.
 */
public final class DebugCommandGameTest {
    @GameTest
    public void pressTalliesAllDiamondsAgainstAnAlwaysDiamondRecipe(GameTestHelper helper) {
        CapturingSource capturing = new CapturingSource();
        CommandSourceStack source = sourceFor(helper, capturing);

        helper.getLevel().getServer().getCommands().performPrefixedCommand(source, "synthetic_diamonds debug press 50 minecraft:charcoal");

        helper.assertTrue(
            capturing.hasKey("command.synthetic_diamonds.debug.press.tally"),
            "press printed a tally against the always-diamond test recipe: " + capturing.describe()
        );
        helper.assertTrue(
            capturing.argsOf("command.synthetic_diamonds.debug.press.tally")
                .equals(List.of("50", "0", "0", "50", "1.000", "0.000", "0.000")),
            "diamond weight 1.0 tallies 50 diamonds, 0 flint, 0 gunpowder out of 50: " + capturing.describe()
        );
        helper.succeed();
    }

    @GameTest
    public void pressReportsNoRecipeForAnItemWithNoWeightedPressingRecipe(GameTestHelper helper) {
        CapturingSource capturing = new CapturingSource();
        CommandSourceStack source = sourceFor(helper, capturing);

        helper.getLevel().getServer().getCommands().performPrefixedCommand(source, "synthetic_diamonds debug press 10 minecraft:diamond");

        helper.assertTrue(
            capturing.hasKey("command.synthetic_diamonds.debug.press.no_recipe"),
            "an item with no weighted_pressing recipe reports the no_recipe error: " + capturing.describe()
        );
        helper.succeed();
    }

    /** A {@link CommandSourceStack} standing at the test structure's origin, feeding {@code capturing}. */
    private static CommandSourceStack sourceFor(GameTestHelper helper, CapturingSource capturing) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(new BlockPos(1, 2, 1));
        return new CommandSourceStack(
            capturing, Vec3.atCenterOf(pos), Vec2.ZERO, level, PermissionSet.ALL_PERMISSIONS, "DebugCommandGameTest",
            Component.literal("DebugCommandGameTest"), level.getServer(), null
        );
    }

    /**
     * A {@link CommandSource} that records every message sent to it instead of delivering it
     * anywhere, so a game test can assert on {@code DebugCommand}'s feedback without a real player
     * — by translation key and argument list rather than rendered text, since a dedicated server
     * does not localise ({@link TranslatableContents#getKey()}/{@link TranslatableContents#getArgs()}
     * stay resolvable there even though {@code getString()} would not reliably reflect {@code
     * en_us.json}).
     */
    private static final class CapturingSource implements CommandSource {
        private final List<Component> messages = new ArrayList<>();

        @Override
        public void sendSystemMessage(Component component) {
            messages.add(component);
        }

        @Override
        public boolean acceptsSuccess() {
            return true;
        }

        @Override
        public boolean acceptsFailure() {
            return true;
        }

        @Override
        public boolean shouldInformAdmins() {
            return false;
        }

        boolean hasKey(String key) {
            return translatables().anyMatch(t -> t.getKey().equals(key));
        }

        List<String> argsOf(String key) {
            return translatables().filter(t -> t.getKey().equals(key))
                .findFirst()
                .map(t -> Arrays.stream(t.getArgs()).map(String::valueOf).toList())
                .orElse(List.of());
        }

        String describe() {
            return translatables().map(t -> t.getKey() + Arrays.toString(t.getArgs())).toList().toString();
        }

        /**
         * Every {@link TranslatableContents} reachable from a captured message, including a
         * {@code CommandSyntaxException}'s own rendering: {@code performPrefixedCommand} reports a
         * thrown exception as an empty top-level component carrying the real translatable as a
         * <em>sibling</em>, not as its own contents, so a flat top-level check alone misses it.
         */
        private Stream<TranslatableContents> translatables() {
            return messages.stream().flatMap(CapturingSource::translatablesOf);
        }

        private static Stream<TranslatableContents> translatablesOf(Component component) {
            Stream<TranslatableContents> own =
                component.getContents() instanceof TranslatableContents t ? Stream.of(t) : Stream.of();
            return Stream.concat(own, component.getSiblings().stream().flatMap(CapturingSource::translatablesOf));
        }
    }
}
