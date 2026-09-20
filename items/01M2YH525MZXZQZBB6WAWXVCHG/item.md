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

- [ ] All three recipe JSON files load without error; `charcoal.json` and `coal.json` at identical
      weights and counts; `coal_block.json` at the same weights but 1/9/9 counts.
- [ ] `RECIPE-REQ-004` confirmed: no charcoal-block recipe file exists, and this is stated as
      deliberate in a code comment or the recipe folder's own note, not left silently absent.
- [ ] A belt-mode game test and a world/depot-mode game test each press all three recipes and
      assert exactly one outcome per cycle, over enough seeded rolls to give confidence in
      exclusivity (`ROLL-REQ-001`, `TEST-REQ-003` style).
- [ ] A coexistence game test presses a real Create Fly vanilla recipe and this mod's recipe in the
      same session, confirming neither disturbs the other's roll behaviour.
- [ ] `just check` green, including all new game tests.

## Constraints and prior findings

`domains/recipe.md` `RECIPE-REQ-001`–`007`, `RECIPE-FAIL-001`–`004`; `decisions/DEC-007-inputs-and-block-variants.md`;
`operations/testing.md`'s game-test row. Blocked by SD-2 (needs the recipe class, serializer id and
confirmed JSON field names). Blocks SD-5 (the requirement-to-test table cites these game tests) and
SD-8 (a JEI category needs a real recipe to render). The coal-block roll shape (×9 by-products only,
diamond odds unchanged) is a Kevin ruling, not this ticket's to reopen — see
`rulings-2026-09-20.md`.
