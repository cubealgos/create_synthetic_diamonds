---
title: "create_synthetic_diamonds DEC-001 — Distributed product, full spec sheet"
type: "spec"
category: "create_synthetic_diamonds"
---

# `DEC-001` — Distributed product, full spec sheet

**Status:** decided by Kevin, 2026-09-20.

This is the fourth small Create Fly add-on built on the way to `create_civilization`. Kevin's own
framing from the rulings session — "building the 4th mod is now only repetition, our ideas are
still very simple" — describes the team's fluency with this shape of project, not a reason to skip
the spec: like all three siblings, it ships to real users on Modrinth, not only into the
civilization mod, and follows the same process, the same reasoning the siblings' own `DEC-001`s
already covered exactly this tension. The pipeline is the same as every sibling's: Sonnet subagents
draft, Claude reviews, Kevin reviews the findings.

Alternative considered: treating it as internal tooling and skipping §5–§7 (interface contracts,
compliance, release engineering), since `create_civilization` is the real motivation. Rejected for
the same reason as all three siblings: a mod that changes what a Create mechanical press can
produce invites bug reports and balance questions from server operators, whether or not the
reporter ever touches the civilization mod. Cost if wrong: an evening of spec for a mod with no
block, no item, and no screen of its own — the smallest surface of any sibling so far.
