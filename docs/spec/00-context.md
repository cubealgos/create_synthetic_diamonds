---
title: "create_synthetic_diamonds spec — context: why, for whom, and what it will not do"
type: "spec"
category: "create_synthetic_diamonds"
---

# 00 — Context

## Why this exists

`create_civilization`'s early game still leans on hand-mining for diamonds. Kevin's idea, verbatim:
"hitting charcoal with a press should either destroy the charcoal or create a diamond; the chance
really low so this is a bit harder to automate than mining for diamonds early game." A Create
mechanical press already sits on every serious charcoal or coal line; giving it a low, honest
chance at a vanilla diamond turns an existing piece of infrastructure into a slow alternative to
digging, without adding a new item, block, or screen to learn.

It is the fourth small Create Fly add-on built this way, and — in Kevin's own words from the
rulings session — "building the 4th mod is now only repetition, our ideas are still very simple."
That framing describes the team's growing fluency, not a reason to skip the spec: like its three
siblings, this mod ships to real users on Modrinth, and the classification reasoning that applied
to each of them applies here unchanged (`decisions/DEC-001-classification.md`).

## Who it is for

- Players on Minecraft 26.2 with Fabric and Create Fly who already run a charcoal or coal line and
  want a slow, low-risk chance at diamonds from it.
- Server operators who install it alongside Create Fly and expect nothing to configure.
- Modpack authors and datapack authors who want to retune the odds or the ingredients through data
  (`domains/recipe.md`, `domains/roll.md`) without touching Java.

## Business context

No business model, no revenue, no telemetry. Published on Modrinth under MIT, source on the
cubealgos Forgejo with a GitHub mirror and tracker, public from the first commit
(`decisions/DEC-003-licence.md`) — the same place the three siblings ended up.

## What it will not do

- No new item, block, or screen of this mod's own: the only product is the vanilla
  `minecraft:diamond` (`decisions/DEC-006-vanilla-diamond.md`); the only by-products are vanilla
  flint and gunpowder.
- No change to any other Create Fly recipe type: crushing, milling, cutting, mixing and the rest
  keep their own independent-roll behaviour exactly as Create Fly ships it — this mod's custom
  recipe class only ever affects the recipes it defines (`04-architecture.md` `ARCH-DEC-002`).
- No change to the vanilla diamond item itself: no new use, no new property, nothing that makes a
  synthetic diamond behave differently from a mined one.
- No protection or permission system: any player with a mechanical press and the input items can
  run this mechanic, exactly as any Create Fly machine is fair game to whoever can reach it.
- No recipe-viewer integration at 1.0.0 (JEI or otherwise): the press finds and runs the recipe
  either way; a self-contained JEI category is a 1.1 ticket, not a 1.0.0 scope item (Kevin,
  2026-09-20; `domains/ui.md` `UI-DEC-001`, `decisions/DEC-009-jei-deferred.md`).
- No shared throughput cap across multiple presses: each press rolls independently, with no
  diminishing returns designed in — accepted by Kevin, 2026-09-20, as the intended reward for
  building more presses, not a gap to close (`04-architecture.md` `ARCH-FAIL-005`,
  `domains/roll.md` `ROLL-DEC-003`, `decisions/DEC-010-scaling-accepted.md`, research §F).

## Success

Kevin sets up one mechanical press pulling charcoal from a furnace line, lets it run for a few
real-time hours, and finds diamonds turning up in the output chest alongside a much larger pile of
flint and a smaller pile of gunpowder — roughly in the 0.5% / 95% / 4.5% proportions Kevin picked —
with nothing about the press or the world that looks any different from a vanilla Create Fly
press, other than what comes out of it.
