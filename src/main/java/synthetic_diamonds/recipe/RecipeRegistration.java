package synthetic_diamonds.recipe;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import synthetic_diamonds.SyntheticDiamonds;

/**
 * Registers {@code synthetic_diamonds:weighted_pressing} into vanilla's own recipe serializer
 * registry (`docs/spec/contracts/public-surface.md` `SURFACE-REQ-001`) — an ordinary
 * {@code Registry.register} call, no Create Fly API and no {@code RecipeType} registration of its
 * own (`docs/spec/domains/roll.md` `ROLL-REQ-005`).
 */
public final class RecipeRegistration {
    private RecipeRegistration() {
    }

    public static void register() {
        Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            Identifier.fromNamespaceAndPath(SyntheticDiamonds.MOD_ID, "weighted_pressing"),
            WeightedPressingRecipeSerializer.SERIALIZER
        );
    }
}
