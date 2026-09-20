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

## Findings

`javap -p -c` against the Create Fly jar (`create-fly-26.2-rc-2-6.0.9-1.jar`) and the merged
Minecraft jar (`minecraft-merged-deobf-26.2.jar`) in the Gradle cache, done before any Java was
written, confirms `ROLL-REQ-002`/`ROLL-REQ-005`/`ARCH-DEC-001`/`ARCH-DEC-002` hold as proposed, with
one refinement recorded below. No mixin fallback (`ARCH-FAIL-002`/`ROLL-FAIL-003`) is needed.

- **`RecipeMap.create(Iterable<RecipeHolder<?>>)`** builds its `Multimap<RecipeType<?>,
  RecipeHolder<?>>` by calling `RecipeHolder.value().getType()` on each loaded recipe
  (`invokeinterface Recipe.getType`) and using that live object as the multimap key — never reading
  the JSON `"type"`/serializer field. `RecipeMap.byType(RecipeType<T>)` is a plain
  `Multimap.get(Object)` call: reference/`equals` identity of the `RecipeType` object is what
  matters, confirming a custom class whose `getType()` returns the literal `AllRecipeTypes.PRESSING`
  static field lands in exactly the same bucket as every vanilla `PressingRecipe`.
- **`MechanicalPressBlockEntity.getRecipe(SingleRecipeInput)`** disassembles to exactly
  `((ServerLevel) level).recipeAccess().getRecipeFor(AllRecipeTypes.PRESSING, input, level)` — the
  same literal static field, no string/id comparison — and `RecipeManager.getRecipeFor` resolves to
  `RecipeMap.getRecipesFor(type, input, level).findFirst()`, i.e. `byType(type).stream().filter(v ->
  v.value().matches(input, level))`. Confirmed identical in both `tryProcessOnBelt` and
  `tryProcessInWorld` (belt and world/depot mode call the same `getRecipe()`), matching `ROLL-REQ-006`.
- **Refinement to `ARCH-DEC-002`'s proposed shape**: `tryProcessOnBelt`/`tryProcessInWorld` do not
  stop at `Recipe`/`RecipeHolder` — after unwrapping the `Optional<RecipeHolder<PressingRecipe>>`
  (a compile-time-only generic signature; erased at runtime, so it performs no runtime check), both
  methods `checkcast` the recipe's value to
  `com.zurrtum.create.foundation.recipe.CreateRollableRecipe` before calling the static
  `RecipeApplier.applyRecipeOn(RandomSource, int, RecipeInput, CreateRollableRecipe)`. A class
  implementing bare vanilla `Recipe<SingleRecipeInput>` only (not `CreateRollableRecipe`) would be
  found by `getRecipe()` but then throw `ClassCastException` the instant the press tried to apply it.
  `WeightedPressingRecipe` therefore implements `CreateRollableRecipe<SingleRecipeInput>` (Create
  Fly's own interface, declaring `abstract List<ItemStack> assemble(T, RandomSource)` as a `default`
  method overridable in an implementing class), not `CreateSingleStackRollableRecipe` (which would
  also pull in an unused `ingredient()`/`results()`-based default `assemble` this mod replaces
  anyway) and not plain `Recipe`.
- `MechanicalPressBlockEntity.tryProcessInWorld(ItemEntity, boolean)`'s recipe lookup, roll and
  application (`getRecipe` → `checkcast CreateRollableRecipe` → `RecipeApplier.applyRecipeOn`) have
  no dependency on `getKineticSpeed()`/`canProcessInBulk()`/power state anywhere before that point —
  confirmed by full disassembly of the method. This is why the game test below calls it directly
  rather than building and powering a real kinetic network.
- `BuiltInRegistries.RECIPE_SERIALIZER` (`Registry<RecipeSerializer<?>>`) is the ordinary vanilla
  registry `RecipeRegistration.register()` registers into via `Registry.register`; no Create Fly API
  used, confirming `ROLL-REQ-005`'s "no mixin, no `RecipeType` of its own" claim in full.
- Minor toolchain note, not a design finding: on this 26.2 jar, `net.minecraft.resources.ResourceLocation`
  does not exist — the class is `net.minecraft.resources.Identifier` (`fromNamespaceAndPath(String,
  String)`), used accordingly in `RecipeRegistration`.
