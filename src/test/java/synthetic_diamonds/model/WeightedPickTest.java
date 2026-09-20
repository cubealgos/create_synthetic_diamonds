package synthetic_diamonds.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import synthetic_diamonds.model.WeightedPick.Outcome;

/**
 * The weighted-pick bucket boundaries (`docs/spec/domains/roll.md` `ROLL-REQ-001`), including the
 * exactly-0 and just-under-1.0 edges, and the normalization/sum-to-zero cases (`ROLL-REQ-003`,
 * `ROLL-FAIL-001`, `ROLL-FAIL-002`) (`docs/spec/operations/testing.md`).
 */
final class WeightedPickTest {
    private static final Consumer<String> NO_WARNING = message -> {
        throw new AssertionError("unexpected normalization warning: " + message);
    };

    // 0.25 / 0.25 / 0.5 are exact binary fractions, so the boundaries below are exact in float
    // arithmetic and not an artifact of rounding.
    private static final float DIAMOND_WEIGHT = 0.25f;
    private static final float FLINT_WEIGHT = 0.25f;
    private static final float GUNPOWDER_WEIGHT = 0.5f;

    private static WeightedPick evenThirds() {
        return WeightedPick.of(DIAMOND_WEIGHT, FLINT_WEIGHT, GUNPOWDER_WEIGHT, NO_WARNING);
    }

    @Test
    void rollOfExactlyZeroLandsOnDiamond() {
        assertEquals(Outcome.DIAMOND, evenThirds().pick(0f));
    }

    @Test
    void rollJustBelowTheDiamondBoundaryLandsOnDiamond() {
        assertEquals(Outcome.DIAMOND, evenThirds().pick(Math.nextDown(DIAMOND_WEIGHT)));
    }

    @Test
    void rollExactlyAtTheDiamondBoundaryLandsOnFlint() {
        // [w_diamond, w_diamond + w_flint) is flint's half-open range: the boundary itself is flint.
        assertEquals(Outcome.FLINT, evenThirds().pick(DIAMOND_WEIGHT));
    }

    @Test
    void rollJustBelowTheFlintBoundaryLandsOnFlint() {
        assertEquals(Outcome.FLINT, evenThirds().pick(Math.nextDown(DIAMOND_WEIGHT + FLINT_WEIGHT)));
    }

    @Test
    void rollExactlyAtTheFlintBoundaryLandsOnGunpowder() {
        // [w_diamond + w_flint, 1.0) is gunpowder's half-open range: the boundary itself is gunpowder.
        assertEquals(Outcome.GUNPOWDER, evenThirds().pick(DIAMOND_WEIGHT + FLINT_WEIGHT));
    }

    @Test
    void theLargestFloatBelowOneLandsOnGunpowder() {
        assertEquals(Outcome.GUNPOWDER, evenThirds().pick(Math.nextDown(1.0f)));
    }

    @Test
    void aZeroDiamondWeightNeverLandsOnDiamond() {
        // 0 / 0.25 / 0.75 sums to exactly 1.0 in float arithmetic, unlike 0.95 + 0.05.
        WeightedPick pick = WeightedPick.of(0f, 0.25f, 0.75f, NO_WARNING);
        assertEquals(Outcome.FLINT, pick.pick(0f));
    }

    @Test
    void weightsThatAlreadySumToOneAreNotReportedAsNormalized() {
        // NO_WARNING throws if invoked; reaching this line at all is the assertion.
        evenThirds();
    }

    @Test
    void weightsThatDoNotSumToOneAreNormalizedProportionallyAndReportedOnce() {
        List<String> warnings = new ArrayList<>();
        // 1/1/2 sums to 4, not 1.0; normalized proportionally it is the same 0.25/0.25/0.5 shape.
        WeightedPick pick = WeightedPick.of(1f, 1f, 2f, warnings::add);

        assertEquals(1, warnings.size(), "exactly one normalization warning");
        assertEquals(Outcome.DIAMOND, pick.pick(0f));
        assertEquals(Outcome.FLINT, pick.pick(0.25f));
        assertEquals(Outcome.GUNPOWDER, pick.pick(0.5f));
        assertEquals(Outcome.GUNPOWDER, pick.pick(Math.nextDown(1.0f)));
    }

    @Test
    void weightsSummingToExactlyZeroAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> WeightedPick.of(0f, 0f, 0f, NO_WARNING));
    }

    @Test
    void theRejectionMessageNamesTheFailureMode() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> WeightedPick.of(0f, 0f, 0f, NO_WARNING));
        assertTrue(e.getMessage().contains("ROLL-FAIL-002"), "message names ROLL-FAIL-002, but was: " + e.getMessage());
    }
}
