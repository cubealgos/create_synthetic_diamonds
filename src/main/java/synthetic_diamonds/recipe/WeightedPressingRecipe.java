package synthetic_diamonds.recipe;

import com.zurrtum.create.AllRecipeTypes;
import com.zurrtum.create.foundation.recipe.CreateRollableRecipe;
import java.util.List;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import synthetic_diamonds.SyntheticDiamonds;
import synthetic_diamonds.model.WeightedPick;
import synthetic_diamonds.model.WeightedPick.Outcome;

/**
 * The exclusive-roll pressing recipe (`docs/spec/domains/roll.md` `ROLL-REQ-001`,
 * `ROLL-REQ-002`): {@code getType()} returns the literal {@code AllRecipeTypes.PRESSING} static
 * field, so a real {@code MechanicalPressBlockEntity} finds this class through its ordinary
 * {@code RecipeManager}/{@code RecipeMap} lookup with zero mixin (`docs/spec/04-architecture.md`
 * `ARCH-DEC-001`, `ARCH-DEC-002`), and {@code assemble} performs one weighted pick over
 * diamond/flint/gunpowder in place of Create Fly's shared independent-roll default. Implements
 * {@link CreateRollableRecipe} — not just vanilla {@code Recipe} — because
 * {@code MechanicalPressBlockEntity.tryProcessInWorld}/{@code tryProcessOnBelt} checkcast the
 * looked-up recipe to that interface before calling {@code RecipeApplier.applyRecipeOn} (see this
 * ticket's `## Findings`).
 */
public final class WeightedPressingRecipe implements CreateRollableRecipe<SingleRecipeInput> {
    private final Ingredient ingredient;
    private final float diamondChance;
    private final int diamondCount;
    private final float flintChance;
    private final int flintCount;
    private final float gunpowderChance;
    private final int gunpowderCount;
    private final WeightedPick pick;

    public WeightedPressingRecipe(
        Ingredient ingredient,
        float diamondChance,
        int diamondCount,
        float flintChance,
        int flintCount,
        float gunpowderChance,
        int gunpowderCount
    ) {
        this.ingredient = ingredient;
        this.diamondChance = diamondChance;
        this.diamondCount = diamondCount;
        this.flintChance = flintChance;
        this.flintCount = flintCount;
        this.gunpowderChance = gunpowderChance;
        this.gunpowderCount = gunpowderCount;
        // Normalized once, at recipe load (this constructor runs once per decode), so a
        // mis-summed datapack recipe logs its warning once per recipe, not once per press cycle
        // (ROLL-REQ-003).
        this.pick = WeightedPick.of(diamondChance, flintChance, gunpowderChance,
            warning -> SyntheticDiamonds.LOGGER.warn("{}: {}", ingredient, warning));
    }

    public Ingredient ingredient() {
        return ingredient;
    }

    public float diamondChance() {
        return diamondChance;
    }

    public int diamondCount() {
        return diamondCount;
    }

    public float flintChance() {
        return flintChance;
    }

    public int flintCount() {
        return flintCount;
    }

    public float gunpowderChance() {
        return gunpowderChance;
    }

    public int gunpowderCount() {
        return gunpowderCount;
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return ingredient.test(input.item());
    }

    @Override
    public List<ItemStack> assemble(SingleRecipeInput input, RandomSource random) {
        Outcome outcome = pick.pick(random.nextFloat());
        return List.of(switch (outcome) {
            case DIAMOND -> new ItemStack(Items.DIAMOND, diamondCount);
            case FLINT -> new ItemStack(Items.FLINT, flintCount);
            case GUNPOWDER -> new ItemStack(Items.GUNPOWDER, gunpowderCount);
        });
    }

    @Override
    public RecipeSerializer<WeightedPressingRecipe> getSerializer() {
        return WeightedPressingRecipeSerializer.SERIALIZER;
    }

    @Override
    @SuppressWarnings("unchecked")
    public RecipeType<WeightedPressingRecipe> getType() {
        // The literal static field, not a new registration (ROLL-REQ-005): generics erase at
        // runtime, so this cast only satisfies the compiler, matching `PressingRecipe`'s own
        // getType() target exactly.
        return (RecipeType<WeightedPressingRecipe>) (RecipeType<?>) AllRecipeTypes.PRESSING;
    }
}
