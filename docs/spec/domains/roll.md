---
title: "create_synthetic_diamonds spec — ROLL: the exclusive-roll mechanism"
type: "spec"
category: "create_synthetic_diamonds"
---

# `ROLL` — the exclusive-roll mechanism

## 1. Purpose

How exactly one of three outcomes is produced, never zero and never two at once: the custom
`Recipe`/`RecipeSerializer` pair that carries it, why it needs no mixin, the weighted pick
algorithm, its random source, and what happens when a datapack's weights are malformed. Not which
ingredients or weights any particular recipe declares (`domains/recipe.md`) and not what, if
anything, a player sees about it (`domains/ui.md`).

## 2. Dimensions

| Dimension | Answer |
|---|---|
| **Actors** | The server (`ACTORS-002`) runs the roll on every press cycle that finds a match; Create Fly (`ACTORS-003`) supplies the `RandomSource` parameter and the `AllRecipeTypes.PRESSING` field this mod's recipe class reports itself under; a datapack author (`ACTORS-004`) supplies the weights being rolled against. |
| **Over time** | A recipe match is found → `assemble()` is called once with a `RandomSource` → the weighted pick resolves to exactly one outcome → that outcome's result stack (the item and its count, a per-recipe, per-outcome constant `domains/recipe.md` declares — 1 for every outcome on the single-item recipes, 1 diamond / 9 flint / 9 gunpowder on the coal-block recipe) is returned → the press applies it and consumes the input, once per 240-tick cycle. |
| **Multiplicity** | Exactly one roll per successful recipe match per cycle; exactly one outcome per roll, always — this is the entire point of the custom class (`ROLL-REQ-001`). |
| **Unwanted** | A recipe whose three weights do not sum to 1.0 (`ROLL-FAIL-001`); a negative weight (`domains/recipe.md` `RECIPE-FAIL-003`, clamped before reaching this domain); a datapack that removes one outcome's weight entirely, effectively reducing the roll to two outcomes — allowed, since the three-way shape is a convention this mod follows, not a hard invariant the codec enforces beyond summing to 1.0. |
| **Not-you** | A player who never checks the odds still gets the guarantee for free: every press cycle yields exactly one of the three items, whether or not anyone ever verifies the math. A datapack author who miscounts their weights gets a logged correction, not a broken world. |

## 3. Enumerations

### The weighted pick

Given three weights `w_diamond + w_flint + w_gunpowder = 1.0` (after normalization,
`ROLL-REQ-003`), the roll draws one `float r` from the recipe's `RandomSource` and partitions
`[0, 1)`:

| Range | Outcome |
|---|---|
| `[0, w_diamond)` | diamond |
| `[w_diamond, w_diamond + w_flint)` | flint |
| `[w_diamond + w_flint, 1.0)` | gunpowder |

There is no fourth "nothing" bucket by design: the three outcomes already sum to 100% (research
§C: "here the three outcomes already sum to 100%, so there is no 'nothing' bucket by design").

### Roll outcome vs. Create Fly's own independent rolling (for contrast)

| Mechanism | Per-result independence | Can yield nothing | Can yield two-or-more |
|---|---|---|---|
| Create Fly's `ProcessingOutput.rollOutput` (vanilla pressing, crushing, etc.) | Yes — each result rolled separately | Yes, if every entry misses | Yes, any combination |
| This mod's weighted-exclusive pick | No — one roll picks exactly one bucket | Never | Never |

## 4. Use cases

`UC-001` through `UC-006` in `02-journeys.md`; the normalization behaviour is exercised in `UC-007`.

## 5. Requirements

