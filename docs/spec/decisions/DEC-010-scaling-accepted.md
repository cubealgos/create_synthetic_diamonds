---
title: "create_synthetic_diamonds DEC-010 — Horizontal press scaling is accepted, not capped"
type: "spec"
category: "create_synthetic_diamonds"
---

# `DEC-010` — Horizontal press scaling is accepted, not capped

**Status:** decided by Kevin, 2026-09-20, resolving a research finding the spec sheet's first
draft had carried forward as an open question rather than resolved.

The research pass found that a single press (default `bulkPressing = false`) yields roughly 1.5
diamonds/hour at 0.5% — comfortably slower than an assumed ~3–10/hour early-game hand-mining
ballpark, satisfying "harder to automate than mining" at single-press scale — but that press
throughput has no shared cap across multiple presses: each press rolls entirely independently, so
a player building several presses off one line can scale well past hand-mining rates (research
§F). The sheet's first draft flagged this explicitly rather than resolving it.

**Kevin, 2026-09-20: "scaling accepted: no cap and no brake, ten presses beating hand-mining is an
achievement."** This mod adds no shared cap, no per-press cooldown, and no diminishing-returns
mechanic across multiple presses (`domains/roll.md` `ROLL-DEC-003`, `04-architecture.md`
`ARCH-FAIL-005`). Building enough presses to out-pace hand-mining is the intended reward for the
automation investment — build cost, belt/farm infrastructure, and real time — not a balance gap to
close.

Alternative considered: a shared cap, a per-press cooldown, or a lower base chance to bound
horizontal scaling. Rejected: Kevin's ruling treats the automation investment itself (building and
feeding multiple presses) as the intended cost, not something needing an artificial brake on top.
Cost if wrong: none identified by this ruling — if a later balance pass finds it too fast in
practice, a per-press cooldown or a lower chance are both small, additive changes to
`domains/roll.md`/`domains/recipe.md`, not a redesign.
