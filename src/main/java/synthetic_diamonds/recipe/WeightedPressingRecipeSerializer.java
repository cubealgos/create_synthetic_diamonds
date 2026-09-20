package synthetic_diamonds.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * The codec for {@code synthetic_diamonds:weighted_pressing} (`docs/spec/contracts/public-surface.md`
 * `SURFACE-REQ-001`): {@code ingredient} plus the six {@code <outcome>_chance}/{@code <outcome>_count}
 * fields named in `docs/spec/domains/recipe.md` §3, confirmed unchanged by this ticket.
 */
public final class WeightedPressingRecipeSerializer {
    public static final RecipeSerializer<WeightedPressingRecipe> SERIALIZER = new RecipeSerializer<>(
        RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.CODEC.fieldOf("ingredient").forGetter(WeightedPressingRecipe::ingredient),
            Codec.FLOAT.fieldOf("diamond_chance").forGetter(WeightedPressingRecipe::diamondChance),
            Codec.INT.fieldOf("diamond_count").forGetter(WeightedPressingRecipe::diamondCount),
            Codec.FLOAT.fieldOf("flint_chance").forGetter(WeightedPressingRecipe::flintChance),
            Codec.INT.fieldOf("flint_count").forGetter(WeightedPressingRecipe::flintCount),
            Codec.FLOAT.fieldOf("gunpowder_chance").forGetter(WeightedPressingRecipe::gunpowderChance),
            Codec.INT.fieldOf("gunpowder_count").forGetter(WeightedPressingRecipe::gunpowderCount)
        ).apply(instance, WeightedPressingRecipe::new)),
        StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, WeightedPressingRecipe::ingredient,
            ByteBufCodecs.FLOAT, WeightedPressingRecipe::diamondChance,
            ByteBufCodecs.INT, WeightedPressingRecipe::diamondCount,
            ByteBufCodecs.FLOAT, WeightedPressingRecipe::flintChance,
            ByteBufCodecs.INT, WeightedPressingRecipe::flintCount,
            ByteBufCodecs.FLOAT, WeightedPressingRecipe::gunpowderChance,
            ByteBufCodecs.INT, WeightedPressingRecipe::gunpowderCount,
            WeightedPressingRecipe::new
        )
    );

    private WeightedPressingRecipeSerializer() {
    }
}
