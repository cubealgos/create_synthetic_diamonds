---
schema_version: 1
id: 01M2YH525MZXZQZBB6WAWXVCHG
key: SD-3
type: feat
title: "Recipe data: charcoal, coal, coal block, and game tests through a real press"
created_by: kevin
created_at: 2026-09-20T04:28:23Z
---

## Scope

The three shipped recipe JSON files under
`data/synthetic_diamonds/recipe/weighted_pressing/`: `charcoal.json` (`minecraft:charcoal`,
`RECIPE-REQ-001`), `coal.json` (`minecraft:coal`, `RECIPE-REQ-002`), `coal_block.json` (matched via
the `c:storage_blocks/coal` tag, `RECIPE-REQ-003`) — all three at 0.5% diamond / 95% flint / 4.5%
gunpowder, the block recipe's diamond odds identical to a single item's, only its by-product counts
scaled ×9 (1 diamond / 9 flint / 9 gunpowder, Kevin's ruling in `decisions/DEC-007-inputs-and-block-variants.md`).
No charcoal-block recipe (`RECIPE-REQ-004`: none exists to add one for). Game tests proving a real
`MechanicalPressBlockEntity` finds and runs each recipe in both belt mode and world/depot mode
(`ROLL-REQ-006`), that pressing consumes exactly one input and yields exactly one of the three
outcomes per cycle — never zero, never two — asserted over many rolls with a seeded random for
outcome-exclusivity confidence (`operations/testing.md`), and that this mod's recipe class coexists
in the same `RecipeMap` bucket as Create Fly's own vanilla `PressingRecipe` instances without
disturbing their independent-roll behaviour (`TEST-REQ-003`).

## Approach

Write the three JSON files against SD-2's confirmed serializer id and field shape. Game tests
under `src/gametest`: for each of the three recipes, press the input on a depot and on a belt
through a real mechanical press and run enough cycles with a seeded `RandomSource` to assert the
observed outcome distribution is mutually exclusive every cycle (never a double-outcome, never a
no-outcome) and lands in the right ballpark for the declared weights — mirroring
`RefusalGameTest`/`TransactionGameTest`'s style of seeded-random assertion from
`create_villager_customers`, adapted to this mod's single-roll-per-cycle shape rather than a
per-tick behaviour check. Add one coexistence test pressing a real Create Fly recipe (e.g.
`coal_ore.json`'s crushing recipe, adapted to pressing, or any shipped vanilla pressing recipe)
alongside this mod's own to prove `TEST-REQ-003`.

## Acceptance criteria

- [x] All three recipe JSON files load without error; `charcoal.json` and `coal.json` at identical
      weights and counts; `coal_block.json` at the same weights but 1/9/9 counts.
- [x] `RECIPE-REQ-004` confirmed: no charcoal-block recipe file exists, and this is stated as
      deliberate in a code comment or the recipe folder's own note, not left silently absent.
- [x] A belt-mode game test and a world/depot-mode game test each press all three recipes and
      assert exactly one outcome per cycle, over enough seeded rolls to give confidence in
      exclusivity (`ROLL-REQ-001`, `TEST-REQ-003` style).
- [x] A coexistence game test presses a real Create Fly vanilla recipe and this mod's recipe in the
      same session, confirming neither disturbs the other's roll behaviour.
- [x] `just check` green, including all new game tests.

## Constraints and prior findings

`domains/recipe.md` `RECIPE-REQ-001`–`007`, `RECIPE-FAIL-001`–`004`; `decisions/DEC-007-inputs-and-block-variants.md`;
`operations/testing.md`'s game-test row. Blocked by SD-2 (needs the recipe class, serializer id and
confirmed JSON field names). Blocks SD-5 (the requirement-to-test table cites these game tests) and
SD-8 (a JEI category needs a real recipe to render). The coal-block roll shape (×9 by-products only,
diamond odds unchanged) is a Kevin ruling, not this ticket's to reopen — see
`rulings-2026-09-20.md`.

## Findings

The three shipped files (`charcoal.json`, `coal.json`, `coal_block.json`) decode exactly as
`domains/recipe.md` §3 proposes, with `coal_block.json`'s `ingredient` as the tag form
`"#c:storage_blocks/coal"` — confirmed against `fabric-convention-tags-v2-4.7.1+1f023a9e9e.jar`'s
own `data/c/tags/item/storage_blocks/coal.json`, whose only value is `minecraft:coal_block`.
`RECIPE-REQ-004` (no charcoal-block recipe) is recorded as deliberate in `RecipeRegistration`'s own
Javadoc, not left silently absent, and `RecipeFilesTest.noCharcoalBlockRecipeExists` asserts the
file's continued absence.

**A real recipe-id collision, found and fixed before it could break an already-merged test.**
SD-2's `WeightedPressingGameTest` shipped its own private test recipe
(`data/synthetic_diamonds_gametest/recipe/weighted_pressing/test_diamond_always.json`) with
`ingredient: "minecraft:charcoal"` — the same ingredient this ticket's real `charcoal.json` now
also claims. `javap -p -c` of `RecipeMap.create` (merged Minecraft jar) shows `RecipeMap`'s
`byType` multimap is an `ImmutableMultimap` built by iterating the loaded-recipe `Iterable` once,
in whatever order the data pack reload produced it — not alphabetical, not otherwise specified —
and `RecipeMap.getRecipesFor(...).findFirst()` (SD-2's own finding) returns whichever of two
same-ingredient recipes happens to come first in that unspecified order. Once this ticket's
`charcoal.json` existed, SD-2's fixture and the real recipe would both match every charcoal press,
making SD-2's `aRealPressFindsAndRunsTheWeightedPressingRecipe` test's "always a diamond" assertion
depend on an implementation detail neither ticket controls. Fixed by moving SD-2's fixture off
`minecraft:charcoal` onto `minecraft:blaze_powder` (unused by any Create Fly pressing recipe and by
all three of this mod's own), documented in that test class's own Javadoc; `RECIPE-FAIL-004`
already says this mod does not special-case same-ingredient conflicts, so the fix is to avoid
creating one between this mod's *own* two recipes, not to rely on the resolution order.

**The datapack-override test (`RECIPE-REQ-005`) and the 2,000-roll distribution test
(`ROLL-REQ-001`) cannot both read the loaded `charcoal.json`, so they deliberately don't.**
`runGameTest` runs every `@GameTest` method against one dedicated server and one data-pack reload
for the whole module — a game-test-only override of the shipped `charcoal.json` (diamond weight
1.0, shipped from this same module's `synthetic_diamonds_gametest` companion mod at the identical
resource path) is therefore global to every test in the run, not scoped to one test method.
`WeightedPressingDistributionGameTest` sidesteps this by constructing its
`WeightedPressingRecipe` directly from the same six literals as the real `charcoal.json`, never
going through `RecipeManager`, so its 2,000-roll tally is unaffected by the override; every other
test that presses charcoal (`ShippedRecipesDepotGameTest`, `BeltPressingGameTest`) only asserts
"one of the three outcomes at its recipe's own count", which an always-diamond charcoal still
satisfies, so the override does not make them flaky either. `RecipeOverrideGameTest` confirms the
override is honoured (20/20 rolls yielded a diamond).

**Belt mode is headless-testable the same way SD-2 drove world/depot mode.**
`javap -p -c` of `MechanicalPressBlockEntity.tryProcessOnBelt(TransportedItemStack, List<ItemStack>)`
shows the identical shape SD-2 found for `tryProcessInWorld`: `getRecipe` → `checkcast
CreateRollableRecipe` → `RecipeApplier.applyRecipeOn`, with no belt-physics, kinetic-speed or power
check anywhere before that point. Unlike `tryProcessInWorld` (which mutates the pressed
`ItemEntity`'s stack in place), `tryProcessOnBelt` does not mutate its `TransportedItemStack`
argument — it appends the roll's result stack to the caller-supplied output `List<ItemStack>`
instead — so `BeltPressingGameTest` reads the outcome off that list.
`TransportedItemStack(ItemStack)` has a public single-arg constructor, so a real, placed
`MechanicalPressBlockEntity`'s belt path is driven directly with no belt or kinetic network built.
Also confirmed: `RecipeApplier.applyRecipeOn(RandomSource, int count, ...)` loops `assemble()`
exactly `count` times (merging same-item results, capped at max stack size) — for a count-1 input
stack this is one roll, matching the "exactly one outcome per cycle" guarantee in both modes.

**`just check`'s gametest run: "All 12 required tests passed :)"** — the 11 `@GameTest` methods
this ticket and SD-2 together define, plus one test this module did not add (present in both a
baseline run and this ticket's final run; not investigated further since it is unrelated to this
ticket's scope and passes either way).

**The 2,000-roll charcoal distribution, seed `20260920`:** diamond 8/2000 (0.004, within
`[0.001, 0.012]`), flint 1900/2000 (0.95, within `[0.92, 0.98]`), gunpowder 92/2000 (0.046, within
`[0.02, 0.07]`) — every one of the 2,000 rolls produced exactly one stack, of one of the three
outcome items, at count 1.

**A pre-existing, harmless float-precision note, not a bug:** `0.005f + 0.95f + 0.045f` sums to
`0.9999999897554517` in `WeightedPick.of`'s double-widened arithmetic, not exactly `1.0`, so every
one of this ticket's three recipes (and the hand-built recipe in the distribution test) triggers
`ROLL-REQ-003`'s normalize-and-log-once path on every load. The normalization is proportional and
negligible (the actual weights used are apart from `1.0` by ~`1e-8`); logged here since it is a new,
real (if inconsequential) instance of `ROLL-FAIL-001` the shipped recipes themselves trigger, not
only a datapack author's arithmetic mistake as `roll.md` example anticipated.
