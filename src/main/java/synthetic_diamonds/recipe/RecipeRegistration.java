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
 *
 * <p>The three shipped recipe instances this serializer decodes live under
 * {@code src/main/resources/data/synthetic_diamonds/recipe/weighted_pressing/}: {@code
 * charcoal.json} (`RECIPE-REQ-001`), {@code coal.json} (`RECIPE-REQ-002`), and {@code
 * coal_block.json`}, matched via the {@code c:storage_blocks/coal} item tag (`RECIPE-REQ-003`).
 * There is deliberately no fourth {@code charcoal_block.json}: neither vanilla nor Create Fly
 * defines a charcoal block to press, so `RECIPE-REQ-004` has nothing to add a recipe for — this is
 * not an oversight.
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
