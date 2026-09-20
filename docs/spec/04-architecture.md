---
title: "create_synthetic_diamonds spec — architecture: one custom recipe class, zero mixin"
type: "spec"
category: "create_synthetic_diamonds"
---

# 04 — Architecture

Sheet §3. Everything here follows
`vault/technical/minecraft/create-fly-pressing-recipes-26-2.md`; where the research left a gap or a
proposal rather than a settled fact, the gap is named and marked accordingly, never presented as
decided.

This mod is structurally simpler than any of its three siblings: no mixin at all, no new block, no
new item, no new screen, and no persisted state of its own.

## Shape

```
 datapack / JSON                    server                                  Create Fly
 ┌───────────────────────┐  loads  ┌────────────────────────────────┐  looked ┌────────────────────┐
 │ data/synthetic_        │────────►│ WeightedPressingRecipe          │  up by  │ AllRecipeTypes      │
 │  diamonds/recipe/       │ decode  │  implements Recipe<SingleRecipe │◄────────│  .PRESSING           │
 │  weighted_pressing/     │         │  Input>                        │         │  (literal static     │
 │  charcoal.json          │         │  getType() → AllRecipeTypes    │         │   field, unchanged)   │
 │  coal.json               │         │    .PRESSING                  │         │                      │
 │  coal_block.json         │         │  getSerializer() → this mod's │         │ MechanicalPress       │
 └───────────────────────┘         │    own RecipeSerializer         │────────►│  BlockEntity           │
                                    │  assemble(input, RandomSource)  │  belt / │  .getRecipe()          │
                                    │    → weighted-exclusive pick    │  world/  │  (belt mode, world/    │
                                    │    over {diamond, flint,        │  depot   │   depot mode — both    │
                                    │    gunpowder}                    │  mode    │   identical lookup)    │
                                    └────────────────────────────────┘         └────────────────────┘
                                    no mixin config, no new block,
                                    no new item, no new screen,
                                    no persisted state of this mod's own
```

**Nothing here is a new server-side object beyond a recipe class and its serializer.** The only
things this mod registers are a `Recipe<SingleRecipeInput>` implementation, its
`RecipeSerializer`, and the JSON recipe instances that feed them — all through ordinary vanilla
`Registry.register` calls, no Create Fly API and no mixin needed for any of it (research §C).

## `ARCH-DEC-001` — a Fabric mod on Create Fly, one jar, Java 25, zero mixin targets

Same toolchain and layout as the three siblings (`decisions/DEC-004-toolchain.md`): Loom 1.17,
Kotlin DSL with a version catalog, one Gradle project — there is no sim layer to keep pure, and
almost nothing here needs a package-purity check at all, since the only genuinely pure logic is the
weighted-pick algorithm and the weight-normalization step (`operations/testing.md`).

**Unlike every sibling so far, this mod carries no mixin target at all.** The custom recipe class is
found by the vanilla mechanical press through ordinary `Recipe.getType()`/`RecipeType` registry
mechanics — `RecipeManager`/`RecipeMap` bucket recipes by the live `Recipe.getType()` object, and
`MechanicalPressBlockEntity.getRecipe()` looks up that same literal `AllRecipeTypes.PRESSING`
static field — not by a hook into any private vanilla or Create Fly method (research §C). Neither
`create_villager_customers` (two mixin targets) nor `create_brass_compass`/`create_metered_motor`
(zero) needed this particular shape; this is the first of the family to confirm that a Create Fly
processing recipe type can be extended with genuinely zero mixin.

**Cost if wrong:** if the first ticket finds `RecipeManager`/`RecipeMap` behave differently than the
research's disassembly shows (e.g. a stricter type check beyond `getType()` equality), the fallback
is a small mixin scoped to this mod's own recipe lookup — still far narrower than a hook into the
shared `ProcessingOutput.rollOutput` method (`ARCH-DEC-002`, rejected alternative below).

## `ARCH-DEC-002` — the exclusive roll is a custom recipe class, not a mixin on the shared roll method

**Proposed, to confirm at the first ticket** — this is the research's recommendation, not a Kevin
ruling. A custom `Recipe<SingleRecipeInput>`/`RecipeSerializer` pair, registered under this mod's
own id (proposed: `synthetic_diamonds:weighted_pressing`), self-reports `getType() ==
AllRecipeTypes.PRESSING` so the vanilla mechanical press picks it up in both belt and world/depot
mode with no mixin at all. Its own `assemble(SingleRecipeInput, RandomSource)` implements a true
weighted-exclusive pick over the three outcomes, replacing `CreateSingleStackRollableRecipe`'s
shared default `assemble()` (which rolls every `ProcessingOutput` independently) for recipes of
this mod's own type only (`domains/roll.md` `ROLL-DEC-001`).

