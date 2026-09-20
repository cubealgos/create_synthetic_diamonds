---
title: "create_synthetic_diamonds DEC-004 — Java 25, Gradle 9.5.1, Loom 1.17, Kotlin DSL, version catalog, one Gradle project, zero mixin targets"
type: "spec"
category: "create_synthetic_diamonds"
---

# `DEC-004` — Java 25, Gradle 9.5.1, Loom 1.17, Kotlin DSL, version catalog, one Gradle project, zero mixin targets

**Status:** decided by Kevin, 2026-09-20.

The same toolchain as all three siblings, verified in the vault's Minecraft notes: Java 25, Gradle
9.5.1, Loom 1.17, Kotlin DSL with a version catalog. One Gradle project, for the same reason
`create_brass_compass` and `create_metered_motor` give — no sim layer to keep pure, and the mod's
one genuinely pure surface (the weighted-pick algorithm and the weight-normalization logic,
`operations/testing.md`) is checked by a package-purity check without a second module.

**Unlike every sibling so far, this mod's mixin config has no targets at all.** The custom recipe
class needed to make the three chance outcomes mutually exclusive is found by the vanilla
mechanical press through ordinary `Recipe.getType()`/`RecipeType` registry mechanics —
`RecipeManager`/`RecipeMap` bucket recipes by the live `getType()` Java object, and
`MechanicalPressBlockEntity.getRecipe()` looks up that same literal `AllRecipeTypes.PRESSING`
static field — not through a hook into any private vanilla or Create Fly method (research §C,
`04-architecture.md` `ARCH-DEC-001`, `ARCH-DEC-002`). `create_villager_customers` needed two mixin
targets for its own reasons (no Fabric event for the villager brain, no non-player checkout API);
this mod needs none.

Alternative considered: splitting a pure module from the Minecraft-facing one, as
`create_civilization` does. Rejected for the same reason as every sibling: the pure surface is small
enough that a package check gives the same guarantee at a fraction of the build complexity. Cost if
wrong: if the first ticket finds the zero-mixin approach does not work as predicted, the fallback is
one narrowly-scoped mixin target on this mod's own recipe lookup (`04-architecture.md`
`ARCH-FAIL-002`) — a ticket-sized addition, not a toolchain change.
