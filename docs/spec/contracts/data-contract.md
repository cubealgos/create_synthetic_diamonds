---
title: "create_synthetic_diamonds spec — data contract: nothing persisted, and why"
type: "spec"
category: "create_synthetic_diamonds"
---

# Data contract (`DATA`)

## What this mod persists

**Nothing.** This is genuinely simpler than every sibling in the family so far —
`create_villager_customers` still persists two transient memory-module values per villager
(`vault/projects/create_villager_customers/spec/contracts/data-contract.md`), and `create_brass_compass` persists a
versioned data component on every compass item. `create_synthetic_diamonds` writes no memory
module, no data component, no config file, and no world-save state of its own at all.

## Why there is nothing to persist

Every press cycle is stateless from this mod's point of view: a recipe is matched against the
current input, one roll happens, one result is produced, the input is consumed — and nothing about
that cycle needs to be remembered for the next one. The press itself already persists everything
that matters (its position, its kinetic state, the item queued on its belt or in its depot) as
ordinary Create Fly block-entity state that exists regardless of whether this mod is installed. The
recipe JSON files are data, loaded fresh at every datapack (re)load like any other recipe, and are
not "state" in the sense this contract is about — they are input, not memory.

## Rules

| ID | Rule |
|---|---|
| `DATA-REQ-001` | The system shall write no memory module, data component, capability, or NBT tag of its own to any entity, block entity, or item stack. |
| `DATA-REQ-002` | The system shall write no file, config, or database of its own to the world save or the server's data folder, beyond the recipe JSON files that are ordinary datapack content, not runtime state. |
| `DATA-REQ-003` | A malformed or unreadable recipe JSON on load shall degrade to "that recipe does not exist" (`domains/recipe.md` `RECIPE-FAIL-001`, `RECIPE-FAIL-002`), never to a corrupted save or a crash. |

## Versioning: not applicable, and why

There is no schema to version, because there is no persisted data structure at all — not even the
transient, disposable-on-load-into-an-older-build kind `create_villager_customers`'s memory modules
are. A future version changing the recipe JSON shape (`domains/recipe.md`) is an ordinary datapack
schema change, handled the way any Create Fly or vanilla recipe format change is: old JSON either
still decodes or it does not, with no save-file migration involved either way, since nothing was
ever written to a save.

## Out of scope (sheet §8)

No record of past press cycles anywhere: the only evidence a diamond was produced is the diamond
itself sitting in the press's output, exactly as for any Create Fly recipe. No import of another
mod's recipe or drop-rate state; no export.