**Rejected alternative (b): a mixin on `CreateSingleStackRollableRecipe.assemble()` /
`ProcessingOutput.rollOutput`.** That method is shared by *every* Create Fly processing recipe type
— crushing, milling, cutting, mixing, compacting, sandpaper polishing, splashing, haunting — all of
which rely on independent rolls for their own vanilla recipes (`coal_ore.json`'s crushing recipe is
a real example, research §B). A mixin there risks silently changing unrelated Create Fly recipes'
drop behaviour unless very carefully scoped; a dedicated recipe class only ever affects recipes this
mod itself defines.

**Rejected alternative (c): three independent `ProcessingOutput` entries at face-value odds.**
Computed directly from three independent Bernoulli trials: ≈4.75% of presses would yield nothing at
all, and ≈4.71% would yield two-or-more results at once (research §C) — unacceptable against
Kevin's "exactly one of three, never zero, never two" rule.

**Cost if wrong:** if `RecipeManager`'s live-`getType()` bucketing does not behave exactly as the
research's disassembly shows, the fallback is the rejected mixin path, scoped tightly to this mod's
own lookup rather than the shared roll method — still avoids touching unrelated Create Fly recipes.

## `ARCH-DEC-003` — no new block, item, screen, container, or persisted state

The mechanical press, the diamond/flint/gunpowder items, and vanilla's/Create Fly's own
recipe-application flow do all the work. This mod's server-side surface is exactly: one recipe
class, one serializer, and the JSON files that parametrize them. Nothing is written to the world
save beyond what the recipe consumption/production already writes to inventories — there is no
memory module, no data component, no config file beyond the recipe JSONs themselves
(`contracts/data-contract.md`).

## `ARCH-DEC-004` — no JEI category at 1.0.0; a self-contained one is a 1.1 ticket

Create Fly's own JEI category for pressing (`PressingCategory`) populates itself from
`recipeMap.byType(AllRecipeTypes.PRESSING)` — so this mod's recipes *would* be included in that
collection — but its `setRecipe(...)` method then hard-casts every recipe to the concrete
`PressingRecipe` record before reading `.ingredient()`/`.results()`, which throws
`ClassCastException` the instant it tries to render a recipe of this mod's differently-typed class
(research §C). JEI support therefore requires this mod's own JEI category (a small
`jei_mod_plugin` Fabric entrypoint) or no JEI integration at all; there is no EMI integration
anywhere in Create Fly to use instead (research §C confirms no `dev/emi` class exists in the jar).

**Decided by Kevin, 2026-09-20: "no JEI category at 1.0.0, a 1.1 ticket."** The press finds and
runs the recipe regardless of whether it is displayed anywhere, and the odds are readable from the
mod's own README/description or the recipe JSON files directly at 1.0 (`domains/ui.md`
`UI-DEC-001`, `decisions/DEC-009-jei-deferred.md`). The own-JEI-category path stays available and
cheap to add on top of the recipe class without touching the recipe or roll domains, and is now a
scheduled 1.1 scope item rather than an open "revisit later."

## Runtime topology (sheet §3.1)

The mod runs inside the Minecraft client and server processes; no process, daemon, or file of its
own. All logic — recipe lookup, the roll, and the result application — is server-side, exactly as
for every Create Fly processing recipe. The recipe JSON itself is also loaded client-side, as
vanilla recipes always are, purely for data-driven client bookkeeping (`contracts/platform-matrix.md`);
no client-side behaviour of this mod's own exists.

## Failure modes with no single owner (sheet §3.6)

| ID | Failure | Response |
|---|---|---|
| `ARCH-FAIL-001` | Create Fly missing or an incompatible version | Fabric Loader refuses to start with its dependency message; the mod adds nothing. |
| `ARCH-FAIL-002` | `RecipeManager`/`RecipeMap` bucketing does not behave as the research's disassembly shows, at the first ticket | Falls back to a narrowly-scoped mixin on this mod's own lookup only (`ARCH-DEC-001`, `ARCH-DEC-002`) — not on the shared roll method. |
| `ARCH-FAIL-003` | A datapack's recipe JSON has malformed or out-of-range weights | Normalized and logged once, or refused at load with a clear error for a structurally invalid file (`domains/recipe.md`, `domains/roll.md` `ROLL-FAIL-001`). |
| `ARCH-FAIL-004` | Another mod also registers a recipe class self-reporting `AllRecipeTypes.PRESSING` for the same ingredient | Both are candidates in the same `RecipeMap` bucket; ordinary vanilla recipe-conflict resolution (whichever the game finds first, or a datapack overriding one) applies — no special handling by this mod, since Create Fly's own recipes already coexist this way. |
| `ARCH-FAIL-005` | Horizontal press scaling (many presses off one line) outruns the intended "harder to automate than mining" pacing | **Accepted by Kevin, 2026-09-20** — not a failure this mod defends against: "scaling accepted: no cap and no brake, ten presses beating hand-mining is an achievement." Each press rolls independently with no shared cap (research §F), by design (`decisions/DEC-010-scaling-accepted.md`). |
