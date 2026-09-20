---
title: "create_synthetic_diamonds DEC-008 — A custom Recipe/RecipeSerializer pair under AllRecipeTypes.PRESSING, zero mixin, proposed to confirm"
type: "spec"
category: "create_synthetic_diamonds"
---

# `DEC-008` — A custom Recipe/RecipeSerializer pair under AllRecipeTypes.PRESSING, zero mixin, proposed to confirm

**Status:** proposed by the research pass, to confirm at the first ticket. Not a Kevin ruling — the
mutual-exclusivity requirement itself is Kevin's (`decisions/DEC-005-chance-and-outcomes.md`); this
decision is about how to implement it.

The mechanism: a custom `Recipe<SingleRecipeInput>` class, registered under this mod's own
`RecipeSerializer` id (proposed: `synthetic_diamonds:weighted_pressing`), whose `getType()` returns
the literal `AllRecipeTypes.PRESSING` static field. Because `RecipeManager`/`RecipeMap` bucket
recipes by that live `getType()` Java object rather than by the JSON `"type"`/serializer field, and
`MechanicalPressBlockEntity.getRecipe()` looks up recipes by that same object, this class is placed
in exactly the same recipe bucket as every vanilla `PressingRecipe` and is found and run by the
mechanical press — in both belt mode and world/depot mode — with **zero mixin** (research §C). Its
own `assemble(SingleRecipeInput, RandomSource)` implements the weighted-exclusive pick
(`domains/roll.md`), replacing the shared default `assemble()` every other Create Fly processing
recipe type still uses, but only for recipes of this mod's own type.

## Rejected alternative: a mixin on the shared roll method

`CreateSingleStackRollableRecipe.assemble()`/`ProcessingOutput.rollOutput` is the method every
single-input Create Fly processing recipe shares — pressing, crushing, milling, cutting, mixing,
compacting, sandpaper polishing, splashing, haunting. A mixin there would need very careful scoping
(e.g. matching on the specific recipe instance) to avoid silently changing how every *other*
Create Fly recipe of every other processing type rolls its own, unrelated chance outputs — a real
recipe like `coal_ore.json`'s crushing recipe (two independent `chance:0.75` results plus a
guaranteed one) depends on that method's existing independent-roll behaviour (research §B, §C).
Rejected as needlessly wide a target for a change that only needs to affect this mod's own three
recipes.

## Rejected alternative: pure independent chance outputs

Three `ProcessingOutput` entries at face-value odds (0.5%/95%/4.5%) computed out to ≈4.75% of
presses yielding nothing at all and ≈4.71% yielding two-or-more results at once (research §C,
computed directly from the three independent Bernoulli probabilities) — nowhere near Kevin's
"exactly one of three, always" rule. Rejected outright, not merely deprioritized.

## Cost if wrong

If the first ticket finds `RecipeManager`/`RecipeMap`'s live-`getType()` bucketing does not behave
exactly as the research's `javap` disassembly predicts, the fallback is a mixin scoped narrowly to
this mod's own recipe lookup or registration path — not the shared roll method rejected above.
Either way, the three-way exclusive weighted pick in `domains/roll.md` stays the same; only how the
press is made to find it would change. See `04-architecture.md` `ARCH-DEC-001`, `ARCH-DEC-002`,
`ARCH-FAIL-002`.
