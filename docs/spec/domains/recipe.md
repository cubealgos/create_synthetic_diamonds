---
title: "create_synthetic_diamonds spec — RECIPE: the three pressing recipes"
type: "spec"
category: "create_synthetic_diamonds"
---

# `RECIPE` — the three pressing recipes

## 1. Purpose

The three recipe instances themselves: what they accept, what their JSON looks like, and how a
datapack author retunes them. Not how the exclusive roll they carry actually works
(`domains/roll.md`) and not what, if anything, a player sees about them (`domains/ui.md`).

## 2. Dimensions

| Dimension | Answer |
|---|---|
| **Actors** | The server (`ACTORS-002`) loads and matches these recipes; Create Fly (`ACTORS-003`) supplies the press that runs them; a datapack or resource pack author (`ACTORS-004`) may override their ingredients or weights. |
| **Over time** | Loaded once at datapack (re)load, exactly as any vanilla or Create Fly recipe; matched fresh against every press cycle's input; never mutated at runtime. |
| **Multiplicity** | Exactly three recipe JSON files at 1.0: charcoal, coal, coal block. A datapack may add more (e.g. a modded coal-equivalent item) without touching this mod's own files, or override any of the three outright. |
| **Unwanted** | A malformed JSON (bad item id, negative count); a recipe whose three weights do not sum to 1.0 (handled in `domains/roll.md`, not here); two recipes both claiming the same input item (ordinary vanilla recipe-conflict resolution applies, `04-architecture.md` `ARCH-FAIL-004`). |
| **Not-you** | A player who never presses charcoal or coal sees nothing different about their world at all. A modpack author who removes Create Fly loses these recipes entirely, since they only exist within `AllRecipeTypes.PRESSING`. |

## 3. Enumerations

### The three recipes

| Recipe | Ingredient | Weights (diamond / flint / gunpowder) | Result counts (diamond / flint / gunpowder) | Max diamonds per press cycle |
|---|---|---|---|---|
| `charcoal` | `minecraft:charcoal` | 0.5% / 95% / 4.5% (Kevin, 2026-09-20) | 1 / 1 / 1 | 1 |
| `coal` | `minecraft:coal` | 0.5% / 95% / 4.5%, identical to `charcoal` (Kevin, 2026-09-20) | 1 / 1 / 1 | 1 |
| `coal_block` | `#c:storage_blocks/coal` (matches `minecraft:coal_block`) | 0.5% / 95% / 4.5%, **identical odds to a single item** (Kevin, 2026-09-20) | 1 / 9 / 9 — only the by-product counts scale (Kevin, 2026-09-20) | 1 |

Two separate single-item recipes (`charcoal`, `coal`) rather than one recipe keyed by the vanilla
`minecraft:coals` tag — both are viable (research §A confirms `minecraft:coals` = `{coal,
charcoal}` exactly), but two nearly-identical recipes is simplest and mirrors Create Fly's own
single-ingredient pressing recipe convention (research §A recommendation).

