---
schema_version: 1
id: 01M2YH2GBP4YGPATW8GE3XZBZE
key: M1
title: The recipes and the roll
status: todo
created_at: 2026-09-20T04:26:59Z
---

## Goal

The mechanism itself: a mechanical press finds and runs this mod's own recipes, and every press
cycle yields exactly one of {diamond, flint, gunpowder}, never zero and never two.

## Scope

The custom `Recipe`/`RecipeSerializer` pair and the pure weighted-exclusive roll it carries
(`SD-2`); the three recipe data files (charcoal, coal, coal block) and the game tests proving a
real mechanical press runs them, belt and world/depot mode alike (`SD-3`); the dev-only debug
command for eyeballing the roll's distribution quickly (`SD-4`). Not the Modrinth listing or the
release itself (`M2`).

## Exit criteria

- A real `MechanicalPressBlockEntity` finds and runs this mod's recipe class with zero mixin, or
  the documented mixin fallback is taken and recorded.
- All three recipes ship, each proven exclusive over many seeded rolls in a game test.
- The debug command reports an accurate tally against the live weights.

## Tickets

SD-2, SD-3, SD-4.

## Depends on

M0 (the bootstrapped scaffold, `verifyPurePackage`, the empty mixin config).
