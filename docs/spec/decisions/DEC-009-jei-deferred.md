---
title: "create_synthetic_diamonds DEC-009 — No JEI category at 1.0.0; a self-contained one is a 1.1 ticket"
type: "spec"
category: "create_synthetic_diamonds"
---

# `DEC-009` — No JEI category at 1.0.0; a self-contained one is a 1.1 ticket

**Status:** decided by Kevin, 2026-09-20, confirming the spec sheet's own proposal and turning it
into a concrete scope item.

Create Fly's own JEI category for pressing (`PressingCategory`) hard-casts every recipe it renders
to the concrete `PressingRecipe` record and would throw `ClassCastException` on this mod's
differently-classed custom recipe (research §C); JEI support therefore needs this mod's own
category (a small `jei_mod_plugin` Fabric entrypoint), and there is no EMI integration anywhere in
Create Fly to use instead (research §C, §E). The spec sheet's first draft proposed shipping without
any recipe-viewer integration at 1.0 and revisiting later.

**Kevin, 2026-09-20: "no JEI category at 1.0.0, a 1.1 ticket."** This confirms the proposal for
1.0.0 and additionally commits a specific version to build it in, rather than leaving "later" open
indefinitely. The press finds and runs the recipe regardless of whether it is displayed anywhere
(`04-architecture.md` `ARCH-DEC-004`); at 1.0.0 the odds are readable only from the mod's own
README/Modrinth description or the recipe JSON files directly (`domains/ui.md`).

Alternative considered: shipping a JEI category at 1.0.0 alongside the recipe class. Not chosen:
Kevin's ruling explicitly defers it, keeping the first release's surface to exactly the recipe
mechanism itself. Cost if wrong: none — the JEI category is additive and does not touch the recipe
or roll domains when it is eventually built (`domains/ui.md` `UI-DEC-001`, `UI-REQ-002`).
