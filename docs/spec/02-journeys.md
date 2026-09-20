---
title: "create_synthetic_diamonds spec — journeys: the use cases end to end"
type: "spec"
category: "create_synthetic_diamonds"
---

# 02 — Journeys

Every step names who acts. `UC` ids are flat across the project; domain files reference them. Every
press cycle in these journeys follows one rule: exactly one of diamond, flint, or gunpowder is
produced, never zero and never two at once (`domains/roll.md` `ROLL-REQ-001`).

### `UC-001` — Pressing charcoal into a diamond (the success case)

Actor: player (`ACTORS-001`) · Goal: get a diamond out of a charcoal press

| Step | Actor | Action |
|---|---|---|
| 1 | player | Feeds a piece of charcoal into a mechanical press, on a belt or resting in world/depot mode. |
| 2 | server | At the end of a 240-tick cycle, finds this mod's charcoal recipe via `AllRecipeTypes.PRESSING` and consumes exactly one charcoal (`domains/recipe.md` `RECIPE-REQ-001`). |
| 3 | server | Rolls the exclusive three-way pick; the 0.5% diamond branch wins (`domains/roll.md` `ROLL-REQ-002`). |
| 4 | server | Outputs one `minecraft:diamond`; no flint, no gunpowder. |
| 5 | player | Collects the diamond from the press's output. |

### `UC-002` — Pressing charcoal into flint (the majority case)

Actor: player · Goal: understand why most presses just consume charcoal

| Step | Actor | Action |
|---|---|---|
| 1 | server | Same recipe lookup and consumption as `UC-001`. |
| 2 | server | Rolls the exclusive pick; the 95% flint branch wins. |
| 3 | server | Outputs one `minecraft:flint`; no diamond, no gunpowder. |
| 4 | player | Sees flint accumulate steadily; this is the expected common outcome. |

### `UC-003` — Pressing charcoal into gunpowder (the minority failure case)

Actor: player · Goal: understand the second, rarer by-product

| Step | Actor | Action |
|---|---|---|
| 1 | server | Same recipe lookup and consumption as `UC-001`. |
| 2 | server | Rolls the exclusive pick; the 4.5% gunpowder branch wins. |
| 3 | server | Outputs one `minecraft:gunpowder`; no diamond, no flint. |
| 4 | player | Sees gunpowder accumulate more slowly than flint, roughly at a 95:4.5 ratio to it over time. |

### `UC-004` — Pressing coal (identical mechanism, different input)

Actor: player · Goal: run the same mechanic on coal instead of charcoal

| Step | Actor | Action |
|---|---|---|
| 1 | player | Feeds `minecraft:coal` into a press instead of charcoal. |
| 2 | server | Finds this mod's separate coal recipe (`domains/recipe.md` `RECIPE-REQ-002`), identical odds to the charcoal recipe. |
| 3 | server | Rolls and outputs exactly as `UC-001`–`003`, at the same 0.5%/95%/4.5% split. |
| 4 | player | Gets the same diamond/flint/gunpowder odds whether they feed charcoal or coal. |

### `UC-005` — Pressing a coal block

Actor: player · Goal: run the mechanic at block scale

| Step | Actor | Action |
|---|---|---|
| 1 | player | Feeds `minecraft:coal_block` into a press. |
| 2 | server | Finds this mod's coal-block recipe, matched via the `c:storage_blocks/coal` tag (`domains/recipe.md` `RECIPE-REQ-003`). |
| 3 | server | Rolls the block recipe's weights — the **same** 0.5% diamond / 95% flint / 4.5% gunpowder odds as a single-item press (Kevin, 2026-09-20). |
| 4 | server | Outputs exactly one of: 1 diamond, 9 flint, or 9 gunpowder; the coal block itself is consumed whole, not unpacked into nine coal (`decisions/DEC-007-inputs-and-block-variants.md`). |
| 5 | player | Gets diamonds at the *same* rate per press as a single-item press — no better odds from pressing a block — but a full block's worth of by-product back on the common outcome, since "blocks are compression, not a shortcut" (Kevin, 2026-09-20). |

There is no equivalent charcoal-block journey: no charcoal block exists in vanilla or Create Fly to
press (research §D; `decisions/DEC-007-inputs-and-block-variants.md`).

### `UC-006` — Belt-fed continuous pressing

Actor: player · Goal: automate the mechanic instead of hand-feeding

| Step | Actor | Action |
|---|---|---|
| 1 | player | Builds a belt carrying charcoal or coal under (or through) a mechanical press in belt mode. |
| 2 | server | Every 240 ticks, consumes exactly one item from the belt and rolls once, regardless of how much charcoal is queued on the belt (`bulkPressing` defaults to `false`, research §B). |
| 3 | server | Repeats indefinitely as long as the belt keeps supplying input. |
| 4 | player | Sees a slow, steady trickle of diamonds mixed into a much larger stream of flint and a smaller stream of gunpowder, at roughly 300 press applications/hour from one press. |

Scaling this journey to several presses off one belt is accepted, not a balance problem to solve:
Kevin, 2026-09-20, "no cap and no brake, ten presses beating hand-mining is an achievement"
(research §F; `04-architecture.md` `ARCH-FAIL-005`; `domains/roll.md`) — not itself a new journey,
since each press runs `UC-006` independently with no shared state between presses.

### `UC-007` — A datapack author retunes the weights

Actor: datapack or resource pack author (`ACTORS-004`) · Goal: change the odds without touching Java

| Step | Actor | Action |
|---|---|---|
| 1 | author | Writes a datapack overriding `data/synthetic_diamonds/recipe/weighted_pressing/charcoal.json` (or `coal.json`, `coal_block.json`) with different weights. |
| 2 | server | Loads the override through this mod's custom codec exactly as it loads the shipped defaults; no Java is touched (`domains/roll.md` `ROLL-REQ-003`). |
| 3 | server | If the three weights in the override do not sum to 1.0, normalizes them and logs once (`domains/roll.md` `ROLL-FAIL-001`). |
| 4 | player | Presses now roll against the overridden odds, with the same mutual-exclusivity guarantee as the shipped defaults. |

### `UC-008` — A player checks a recipe viewer and finds nothing there

Actor: player (`ACTORS-006`) · Goal: look up the odds through JEI, as they would for any other Create Fly recipe

| Step | Actor | Action |
|---|---|---|
| 1 | player | Opens JEI's "Pressing" category, expecting to see the charcoal/coal/coal-block recipes listed. |
| 2 | server / client | Nothing appears: Kevin ruled no JEI category at 1.0.0, deferred to a 1.1 ticket (`domains/ui.md` `UI-DEC-001`), and Create Fly's own `PressingCategory` cannot render a recipe of this mod's custom class without crashing, so it is deliberately excluded rather than shown broken (research §C, §E). |
| 3 | player | Learns the odds instead from the mod's own README/description, or by reading the recipe JSON files directly. |
| 4 | player | The press itself finds and runs the recipe regardless — this only affects what a recipe viewer displays, not whether the mechanic works. |
