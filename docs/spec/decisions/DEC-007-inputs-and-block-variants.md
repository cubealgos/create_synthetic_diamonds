---
title: "create_synthetic_diamonds DEC-007 — Charcoal, coal, and coal block all press; no charcoal block; the block's failure by-product scales ×9, its diamond odds do not"
type: "spec"
category: "create_synthetic_diamonds"
---

# `DEC-007` — Charcoal, coal, and coal block all press; no charcoal block; the block's failure by-product scales ×9, its diamond odds do not

**Status:** decided by Kevin, 2026-09-20, in full — both that the coal variants and their block
form should work "if both pressing recipes are free," and, in a same-day follow-up ruling after
reading the first draft of this spec, the block recipe's exact roll shape.

## Both coal variants, and the coal block, press; no charcoal block exists

Both `minecraft:charcoal` and `minecraft:coal` get their own pressing recipe, at identical odds
(`decisions/DEC-005-chance-and-outcomes.md`). The coal block also presses, matched via the Fabric
convention tag `c:storage_blocks/coal` (confirmed to contain exactly `minecraft:coal_block`,
research §D). **No charcoal block exists to add a recipe for**: confirmed absent from both the
Create Fly jar and vanilla 26.2 by an exhaustive case-insensitive grep across every asset,
blockstate, item, recipe, and class in both jars (research §D) — this is simply nothing to build,
not an open question.

## The block recipe's roll shape: same 0.5% odds, ×9 failure by-product only

The spec sheet's first draft proposed a choice between two options identified in research §D: (a)
one scaled roll at 9× the diamond chance (at most one diamond per block press, lower variance), or
(b) nine independent rolls resolved at once (up to nine diamonds per block press, higher variance).
**Kevin ruled on 2026-09-20 for a third shape, neither of the two proposed:**

> "one roll per block press at the same 0.5%, the failure by-product scaled ×9 (9 flint or 9
> gunpowder), so blocks are compression, not a shortcut."

One roll per block press, as option (a) — but the diamond chance stays at the plain single-item
0.5%, not scaled up to 4.5%; only the *by-product* counts scale, to 9 flint or 9 gunpowder. A coal
block therefore presses at a **worse** diamond-per-charcoal rate than nine single-item presses
would (one 0.5% roll consumes nine items' worth of material, instead of nine independent 0.5%
rolls), which is exactly Kevin's point: a block is a convenience for batching material through one
press cycle and getting a lossless by-product back, not a way to farm diamonds faster. See
`domains/recipe.md` `RECIPE-REQ-003` and `02-journeys.md` `UC-005` for the resulting recipe shape
and journey.

Both of the sheet's original options are superseded by this ruling, not chosen between; recorded
here as the full history of how this decision was reached, per
`rulings-2026-09-20.md`'s follow-up section.
