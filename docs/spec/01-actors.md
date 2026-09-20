---
title: "create_synthetic_diamonds spec — actors: who touches a press and what each may do"
type: "spec"
category: "create_synthetic_diamonds"
---

# 01 — Actors

| ID | Actor | May | May not |
|---|---|---|---|
| `ACTORS-001` | **Player** | Build and feed a mechanical press with charcoal, coal, or coal blocks; collect whatever comes out; read the recipe JSON files directly to learn the odds | Configure this mod through a screen: there is no screen or setting of its own to open (`domains/ui.md`) |
| `ACTORS-002` | **Server** | Resolve which recipe applies to a given input; roll the exclusive three-way pick; apply the result (a diamond, flint, or gunpowder) and consume the input, once per 240-tick press cycle | Trust a client with any of this: the press's recipe resolution and roll are entirely server-side, as for every Create Fly processing recipe |
| `ACTORS-003` | **Create Fly** (dependency) | Supply the mechanical press block, its belt and world/depot processing modes, and the `AllRecipeTypes.PRESSING` recipe type this mod's custom recipe class reports itself under | Be modified: this mod adds a recipe class and JSON files, and touches nothing of Create Fly's own code (`04-architecture.md`) |
| `ACTORS-004` | **Datapack or resource pack author** | Retune the three recipes' ingredients and weights as ordinary JSON files under `data/synthetic_diamonds/recipe/weighted_pressing/` (`domains/recipe.md`, `contracts/public-surface.md`) | Change the mutual-exclusivity guarantee itself, or the recipe class's `getType()`/`getSerializer()` wiring: those are code (`domains/roll.md`) |
| `ACTORS-005` | **Server operator** | Install and remove the mod; nothing to configure beyond the recipe JSON files above | Read or edit an in-flight press cycle from outside the world save: there is nothing to read, since the mod persists no state of its own (`contracts/data-contract.md`) |
| `ACTORS-006` | **Modrinth visitor / potential server operator** | Read the listing, decide whether to install it based on the stated odds and by-products | Expect a recipe-viewer entry at 1.0: JEI/EMI support is deferred (`domains/ui.md`) |
| `ACTORS-007` | **Contributor** | Build, test and change the mod under MIT | Add telemetry or network calls (`operations/compliance.md`) |

## Findings from writing this

- **`FINDING-1`** There is no menu, no configuration screen, and no player identity anywhere in
  the mechanic: a press runs the same for any player who built it, exactly like any other Create
  Fly processing recipe. This mod's whole technical shape (`04-architecture.md`) exists only to
  solve one problem — making three chance outcomes mutually exclusive — not to add anything a
  player interacts with directly.
- **`FINDING-2`** The server is the only actor with a random hand, and unlike `create_metered_motor`
  or `create_villager_customers`, the randomness here gates *what comes out*, not *whether
  something happens at all*: every press cycle that consumes an input produces exactly one of the
  three results, never nothing and never more than one (`domains/roll.md`).
- **`FINDING-3`** The datapack author's dial is purely economic — the three weights and which items
  count as inputs. Nothing about *how* the roll stays exclusive is data; that guarantee is Java,
  because it cannot be expressed any other way in Create Fly's shipped roll logic (research §E).
- **`FINDING-4`** Unlike every sibling so far, there is no actor here who ever opens a screen this
  mod drew, and no actor whose interaction this mod validates against anything: the press already
  validates its own inputs (a valid `Ingredient` match) the same way it does for every Create Fly
  pressing recipe, and this mod adds nothing on top of that.
