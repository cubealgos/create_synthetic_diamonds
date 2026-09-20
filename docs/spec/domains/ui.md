---
title: "create_synthetic_diamonds spec — UI: a stub, and why"
type: "spec"
category: "create_synthetic_diamonds"
---

# `UI` — nothing is shown, and why

## 1. Purpose

Say plainly what a player sees of this mod at 1.0 (almost nothing) and why, rather than leave the
absence unexplained. Not the recipes themselves (`domains/recipe.md`) and not the roll mechanism
(`domains/roll.md`).

## 2. Dimensions

| Dimension | Answer |
|---|---|
| **Actors** | The player (`ACTORS-001`) presses charcoal, coal, or coal blocks and reads the result from the press's own output, exactly as for any Create Fly pressing recipe; a Modrinth visitor (`ACTORS-006`) reads the odds from the mod's description, not from in-game. |
| **Over time** | Nothing of this mod's own is ever drawn, opened, or updated on screen, at any point from install to a press cycle completing. |
| **Multiplicity** | Zero screens, zero tooltips, zero new items, zero recipe-viewer categories at 1.0. |
| **Unwanted** | A player expecting to find these recipes in JEI's "Pressing" tab and finding nothing, since Create Fly's own JEI category cannot render this mod's recipe class without crashing (research §C) — this mod deliberately omits itself rather than appear broken. |
| **Not-you** | A player who never presses charcoal sees no change to any existing screen, tooltip, or recipe-viewer entry anywhere in their game. |

## 3. Enumerations

### What exists vs. what does not, at 1.0

| Surface | Present? |
|---|---|
| New screen | No |
| New tooltip | No |
| New item | No — the product is the vanilla diamond (`decisions/DEC-006-vanilla-diamond.md`) |
| New block | No |
| JEI category | No at 1.0.0, a 1.1 ticket (Kevin, 2026-09-20; `04-architecture.md` `ARCH-DEC-004`, `UI-DEC-001`) |
| EMI category | No — no EMI integration exists anywhere in Create Fly to add one to (research §C) |
| Recipe JSON files, readable directly | Yes — the only place the odds are visible in-game/in-install |

## 4. Use cases

`UC-008` in `02-journeys.md`.

## 5. Requirements

| ID | Requirement | Priority | From |
|---|---|---|---|
| `UI-REQ-001` | The system shall add no screen, tooltip, item, or block of its own at 1.0. | Must | `00-context.md` |
| `UI-REQ-002` | The system shall add its own JEI recipe-viewer category, rather than Create Fly's built-in pressing category, as a 1.1 ticket — never Create Fly's own category, since the latter crashes on this mod's recipe class (research §C). | Should, scheduled for 1.1 | Kevin, 2026-09-20; `04-architecture.md` `ARCH-DEC-004` |
| `UI-REQ-003` | The system shall keep the recipe JSON files themselves as the only documented way to read the exact odds, alongside the mod's own README/Modrinth description. | Must | `UC-008` |

## 6. Failure modes

| ID | Failure | Response |
|---|---|---|
| `UI-FAIL-001` | A player opens JEI's Pressing category expecting to see these recipes | Sees nothing for them; not a crash, since this mod's recipes are deliberately excluded from Create Fly's own JEI category rather than handed to it (`UC-008`). |
| `UI-FAIL-002` | A player mistakes the absence of a JEI entry for the recipe not existing | Corrected by the mod's own description; the press finds and runs the recipe regardless of any recipe viewer (`04-architecture.md`). |

## 7. Open questions

None: resolved by Kevin, 2026-09-20 — no JEI category at 1.0.0, a 1.1 ticket
(`rulings-2026-09-20.md`, `UI-DEC-001`).

## 8. Decisions

- `UI-DEC-001` — **No recipe-viewer integration at 1.0.0; a self-contained JEI category is a 1.1
  ticket** (decided by Kevin, 2026-09-20, confirming the sheet's own proposal: "no JEI category at
  1.0.0, a 1.1 ticket"): the press works regardless of whether it is displayed anywhere, and a
  self-contained JEI category is a self-contained addition that can be built on top of the recipe
  class at any later point without touching the recipe or roll domains. **Cost if wrong:** none —
  this is now a scheduling decision, not an open design question; players discover the mechanic
  through the description or trial-and-error only until the 1.1 ticket ships.
