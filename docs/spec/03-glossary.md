---
title: "create_synthetic_diamonds spec — glossary"
type: "spec"
category: "create_synthetic_diamonds"
---

# 03 — Glossary

| Term | Means |
|---|---|
| **pressing recipe** | A Create Fly `create:pressing`-type recipe: one `Ingredient` in, one or more results out, resolved by a mechanical press. Create Fly's own concrete implementation is the `PressingRecipe` record; this mod adds its own class alongside it, not in place of it. |
| **weighted-exclusive roll** | This mod's own mechanism: one roll against three weights summing to 100%, returning exactly one of diamond, flint, or gunpowder — never zero, never more than one (`domains/roll.md`). Not a Create Fly concept; Create Fly has no built-in equivalent (research §C). |
| **`ProcessingOutput`** | Create Fly's own per-result payload on a recipe's `results` list (`Holder<Item> item, int count, DataComponentPatch components, float chance`). Rolled *independently* per entry — the reason a naive three-`ProcessingOutput` recipe cannot express "exactly one of three" (research §B, §C). Contrasted here, not used by this mod's own results. |
| **custom recipe type / serializer** | This mod's own `Recipe<SingleRecipeInput>` implementation and its `RecipeSerializer`, registered under this mod's id (proposed: `synthetic_diamonds:weighted_pressing`), whose `getType()` returns the literal `AllRecipeTypes.PRESSING` static field so the vanilla mechanical press finds it with zero mixin (`domains/roll.md`, research §C). |
| **belt mode** | A mechanical press processing an item carried past it on a Create belt (`tryProcessOnBelt`). Shares the identical recipe-lookup and roll path with world/depot mode (research §B). |
| **world / depot mode** | A mechanical press processing an item resting on the ground or a depot beneath it (`tryProcessInWorld`). Shares the identical recipe-lookup and roll path with belt mode (research §B). |
| **basin mode** | A mechanical press's *unrelated* "basin compression" feature: auto-crafting a vanilla shaped/shapeless crafting recipe from a basin full of ingredients (`tryProcessInBasin`). Not `create:pressing`, not reached by anything in this mod, irrelevant here (research §B). |
| **`bulkPressing`** | A Create Fly server config (`AllConfigs.server().recipes.bulkPressing`), default `false`. When `false`, a press consumes exactly one input item and rolls exactly once per cycle, however large the queued stack; when `true`, a full stack is processed (and rolled once per item) in one cycle. This mod does not change the default (research §B). |
| **cycle** | `PressingBehaviour.CYCLE = 240` ticks (12 real seconds at normal tick rate); a recipe roll happens once per cycle, when the press's `runningTicks` reaches half that (research §B). |
| **`c:storage_blocks/coal`** | The Fabric convention tag Create Fly ships containing exactly `minecraft:coal_block`; the ingredient this mod's coal-block recipe matches against (research §A, §D). |
| **`minecraft:coals`** | The vanilla tag containing exactly `minecraft:coal` and `minecraft:charcoal`; confirmed to exist, but this mod uses two separate recipes rather than this tag, mirroring Create Fly's own single-ingredient pressing recipe convention (research §A, `domains/recipe.md`). |
| **datapack-overridable** | This mod's recipe JSON files, decoded by a custom codec but otherwise ordinary datapack files under `data/synthetic_diamonds/recipe/weighted_pressing/`; a datapack can replace ingredients or weights without touching Java (research §C, `domains/recipe.md`). |
| **normalize and log once** | This mod's proposed handling for a datapack recipe whose three weights do not sum to 1.0: scale them to sum to 1.0 and log a warning once, rather than fail to load (`domains/roll.md` `ROLL-FAIL-001`), mirroring the clamp-and-log pattern `create_villager_customers`' `SURFACE-REQ-002` used for its own data constants. |
