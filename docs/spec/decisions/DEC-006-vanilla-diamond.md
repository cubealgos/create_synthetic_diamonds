---
title: "create_synthetic_diamonds DEC-006 — The success product is the plain vanilla diamond, no item of this mod's own"
type: "spec"
category: "create_synthetic_diamonds"
---

# `DEC-006` — The success product is the plain vanilla diamond, no item of this mod's own

**Status:** decided by Kevin, 2026-09-20.

A successful press cycle yields exactly one `minecraft:diamond` — the same item vanilla mining,
trading, and every other vanilla source already produce. This mod never defines a diamond variant,
a "synthetic" marker, or any item of its own at all (`rulings-2026-09-20.md`).

Rationale: diamonds already have every downstream use in vanilla and in Create — enchanting,
tools, armor, beacons, Create's own diamond-tier recipes where they exist — and a mod-specific
diamond item would either need to duplicate all of that usability or silently be worth less than a
real diamond, either of which is more surface than this mod needs. Keeping the product plain vanilla
also keeps `contracts/public-surface.md` and `contracts/data-contract.md` as small as they are: no
item id to register, no texture or model to ship, no tag membership to maintain.

Alternative considered: a mod-specific "synthetic diamond" item, distinguishable from a mined one
(for flavor, or to gate it out of certain recipes). Rejected: Kevin's own framing treats this as an
alternative diamond *source*, not a different diamond, and the plain-vanilla product is simpler in
every respect this decision touches. Cost if wrong: swapping in a mod-specific item later is a
larger change than adding a config toggle would be, since it touches the recipe's result item id
and this mod's entire public surface — but nothing in the current design blocks it.
