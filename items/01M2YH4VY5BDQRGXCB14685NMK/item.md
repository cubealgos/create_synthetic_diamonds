---
schema_version: 1
id: 01M2YH4VY5BDQRGXCB14685NMK
key: SD-2
type: feat
title: Custom pressing recipe type and serializer, exclusive weighted roll
created_by: kevin
created_at: 2026-09-20T04:28:17Z
---

## Scope

A custom `Recipe<SingleRecipeInput>`/`RecipeSerializer` pair, `synthetic_diamonds.recipe`, self-
reporting `getType() == AllRecipeTypes.PRESSING` so the vanilla mechanical press finds and runs it
in both belt and world/depot mode with **zero mixin** (`ROLL-REQ-002`, `ROLL-REQ-005`,
`ROLL-REQ-006`, `04-architecture.md` `ARCH-DEC-002`). Its `assemble(SingleRecipeInput,
RandomSource)` performs a true weighted-exclusive pick over {diamond, flint, gunpowder} —
`ROLL-REQ-001` — replacing `CreateSingleStackRollableRecipe`'s shared independent-roll `assemble()`
for recipes of this type only. The pure weighted-pick algorithm and the weight-normalization/clamp
logic (`ROLL-REQ-003`, `ROLL-FAIL-001`, `ROLL-FAIL-002`) live in `synthetic_diamonds.model`, gated
by `verifyPurePackage`, unit-tested with a fixed `RandomSource` against every bucket boundary
including the edges at exactly 0 and just under 1.0 (`operations/testing.md`). Confirms
`ARCH-DEC-002`'s proposed mechanism against a real server (this ticket is where "proposed, to
confirm" in the spec becomes settled fact or the documented mixin fallback). Registers the
serializer under `synthetic_diamonds:weighted_pressing` (`contracts/public-surface.md`
`SURFACE-REQ-001`); no recipe JSON data files yet — those are SD-3's scope, along with the game
test proving a real press finds this class.

## Approach

Read the research note this sheet cites (`vault/technical/minecraft/create-fly-pressing-recipes-26-2.md`)
before writing any Java, since `ROLL-REQ-002`/`ARCH-DEC-002` are explicitly unconfirmed until this
ticket: verify via `javap` against the Create Fly jar and the merged Minecraft jar in the Gradle
cache that `RecipeManager`/`RecipeMap` really do bucket by the live `getType()` object rather than
the JSON `"type"` field, and that `MechanicalPressBlockEntity.getRecipe()` looks up
`AllRecipeTypes.PRESSING` the same way. If confirmed, write the recipe class and serializer with a
minimal in-code test recipe (not yet a shipped JSON) to prove the roll fires; if not confirmed,
stop and record the finding in this ticket before falling back to `ARCH-FAIL-002`'s narrowly-scoped
mixin — never the shared `ProcessingOutput.rollOutput` method (`ARCH-DEC-002`'s rejected
alternative). Weighted pick: draw one `float` from the `RandomSource`, partition `[0, 1)` into
`[0, w_diamond)` / `[w_diamond, w_diamond+w_flint)` / `[w_diamond+w_flint, 1.0)` exactly as
`domains/roll.md` §3 specifies — no fourth "nothing" bucket. Normalize-and-log when weights don't
sum to 1.0; reject at load when they sum to exactly 0 (`ROLL-FAIL-001`, `ROLL-FAIL-002`).

## Acceptance criteria

- [ ] `RecipeManager`/`RecipeMap` bucketing confirmed against the real jars (or the mixin fallback
      taken and recorded here) before the recipe class is written.
- [ ] `synthetic_diamonds.recipe`'s custom `Recipe`/`RecipeSerializer` self-reports
      `AllRecipeTypes.PRESSING` and is found by a real `MechanicalPressBlockEntity` lookup.
- [ ] The weighted pick in `synthetic_diamonds.model` is unit-tested with a fixed `RandomSource`
      against every bucket boundary (`ROLL-REQ-001`), including exactly-0 and just-under-1.0 edges.
- [ ] Weight normalization (`ROLL-REQ-003`) and the sum-to-zero rejection (`ROLL-FAIL-002`) are
      unit-tested.
- [ ] `verifyPurePackage` passes against `synthetic_diamonds.model` with the roll logic inside it.
- [ ] `just check` green.

## Constraints and prior findings

`domains/roll.md` `ROLL-REQ-001`–`006`, `ROLL-FAIL-001`–`003`, `04-architecture.md` `ARCH-DEC-001`,
`ARCH-DEC-002`, `ARCH-FAIL-002`. Blocked by SD-1 (needs the bootstrapped scaffold, the empty
`synthetic_diamonds.model` package and its `verifyPurePackage` gate). Blocks SD-3 (recipe data
files need the serializer id and JSON shape this ticket settles) and SD-4 (the debug command calls
the same pure roll). The JSON field names in `domains/recipe.md` §3 ("proposed") are confirmed or
adjusted here, and `domains/recipe.md`'s open question ("the exact field names") is resolved by
this ticket, to be recorded back if it diverges from the sheet's proposal.
