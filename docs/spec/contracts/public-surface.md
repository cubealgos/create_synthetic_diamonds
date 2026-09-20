---
title: "create_synthetic_diamonds spec — public surface: what a datapack, resource pack or add-on may rely on"
type: "spec"
category: "create_synthetic_diamonds"
---

# Public surface (`SURFACE`)

| Surface | Stable from | What it is |
|---|---|---|
| Mod id `synthetic_diamonds` | 1.0 | Fabric mod id |
| Recipe serializer/type id `synthetic_diamonds:weighted_pressing` (proposal, confirmed at the first ticket) | 1.0 | The custom recipe class's own registered id (`04-architecture.md` `ARCH-DEC-002`) |
| Recipe JSON files `data/synthetic_diamonds/recipe/weighted_pressing/charcoal.json`, `coal.json`, `coal_block.json` | 1.0 | Ingredients, weights, and per-outcome result counts (`coal_block`'s 9 flint / 9 gunpowder, Kevin, 2026-09-20); datapack-overridable (`domains/recipe.md`) |
| The vanilla `minecraft:diamond` as the success product, always count 1 | 1.0 | `decisions/DEC-006-vanilla-diamond.md`, `decisions/DEC-007-inputs-and-block-variants.md`; never a mod-specific item, never scaled by input |
| Translation keys, screens, tooltips (none at 1.0); JEI category (none at 1.0.0, a 1.1 ticket) | N/A | `domains/ui.md` — nothing of this mod's own is drawn at 1.0.0 |

Not public: the custom `Recipe`/`RecipeSerializer` Java classes' internal shape (constructor
signature, codec field order internal to the class), the weighted-pick algorithm's exact
implementation details beyond the guarantee it makes (`domains/roll.md` `ROLL-REQ-001`). Versioned
by SemVer over the surface above (`operations/release.md`).

`SURFACE-REQ-001`: a change to a stable surface is a major version.
`SURFACE-REQ-002`: **where** a datapack overrides a recipe's weights outside their valid range (a
negative value, or all three summing to zero), the system shall clamp and normalize as
`domains/recipe.md` `RECIPE-FAIL-003` and `domains/roll.md` `ROLL-REQ-003`/`ROLL-FAIL-002`
describe, rather than fail to start.
