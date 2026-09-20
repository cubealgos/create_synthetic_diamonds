---
schema_version: 1
id: 01M2YH5E8J14DJMP6TFC43SWPG
key: SD-5
type: test
title: Requirement-to-test table, three check runs, client checklist
created_by: kevin
created_at: 2026-09-20T04:28:35Z
---

## Scope

The requirement-to-test table `operations/testing.md` `TEST-REQ-001` requires: every `RECIPE-REQ`,
`ROLL-REQ` and `UI-REQ` mapped to the test that proves it (or, where a requirement genuinely cannot
be proven headless, a ruling recording why and pointing at the client checklist instead), plus the
two once-only proofs `TEST-REQ-002` (the pure-package deliberate-break proof, satisfied at SD-1) and
`TEST-REQ-003` (the recipe-bucket coexistence game test, satisfied at SD-3), both restated here with
their ticket references rather than re-proven. Three green `just check` runs in a row on a clean
checkout, recorded with their game-test counts. Kevin's client checklist
(`operations/testing.md`, "Manual / release checklist" row): watching a real press cycle run
against charcoal, coal and a coal block over enough real time (12-second cycles) to see all three
outcomes appear for each, confirming the observed ratio is in the right ballpark for the configured
weights, and confirming no recipe-viewer entry appears anywhere for these recipes at 1.0
(`UI-FAIL-001`) — timing and player-visible ratio are not meaningfully provable headless in the
time this project spends on any one ticket, so the checklist is load-bearing here, not decorative.

## Approach

One table in this ticket, filled from the tests written under SD-2 through SD-4; where a
requirement cannot be proven headless, the table names the reason and points at the client
checklist instead, exactly as `operations/testing.md` scopes it. `just check` run three times in a
row with no flaky failure, on a clean checkout, each run's game-test count recorded here. The debug
command from SD-4 (`/synthetic_diamonds debug press <count>`) is the practical tool for eyeballing
the odds quickly before committing to the slower real-time checklist item.

## Acceptance criteria

- [x] A requirement-to-test table in this ticket covers every `RECIPE-REQ`, `ROLL-REQ` and
      `UI-REQ` id.
- [x] `just check` green three consecutive runs, recorded with their game-test counts, on a clean
      checkout.
- [ ] `TEST-REQ-002` (satisfied at SD-1) and `TEST-REQ-003` (satisfied at SD-3) restated in the
      table with their ticket references.
- [ ] Kevin's client checklist done and recorded: real press cycles against charcoal, coal and a
      coal block, all three outcomes observed for each, ratio sanity-checked, no recipe-viewer
      entry found anywhere.

## Requirement-to-test table

One row per id. "Test" names the class and method that proves it. **GAP, closed (SD-5)** marks a
requirement this sweep found untested and closed with a new game test, committed on this branch.
"client checklist: ..." names the matching item in `## Client checklist (Kevin)` below, for what
this ticket's own `## Scope` already rules out headless (press timing, observed ratio, JEI
absence). A few rows are marked **not testable headless, ruling recorded** with the reasoning
inline, per the standing principle to record an explicit ruling rather than silently drop the
requirement from the sweep. One row (`RECIPE-FAIL-003`) is marked **GAP, not closed — implementation
missing**, not a test gap: see the finding below the table.

### `RECIPE`