| ID | Requirement | Priority | From |
|---|---|---|---|
| `ROLL-REQ-001` | The system shall guarantee that every successful pressing-recipe match under this mod's recipe type produces exactly one of {diamond, flint, gunpowder}, never zero and never more than one, regardless of the weights in force. | Must | Kevin, 2026-09-20; research §C |
| `ROLL-REQ-002` | The system shall implement this guarantee via a custom `Recipe<SingleRecipeInput>` class whose `getType()` returns the literal `AllRecipeTypes.PRESSING` static field and whose `assemble(SingleRecipeInput, RandomSource)` performs the weighted pick directly, replacing `CreateSingleStackRollableRecipe`'s shared independent-roll default for recipes of this mod's own type only. Proposed, to confirm at the first ticket (`04-architecture.md` `ARCH-DEC-002`). | Must, proposed mechanism | Research §C |
| `ROLL-REQ-003` | Where a recipe's three declared weights do not sum to 1.0, the system shall normalize them (scale each by `1.0 / sum`) and log a warning once per recipe, rather than fail to load. | Must | `domains/recipe.md` `RECIPE-FAIL-003`; mirrors `create_villager_customers`' `SURFACE-REQ-002` clamp-and-log pattern |
| `ROLL-REQ-004` | The system shall draw its roll from the same `RandomSource` parameter the press already threads through every pressing recipe's `assemble()` call — the identical parameter Create Fly's own `PressingRecipe` would have received for the same cycle. Which concrete `RandomSource` instance the press supplies was not traced further in the research pass, and this requirement does not depend on it being any particular one. | Must | Research §B |
| `ROLL-REQ-005` | The system shall require no `RecipeType` registration of its own and no mixin: the recipe class is found by `MechanicalPressBlockEntity.getRecipe()`'s ordinary `RecipeManager`/`RecipeMap` lookup, keyed on the live `getType()` object. | Must | Research §C; `04-architecture.md` `ARCH-DEC-001` |
| `ROLL-REQ-006` | The system shall apply this mechanism identically whether the press runs in belt mode or world/depot mode, since both call the identical lookup-and-roll path. | Must | Research §B |

## 6. Failure modes

| ID | Failure | Response |
|---|---|---|
| `ROLL-FAIL-001` | A recipe's three weights (after `domains/recipe.md`'s per-field clamp) do not sum to 1.0 | Normalized to sum to 1.0, proportionally; logged once per recipe (`ROLL-REQ-003`). |
| `ROLL-FAIL-002` | A recipe's weights sum to exactly 0 (all three fields zero or missing) | Cannot be normalized (division by zero); the recipe is rejected at load with a clear error, same treatment as `domains/recipe.md` `RECIPE-FAIL-002`. |
| `ROLL-FAIL-003` | `RecipeManager`/`RecipeMap` bucketing does not behave as the research's disassembly shows, at the first ticket | Falls back to a narrowly-scoped mixin on this mod's own lookup (`04-architecture.md` `ARCH-FAIL-002`) — never the shared `ProcessingOutput.rollOutput` method. |

## 7. Open questions

| Question | Blocks | Decided by |
|---|---|---|
| Whether the custom-recipe-class approach (`ROLL-REQ-002`) actually works as the research's disassembly predicts | `ROLL-REQ-001`–`002`, `005` | first ticket; this is the research's recommendation, not yet confirmed against a running server |
| The exact JSON field names and codec shape for the three weights and three counts | `domains/recipe.md` | first ticket |

Horizontal press-scaling balance (research §F) is resolved, not open: Kevin, 2026-09-20, accepted
it as designed — "no cap and no brake, ten presses beating hand-mining is an achievement"
(`decisions/DEC-010-scaling-accepted.md`, `04-architecture.md` `ARCH-FAIL-005`).

## 8. Decisions

- `ROLL-DEC-001` — **A custom `Recipe`/`RecipeSerializer` pair, not a mixin, not pure data**
  (`decisions/DEC-008-exclusive-roll-mechanism.md`, restated here as the mechanism this domain
  implements). The full reasoning, including the two rejected alternatives, lives in
  `04-architecture.md` `ARCH-DEC-002`.
- `ROLL-DEC-002` — **Normalize and log, never fail to load, on a bad weight sum** (this sheet's
  proposal): a datapack author's arithmetic mistake should degrade to "still mutually exclusive,
  slightly different odds than intended," not a missing recipe or a server crash. **Cost if wrong:**
  a stricter "refuse to load" policy is one branch removed, not a redesign.
- `ROLL-DEC-003` — **No shared cap across presses, by design** (`decisions/DEC-010-scaling-accepted.md`):
  each press's roll is entirely independent of every other press; this domain implements no
  cross-press state, cooldown, or diminishing-returns mechanic, and none is planned. Kevin,
  2026-09-20: "no cap and no brake, ten presses beating hand-mining is an achievement."
