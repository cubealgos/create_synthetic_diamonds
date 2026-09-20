package synthetic_diamonds.model;

import java.util.function.Consumer;

/**
 * The exclusive weighted pick over {@link Outcome#DIAMOND}, {@link Outcome#FLINT} and
 * {@link Outcome#GUNPOWDER} (`docs/spec/domains/roll.md` {@code ROLL-REQ-001}): given three
 * weights, one {@code float} roll lands in exactly one of the three buckets, never zero and never
 * two. Pure: no Minecraft, Fabric or Create import (enforced by {@code verifyPurePackage}).
 */
public final class WeightedPick {

    /** The three mutually exclusive outcomes a pressing roll can produce (`domains/roll.md` §3). */
    public enum Outcome {
        DIAMOND,
        FLINT,
        GUNPOWDER
    }

    private final float diamondWeight;
    private final float flintWeight;

    private WeightedPick(float diamondWeight, float flintWeight) {
        this.diamondWeight = diamondWeight;
        this.flintWeight = flintWeight;
    }

    /**
     * Builds a pick from three weights: each is first clamped into {@code [0, 1]}
     * (`docs/spec/domains/recipe.md` `RECIPE-FAIL-003`), reporting that through
     * {@code onNormalized} once per clamped weight, then the (possibly clamped) three are
     * normalized to sum to exactly 1.0 when they do not (`ROLL-REQ-003`), reported through the
     * same consumer once more. Nothing is logged from this pure class directly; {@code
     * onNormalized} is the sole channel for both kinds of message.
     *
     * @param diamondWeight the diamond bucket's share of {@code [0, 1)}, before clamping or
     *     normalization
     * @param flintWeight the flint bucket's share, before clamping or normalization
     * @param gunpowderWeight the gunpowder bucket's share, before clamping or normalization
     * @param onNormalized called once per weight clamped into range, and at most once more when
     *     the (post-clamp) three weights do not already sum to exactly 1.0, each time with a
     *     human-readable message
     * @return a pick whose three weights sum to exactly 1.0
     * @throws IllegalArgumentException if the three weights, after clamping, sum to exactly zero
     *     and therefore cannot be normalized (`ROLL-FAIL-002`)
     */
    public static WeightedPick of(float diamondWeight, float flintWeight, float gunpowderWeight, Consumer<String> onNormalized) {
        diamondWeight = clamp(diamondWeight, "diamond", onNormalized);
        flintWeight = clamp(flintWeight, "flint", onNormalized);
        gunpowderWeight = clamp(gunpowderWeight, "gunpowder", onNormalized);

        double sum = (double) diamondWeight + (double) flintWeight + (double) gunpowderWeight;
        if (sum == 0.0) {
            throw new IllegalArgumentException(
                "weights " + diamondWeight + "/" + flintWeight + "/" + gunpowderWeight + " sum to exactly zero and cannot be normalized (ROLL-FAIL-002)");
        }
        if (sum != 1.0) {
            onNormalized.accept("weights " + diamondWeight + "/" + flintWeight + "/" + gunpowderWeight + " sum to " + sum
                + ", not 1.0; normalizing proportionally (ROLL-REQ-003)");
            diamondWeight = (float) (diamondWeight / sum);
            flintWeight = (float) (flintWeight / sum);
        }
        return new WeightedPick(diamondWeight, flintWeight);
    }

    /**
     * Clamps a single weight into {@code [0, 1]} (`RECIPE-FAIL-003`), reporting the clamp through
     * {@code onClamped} exactly when {@code weight} was out of range; a weight already in range is
     * returned unchanged, with no message.
     */
    private static float clamp(float weight, String label, Consumer<String> onClamped) {
        if (weight < 0f) {
            onClamped.accept(label + " weight " + weight + " is negative; clamped to 0 (RECIPE-FAIL-003)");
            return 0f;
        }
        if (weight > 1f) {
            onClamped.accept(label + " weight " + weight + " is greater than 1; clamped to 1 (RECIPE-FAIL-003)");
            return 1f;
        }
        return weight;
    }

    /**
     * Partitions {@code [0, 1)} into {@code [0, w_diamond)} / {@code [w_diamond, w_diamond +
     * w_flint)} / {@code [w_diamond + w_flint, 1.0)} and returns which bucket {@code roll} lands
     * in; there is no fourth "nothing" bucket (`domains/roll.md` §3).
     *
     * @param roll a value drawn from {@code [0, 1)}, e.g. {@code RandomSource.nextFloat()}
     */
    public Outcome pick(float roll) {
        if (roll < diamondWeight) {
            return Outcome.DIAMOND;
        }
        if (roll < diamondWeight + flintWeight) {
            return Outcome.FLINT;
        }
        return Outcome.GUNPOWDER;
    }
}
