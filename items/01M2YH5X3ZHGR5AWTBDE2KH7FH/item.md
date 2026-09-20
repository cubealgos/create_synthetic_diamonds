---
schema_version: 1
id: 01M2YH5X3ZHGR5AWTBDE2KH7FH
key: SD-8
type: feat
title: JEI category for the pressing recipe
created_by: kevin
created_at: 2026-09-20T04:28:51Z
---

## Scope

**Backlog, M3 (Later) — deferred to 1.1 by Kevin's own ruling, not scoped for 1.0.0.** A
self-contained JEI recipe-viewer category for this mod's three pressing recipes: Create Fly's own
`PressingCategory` hard-casts every recipe it renders to the concrete `PressingRecipe` record and
throws `ClassCastException` on this mod's differently-typed recipe class (research §C,
`04-architecture.md` `ARCH-DEC-004`), so integration means a small `jei_mod_plugin` Fabric
entrypoint registering this mod's own category — never patching or reusing Create Fly's — or no JEI
integration at all. No EMI integration exists anywhere in Create Fly to piggyback on instead
(`UI-REQ-002`).

## Approach

Not designed in detail here — this ticket is a placeholder recording the decision and its reason,
per `UI-DEC-001`: "no JEI category at 1.0.0, a 1.1 ticket" (Kevin, 2026-09-20). When picked up:
register a `jei_mod_plugin` entrypoint, a category rendering ingredient/weights/results read
directly off this mod's own recipe class (never Create Fly's `PressingRecipe` cast), and confirm it
coexists cleanly alongside Create Fly's own pressing category without either crashing on the
other's recipes.

## Acceptance criteria

- [ ] Not started — backlog, M3. Acceptance criteria to be written when this ticket is picked up
      for 1.1.

## Constraints and prior findings

`domains/ui.md` `UI-REQ-002`, `UI-DEC-001`; `04-architecture.md` `ARCH-DEC-004`;
`decisions/DEC-009-jei-deferred.md`. Blocked by SD-3 (needs a real, shipped recipe class to render
— cannot be built against SD-2's in-code test recipe alone). The press finds and runs these recipes
regardless of whether this ticket ever ships (`04-architecture.md`); this is a discoverability
improvement only, never load-bearing for the mechanic itself.
