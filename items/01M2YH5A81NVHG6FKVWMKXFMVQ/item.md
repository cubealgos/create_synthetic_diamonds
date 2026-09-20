---
schema_version: 1
id: 01M2YH5A81NVHG6FKVWMKXFMVQ
key: SD-4
type: feat
title: "Dev-only debug command: roll the press N times and print the tally"
created_by: kevin
created_at: 2026-09-20T04:28:31Z
---

## Scope

A dev-only, single-player-useful command `/synthetic_diamonds debug press <count>` that runs the
pure weighted roll from `synthetic_diamonds.model` (SD-2) `<count>` times against the live
in-force weights and prints the tally (how many diamond/flint/gunpowder outcomes resulted) to the
command's sender — makes the exclusivity guarantee and the real-world odds directly observable
without waiting through hundreds of real 12-second press cycles.
`operations/testing.md`'s testing table lists this shape as a "candidate if manual verification of
the weighted pick proves awkward"; this ticket commits to building it, since a fast in-game
distribution check is worth more than trusting the unit tests' bucket-boundary coverage alone once
real recipe data exists. No new screen, tooltip, item, or persisted state (`UI-REQ-001`) — a
command only.

## Approach

Register a Fabric command under `synthetic_diamonds.debug`, styled after
`create_villager_customers`' `DebugCommand`/`DebugCommandGameTest` shape (`/villager_customers
debug ...`) but adapted to this mod's single roll rather than a trip simulation: `debug press
<count>` calls the same `synthetic_diamonds.model` roll entry point SD-2 exposes, `<count>` times,
accumulates a tally, and sends it back as a chat message (`N diamond, N flint, N gunpowder out of
<count>`). Dev-only: no permission gate beyond the vanilla op-level a debug/cheat command normally
needs, and no translation key added (`SourceSurfaceTest`'s key-coverage test stays vacuous for this
mod unless a key is actually added — add one only if the command's own text needs localization, and
add the matching `en_us.json` entry in the same commit if so).

## Acceptance criteria

- [ ] `/synthetic_diamonds debug press <count>` runs the roll `<count>` times and prints an
      accurate tally.
- [ ] A game test invokes the command and asserts the tally sums to `<count>` and only ever
      contains the three known outcome buckets.
- [ ] `SourceSurfaceTest` still passes (no networking type introduced; any new translation key has
      an `en_us` entry).
- [ ] `just check` green.

## Constraints and prior findings

`operations/testing.md`'s "Development tool" row (this ticket is where the note's "candidate if
manual verification proves awkward" is committed to). Blocked by SD-2 (calls its pure roll
directly). Not blocked by SD-3: the command rolls against in-force weights, not against any
specific shipped recipe file, so it works against SD-2's in-code test weights even before SD-3's
JSON files land — but is more useful once real weights are in force, so implementation in practice
will likely follow SD-3. No screen, tooltip or persisted state (`UI-REQ-001`); this command is the
only player-facing (dev-facing) surface this mod adds at 1.0 beyond the recipes themselves.