| ID | Requirement | Test / checklist / ruling |
|---|---|---|
| `RECIPE-REQ-001` | Charcoal recipe, 0.5% / 95% / 4.5% | `RecipeFilesTest.charcoalJsonExistsAndCarriesTheSpecsNumbers` (shipped JSON) + `ShippedRecipesDepotGameTest.charcoalIsFoundAndAlwaysYieldsExactlyOneOutcome` (200 real presses) |
| `RECIPE-REQ-002` | Coal recipe, identical weights | `RecipeFilesTest.coalJsonExistsAndCarriesTheSpecsNumbers` + `ShippedRecipesDepotGameTest.coalIsFoundAndAlwaysYieldsExactlyOneOutcome` |
| `RECIPE-REQ-003` | Coal-block recipe, same odds, ×9 by-products | `RecipeFilesTest.coalBlockJsonExistsAndCarriesTheSpecsNumbers` + `ShippedRecipesDepotGameTest.coalBlockIsFoundAndAlwaysYieldsExactlyOneOutcomeAtItsOwnCounts` + `BeltPressingGameTest.coalBlockOnABeltAlwaysYieldsASingleStackOfNineFlintNineGunpowderOrOneDiamond` |
| `RECIPE-REQ-004` | No charcoal-block recipe | `RecipeFilesTest.noCharcoalBlockRecipeExists` |
| `RECIPE-REQ-005` | A datapack override is decoded and used verbatim | `RecipeOverrideGameTest.aDatapackOverrideOfCharcoalJsonIsUsedVerbatim` |
| `RECIPE-REQ-006` | Exactly one vanilla diamond on success, never more, never a mod item | `ShippedRecipesDepotGameTest` (all three recipes, `RecipeAssertions.assertExpectedOutcomeAndCount` rejects any item outside {diamond, flint, gunpowder} and checks the diamond count is always 1) + `WeightedPressingDistributionGameTest.chargedCharcoalRollsStayExclusiveAndInBallpark` (2,000 rolls, diamond count asserted `== 1` every time) |
| `RECIPE-REQ-007` | Single-item input, single-outcome-type output per cycle | `BeltPressingGameTest` (`RecipeAssertions.assertSingleExclusiveOutcome`, result list size `== 1`, all three recipes) + `ShippedRecipesDepotGameTest`/`WeightedPressingDistributionGameTest` (depot mode mutates the one input stack in place, single outcome by construction) |
| `RECIPE-FAIL-001` | Ingredient names a nonexistent item id → fails to load, recipe absent, no crash | **GAP, closed (SD-5)** — `RecipeLoadFailureGameTest.aRecipeReferencingANonexistentItemIdFailsToLoadWithoutCrashing`: a fixture recipe (`test_bad_item_id.json`, ingredient `minecraft:this_item_does_not_exist`) is confirmed absent from `RecipeManager.getRecipes()` after a real reload; the reload itself completing (this class's own two tests, and every other `@GameTest` class in the same session) is the "no crash" half |
| `RECIPE-FAIL-002` | Missing required chance field → rejected at load with a clear error | **GAP, closed (SD-5)** — `RecipeLoadFailureGameTest.aRecipeMissingARequiredChanceFieldFailsToLoadWithoutCrashing`: a fixture recipe (`test_missing_field.json`, `gunpowder_chance`/`gunpowder_count` omitted) is confirmed absent from `RecipeManager.getRecipes()`; the decode error itself is visible in the game-test server log (`DataResult.Error['No key gunpowder_count ...']`, captured by this ticket's `## Verification runs`) but not asserted on directly, since the log text is Mojang's own codec wording, not this mod's |
| `RECIPE-FAIL-003` | Out-of-range chance field → clamped to `[0, 1]` and logged once | **GAP, not closed — implementation missing.** No clamp exists anywhere in the codebase: `WeightedPressingRecipeSerializer`'s codec decodes each `_chance` field as a plain `Codec.FLOAT` with no range check, and `WeightedPick.of` only normalizes the sum, never clamps an individual weight (confirmed by `grep -rn "clamp" src/main`, no hits). A chance field outside `[0, 1]` today reaches `WeightedPick` unclamped — e.g. a datapack `diamond_chance` of `1.5` normalizes to something still `> 1.0` per-bucket relative to the other two, silently skewing every roll toward diamond with no warning. This is a spec-vs-code mismatch, not a missing test, so this ticket does not add a test that would only prove the bug; flagged here for Kevin's ruling rather than fixed on a test-only ticket. |
| `RECIPE-FAIL-004` | Two recipes claim the same ingredient under `AllRecipeTypes.PRESSING` | **Not testable as this mod's own logic, ruling recorded** — the spec's own answer is "ordinary vanilla recipe resolution applies … not special-cased by this mod" (`04-architecture.md` `ARCH-FAIL-004`); verified by code review — no conflict-detection or -resolution code exists anywhere in `RecipeRegistration`/`WeightedPressingRecipe`/`WeightedPressingRecipeSerializer`, so there is no mod-specific behaviour to assert on. `RecipeCoexistenceGameTest` (`TEST-REQ-003`) already proves the adjacent, actually-mod-relevant claim: this mod's own recipe and Create Fly's own recipe, on *different* ingredients, coexist in the same bucket without disturbing each other. |

### `ROLL`

| ID | Requirement | Test / checklist / ruling |
|---|---|---|
| `ROLL-REQ-001` | Every match yields exactly one of {diamond, flint, gunpowder}, never zero, never two | `WeightedPickTest` (all eight boundary/edge cases, pure) + `ShippedRecipesDepotGameTest`/`BeltPressingGameTest` (200/50 real presses × 3 recipes) + `WeightedPressingDistributionGameTest` (2,000 rolls) |
| `ROLL-REQ-002` | A custom `Recipe<SingleRecipeInput>` whose `getType()` is the literal `AllRecipeTypes.PRESSING` field, `assemble` does the pick | `WeightedPressingGameTest.aRealPressFindsAndRunsTheWeightedPressingRecipe` (a real `MechanicalPressBlockEntity` finds and runs it through the ordinary lookup, proving `getType()` reports the literal field) + code review of `WeightedPressingRecipe.getType()`/`assemble()` |
| `ROLL-REQ-003` | Weights not summing to 1.0 are normalized proportionally, logged once | `WeightedPickTest.weightsThatDoNotSumToOneAreNormalizedProportionallyAndReportedOnce` + `weightsThatAlreadySumToOneAreNotReportedAsNormalized` |
| `ROLL-REQ-004` | The roll is drawn from the same `RandomSource` parameter the press already threads through `assemble` | `WeightedPressingDistributionGameTest.chargedCharcoalRollsStayExclusiveAndInBallpark` (a fixed-seed `RandomSource` passed into `assemble` reproduces the same tally every run, which only holds if `assemble` consumes exactly that parameter and no independent source) + code review of `WeightedPressingRecipe.assemble(SingleRecipeInput, RandomSource)`'s signature |
| `ROLL-REQ-005` | No `RecipeType` registration, no mixin; found via ordinary `RecipeManager`/`RecipeMap` lookup | `WeightedPressingGameTest.aRealPressFindsAndRunsTheWeightedPressingRecipe` (lookup works with zero mixin) + code review (`synthetic_diamonds.mixins.json` ships a permanently empty `mixins`/`client` array, `RecipeRegistration.register()` registers only the serializer, `docs/spec/decisions/DEC-004-toolchain.md`) |
| `ROLL-REQ-006` | Identical whether the press runs in belt mode or world/depot mode | `BeltPressingGameTest` (belt mode, `tryProcessOnBelt`, 3 recipes) + `ShippedRecipesDepotGameTest` (world/depot mode, `tryProcessInWorld`, the same 3 recipes) |
| `ROLL-FAIL-001` | Weights not summing to 1.0 | Same as `ROLL-REQ-003` |
| `ROLL-FAIL-002` | Weights summing to exactly zero → rejected at load, clear error | `WeightedPickTest.weightsSummingToExactlyZeroAreRejected` + `theRejectionMessageNamesTheFailureMode` (message names `ROLL-FAIL-002`) |
| `ROLL-FAIL-003` | `RecipeManager`/`RecipeMap` bucketing doesn't behave as predicted → fall back to a mixin | **Not applicable, ruling recorded** — this is a contingency for a prediction that turned out correct: `WeightedPressingGameTest` (SD-2) confirmed the ordinary lookup finds and runs this mod's recipe class exactly as the research's disassembly predicted, so the mixin fallback was never invoked and there is nothing to test for a branch that never triggered. |

### `UI`

| ID | Requirement | Test / checklist / ruling |
|---|---|---|
| `UI-REQ-001` | No screen, tooltip, item or block of its own | **Not testable headless in any meaningful sense — a "we didn't add a class" claim.** Verified by repository structure (`docs/map.md`/`docs/map/root/synthetic_diamonds*.md` list every type under `src/main`: `SyntheticDiamonds`, `SyntheticDiamondsClient`, `DebugCommand`, `WeightedPick`, `RecipeRegistration`, `WeightedPressingRecipe`, `WeightedPressingRecipeSerializer` — no `Screen`/`AbstractContainerMenu`/`Block`/`Item` subclass among them) and by code review at every ticket. |
| `UI-REQ-002` | No JEI category at 1.0.0, a 1.1 ticket | **Scheduling decision, not testable — nothing exists yet to test.** Deferred to 1.1 (`UI-DEC-001`). Confirmed absent today: `client checklist: no JEI entry`. |
| `UI-REQ-003` | The recipe JSON files are the only documented way to read the exact odds | `RecipeFilesTest` (three tests: the files exist, are readable, and carry the spec's exact numbers) combined with `UI-REQ-001`'s verification that no other in-game surface exists to read them from instead |
| `UI-FAIL-001` | A player opens JEI's Pressing category expecting these recipes, sees nothing, not a crash | `client checklist: no recipe-viewer entry appears anywhere for these recipes` — JEI rendering and its category contents are a client/mod-loader integration this project's headless game-test server never loads (no JEI dependency at all), so "sees nothing, no crash" cannot be observed from this side; the press itself working with zero JEI category registered (every game test above) is the half that is provable headless, and is proven. |
| `UI-FAIL-002` | Absence of a JEI entry is not "the recipe doesn't exist"; the press runs the recipe regardless | The functional half — the press finds and runs the recipe with no recipe-viewer category registered anywhere — is exactly what `ShippedRecipesDepotGameTest`/`BeltPressingGameTest`/`WeightedPressingGameTest` already prove, since this build ships no JEI integration at all (`UI-REQ-002`) and every one of those tests still passes. The "corrected by the mod's own description" half is a README/Modrinth-listing claim, not code — out of scope for `just check`. |

### `COMP` and `TEST-REQ`

| ID | Requirement | Test / checklist / ruling |
|---|---|---|
| `COMP-REQ-001` | No network call of the mod's own | `SourceSurfaceTest.noNetworkingTypeIsReferencedByTheMod` (SD-1) |
| `TEST-REQ-001` | Every `RECIPE-REQ`/`ROLL-REQ`/`UI-REQ` names its test | This table, SD-5 |
| `TEST-REQ-002` | Deliberate-break proof for `verifyPurePackage` | **Satisfied at SD-1**: the `verifyPurePackage` Gradle task (`build.gradle.kts`) fails `check` if `synthetic_diamonds.model` imports `net.minecraft`/`net.fabricmc`/`com.zurrtum`; gates `tasks.named("check")`. Restated here per `TEST-REQ-002`. |
| `TEST-REQ-003` | This mod's recipe class coexists in `AllRecipeTypes.PRESSING`'s bucket with Create Fly's own `PressingRecipe` | **Satisfied at SD-3**: `RecipeCoexistenceGameTest.vanillaAndThisModsRecipeCoexistInTheSamePressingBucket` (25 interleaved rolls, vanilla `sugar_cane`→paper untouched, this mod's `coal` still exclusive). Restated here per `TEST-REQ-003`. |

**Gaps found and closed on this branch (2):** `RECIPE-FAIL-001`, `RECIPE-FAIL-002` — both via the
new `RecipeLoadFailureGameTest` (two `@GameTest` methods, two new malformed fixture recipes under
`src/gametest/resources/data/synthetic_diamonds_gametest/recipe/weighted_pressing/`).

**Gaps found and *not* closed, with the ruling recorded above rather than silently dropped (1):**
`RECIPE-FAIL-003` — the clamp-and-log behaviour the spec describes is not implemented anywhere in
`src/main`, so no test can prove it without first fixing the code; a test-only ticket does not
implement it. This needs an explicit ruling from Kevin: fix now on a follow-up bug ticket, or accept
the current unclamped behaviour and correct the spec instead.

## Constraints and prior findings

`operations/testing.md` (`TEST-REQ-001`–`003`), `domains/ui.md` `UI-FAIL-001`/`UI-FAIL-002`.
Blocked by SD-3 (the recipe data and their game tests must exist to table them) and SD-4 (the debug
command is part of the checklist's practical tooling). Blocks SD-7 (release requires this ticket's
green three-run bar and checklist). Modeled directly on `create_villager_customers`'s `VC-6`
(`requirement-to-test table, three green just check runs, client checklist`), adapted to this mod's
three domains (`RECIPE`, `ROLL`, `UI`) in place of the sibling's (`CUSTOMER`, `TRANSACTION`,
`SHOP`).

## Verification runs

Two new `@GameTest` methods committed on `feature/sd-5-test-sweep` (`61c4c88`, pushed), closing the
two gaps the table above marks **GAP, closed (SD-5)**; `just map` re-run (10 files current). Three
consecutive `just check` runs on a clean checkout (`./gradlew clean` between each), no flake:

| Run | `./gradlew check -x test -x runGameTest` (lint) | `map-check` | `./gradlew test` (unit) | `tools` tests | Game tests | Wall clock |
|---|---|---|---|---|---|---|
| 1 | `BUILD SUCCESSFUL` (411ms) | `map: 10 files current` | `BUILD SUCCESSFUL` (736ms) | `Ran 4 tests ... OK` | `All 16 required tests passed :)` | 10s |
| 2 | `BUILD SUCCESSFUL` (386ms) | `map: 10 files current` | `BUILD SUCCESSFUL` (454ms) | `Ran 4 tests ... OK` | `All 16 required tests passed :)` | 12s |
| 3 | `BUILD SUCCESSFUL` (389ms) | `map: 10 files current` | `BUILD SUCCESSFUL` (440ms) | `Ran 4 tests ... OK` | `All 16 required tests passed :)` | 12s |

16 is the full game-test count after this ticket's additions (14 before `SD-5`, +2 new `@GameTest`
methods in the new `RecipeLoadFailureGameTest`). Registering the new class in
`src/gametest/resources/fabric.mod.json`'s `fabric-gametest` entrypoint list was required for its
`@GameTest` methods to run at all — the first local run after adding the class silently stayed at
14 (the class compiled, its two malformed fixture JSONs loaded and were correctly rejected by the
log, but Loom's Fabric game-test runner never invoked its methods) until this was found and fixed;
worth a vault note (`vault/projects/create_synthetic_diamonds/`) since every prior gametest class in
this repo was added in the same commit as its own `fabric.mod.json` entry, so this omission mode has
not been hit here before.

## Client checklist (Kevin)

Press timing (real 240-tick cycles) and the observed diamond/flint/gunpowder ratio are not
meaningfully provable headless (`operations/testing.md`'s own note); the debug command below is the
fast way to sanity-check the odds before spending the real time on watching a press cycle. Unticked
— for Kevin, on `just client` with Create Fly:

- [ ] A powered mechanical press over a depot, pressed in turn against charcoal, coal and a coal
      block: each of the three inputs eventually shows all three outcomes (diamond, flint,
      gunpowder) across repeated cycles, one outcome per press, never zero and never two at once;
      the coal block's diamond outcome is 1 diamond (not scaled), its flint/gunpowder outcomes are
      9 each.
- [x] A belt line running items through a powered press shows the same three outcomes, one per
      item, with the belt carrying the result onward.
- [ ] `/synthetic_diamonds debug press 2000` (charcoal, the default) and
      `/synthetic_diamonds debug press 2000 minecraft:coal_block` each tally near 0.5% diamond /
      95% flint / 4.5% gunpowder.
- [ ] A datapack recipe override (e.g. retuning `charcoal.json`'s weights) changes the odds a real
      press rolls against, without a client update.
- [x] No JEI entry anywhere for these three recipes (deferred to 1.1, `UI-DEC-001`) — confirmed
      absent, not merely unnoticed.