**The coal-block recipe's shape is Kevin's ruling of 2026-09-20** (`decisions/DEC-007-inputs-and-block-variants.md`),
not either option the sheet's first draft proposed: the diamond odds stay at exactly the same 0.5%
as pressing a single charcoal or coal — one press, one roll, no better a diamond rate per block than
per item — and only the *failure* outcomes scale with the block's own size, returning 9 flint or 9
gunpowder instead of 1. Kevin's words: "one roll per block press at the same 0.5%, the failure
by-product scaled ×9 (9 flint or 9 gunpowder), so blocks are compression, not a shortcut." A block
therefore presses at a *worse* diamond-per-charcoal rate than nine single-item presses (same 0.5%
chance consumes nine items' worth of material in one roll instead of nine independent rolls), and
exists for cycle-time convenience and lossless by-product batching, not for better diamond odds.

### JSON shape (proposed)

```json
{
  "type": "synthetic_diamonds:weighted_pressing",
  "ingredient": "minecraft:charcoal",
  "diamond_chance": 0.005,
  "diamond_count": 1,
  "flint_chance": 0.95,
  "flint_count": 1,
  "gunpowder_chance": 0.045,
  "gunpowder_count": 1
}
```

The `coal_block` recipe overrides only the two `_count` fields (`flint_count: 9`,
`gunpowder_count: 9`); its three `_chance` fields are unchanged from the single-item recipes.
`ingredient` is a plain vanilla `Ingredient` (a single item id here; the coal-block recipe uses a
`#tag` form instead) decoded the same way any vanilla recipe ingredient is. `"type"` selects this
mod's own `RecipeSerializer`/codec — a JSON detail, not what buckets the recipe for the press to
find (`04-architecture.md` `ARCH-DEC-002`, research §C: bucketing is by the live `getType()` Java
object). The six chance/count fields are this sheet's proposed field names, confirmed at the first
ticket; the three chance values are read by `domains/roll.md`'s weighted pick, and the count of
whichever outcome wins is read straight off the matching `_count` field.

## 4. Use cases

`UC-001` through `UC-005`, `UC-007` in `02-journeys.md`.

## 5. Requirements

| ID | Requirement | Priority | From |
|---|---|---|---|
| `RECIPE-REQ-001` | The system shall provide a pressing recipe accepting `minecraft:charcoal` as its sole ingredient, with weights 0.5% diamond / 95% flint / 4.5% gunpowder. | Must | Kevin, 2026-09-20; `UC-001`–`003` |
| `RECIPE-REQ-002` | The system shall provide a pressing recipe accepting `minecraft:coal` as its sole ingredient, with weights identical to `RECIPE-REQ-001`. | Must | Kevin, 2026-09-20; `UC-004` |
| `RECIPE-REQ-003` | The system shall provide a pressing recipe accepting `minecraft:coal_block` (matched via the `c:storage_blocks/coal` tag) as its sole ingredient, with the same 0.5% / 95% / 4.5% weights as `RECIPE-REQ-001`, and result counts 1 diamond / 9 flint / 9 gunpowder. | Must | Kevin, 2026-09-20; `decisions/DEC-007-inputs-and-block-variants.md`; `UC-005` |
| `RECIPE-REQ-004` | The system shall add no recipe for a charcoal block: none exists in vanilla or Create Fly to add one for (research §D). | Must | Research §D |
| `RECIPE-REQ-005` | Where a datapack replaces one of these three recipe files, the system shall use the datapack's ingredient, weights and counts, decoded through the same custom codec as the shipped defaults. | Must | `UC-007` |
| `RECIPE-REQ-006` | The system shall produce, on a successful diamond outcome, exactly one `minecraft:diamond` and nothing else — never more than one diamond per press cycle, regardless of the input, and no item of this mod's own, ever. | Must | `decisions/DEC-006-vanilla-diamond.md`; `decisions/DEC-007-inputs-and-block-variants.md` |
| `RECIPE-REQ-007` | Each recipe shall be a single-item input, single-outcome-type output per cycle: no recipe in this mod accepts more than one distinct ingredient or produces more than one of the three outcome types per cycle; an outcome's item *count* is a per-recipe, per-outcome constant (`RECIPE-REQ-003`'s ×9 by-product counts) that does not itself vary the exclusivity guarantee (`domains/roll.md` `ROLL-REQ-001`). | Must | Enumerations |

## 6. Failure modes

| ID | Failure | Response |
|---|---|---|
| `RECIPE-FAIL-001` | A recipe JSON's ingredient references an item id that does not exist (e.g. a typo, or a modded item from an uninstalled mod) | Fails to load like any vanilla recipe referencing a missing item; the recipe is simply absent, no crash. |
| `RECIPE-FAIL-002` | A recipe JSON is missing a required chance field | Rejected at load with a clear decode error naming the missing field, same as a malformed vanilla recipe. |
| `RECIPE-FAIL-003` | A chance field is negative or greater than 1.0 | Clamped to the valid `[0, 1]` range and logged once, then handed to `domains/roll.md`'s normalization step, rather than failing to load. |
| `RECIPE-FAIL-004` | Two loaded recipes (this mod's and, say, a third-party add-on's) both claim the same ingredient under `AllRecipeTypes.PRESSING` | Ordinary vanilla recipe resolution applies (`04-architecture.md` `ARCH-FAIL-004`); not a crash, not special-cased by this mod. |

## 7. Open questions

| Question | Blocks | Decided by |
|---|---|---|
| The exact field names in the JSON shape above (`diamond_chance`, `flint_count` etc.) | `RECIPE-REQ-001`–`003` | first ticket |

The coal-block recipe's weights and counts were the one substantive open question in this domain;
resolved by Kevin, 2026-09-20 (`decisions/DEC-007-inputs-and-block-variants.md`,
`rulings-2026-09-20.md`).

## 8. Decisions

- `RECIPE-DEC-001` — **Two nearly-identical recipes, one per item, not one recipe over the
  `minecraft:coals` tag** (this sheet's reading of research §A's recommendation). Both are cheap and
  equally correct; the two-recipe form mirrors Create Fly's own convention of one ingredient per
  pressing recipe JSON, seen across all 11 of its shipped examples. **Cost if wrong:** collapsing to
  one tag-based recipe is a small JSON change, not a design change.
- `RECIPE-DEC-002` — **The success product is always the plain vanilla diamond, never scaled or
  varied by input** (`decisions/DEC-006-vanilla-diamond.md`): pressing a coal block does not yield
  more than one diamond per cycle, and its diamond *odds* are not scaled up either — only its
  by-product counts are (Kevin, 2026-09-20; `RECIPE-REQ-003`,
  `decisions/DEC-007-inputs-and-block-variants.md`).
