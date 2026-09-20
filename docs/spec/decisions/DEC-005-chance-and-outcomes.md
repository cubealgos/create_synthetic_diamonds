---
title: "create_synthetic_diamonds DEC-005 — 0.5% diamond, 95% flint, 4.5% gunpowder, mutually exclusive"
type: "spec"
category: "create_synthetic_diamonds"
---

# `DEC-005` — 0.5% diamond, 95% flint, 4.5% gunpowder, mutually exclusive

**Status:** decided by Kevin, 2026-09-20.

Per press cycle, exactly one of three outcomes: 0.5% diamond, 95% flint, 4.5% gunpowder — never
zero, never two at once. Kevin picked 0.5% over 1% and 0.1%: about 200 charcoal per diamond, and a
full stack of diamonds (64) costs 12,800 charcoal — a deliberately slow rate, "a bit harder to
automate than mining for diamonds early game" in Kevin's own words (`00-context.md`). Flint and
gunpowder were chosen as the failure by-products because they "make sense" as things a press could
plausibly compress out of charred fuel, with flint the overwhelming majority outcome and gunpowder
the rarer of the two (Kevin, `rulings-2026-09-20.md`).

**"Mutually exclusive" is the one detail that costs real engineering here**, and it is the reason
this mod needs any Java at all rather than being pure recipe data: Create Fly's own chance-output
system rolls every result independently, with no built-in mode for "exactly one of N, always"
(research §C). Getting the exclusivity guarantee requires the custom recipe class
`domains/roll.md`'s `ROLL-REQ-001` and `decisions/DEC-008-exclusive-roll-mechanism.md` describe —
the destroy-on-miss half of the mechanic (a chance of a diamond, otherwise nothing) would have been
pure JSON on its own (research §E), but resolving "otherwise" into an exclusive flint-or-gunpowder
pick is what forces the Java.

The balance implications of 0.5% — both that it comfortably satisfies "harder to automate than
mining" at single-press scale, and that it has no built-in defense against many presses run in
parallel — are research findings, not part of this ruling, and are carried forward as an explicit
open question rather than folded into this number (`README.md` "Open questions gathered",
`04-architecture.md` `ARCH-FAIL-005`, research §F). This decision fixes the three percentages only;
it does not resolve horizontal scaling.
