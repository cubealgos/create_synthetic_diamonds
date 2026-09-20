---
title: "create_synthetic_diamonds spec — testing"
type: "spec"
category: "create_synthetic_diamonds"
---

# Testing (`TEST`)

| Layer | What | Where |
|---|---|---|
| Unit | The weighted-pick algorithm given a fixed `RandomSource` (each bucket boundary lands the right outcome, including edge values at exactly 0 and just under 1.0); the weight-normalization logic (sums to 1.0, handles the sum-to-zero rejection case); the field-level clamp on out-of-range weights — all pure, no Minecraft imports needed, checked by a package-purity check as the siblings use (`verifyPurePackage`/`verifyPureCore`) | `src/test` |
| Game tests | A real `MechanicalPressBlockEntity` actually finds and runs this mod's custom recipe class in belt mode; the same in world/depot mode; pressing charcoal, coal, and a coal block each consume exactly one input and produce exactly one of the three outcomes per cycle, never zero and never two; a datapack-overridden recipe with mis-summed weights loads normalized rather than failing; two recipes with the same ingredient coexist under ordinary vanilla recipe resolution | `src/gametest`, Loom `runGameTest` |
| Manual / release checklist | Watching a press cycle actually run against charcoal/coal/coal-block over enough real time to see all three outcomes appear, and confirming the observed ratio is in the right ballpark for the configured weights; confirming no recipe-viewer entry appears anywhere at 1.0 (`domains/ui.md`) | release checklist |
| Development tool | None proposed at 1.0: unlike the siblings' debug commands (which force a random outcome or fill mock data for a screen), there is no screen or trip to force here — a `/synthetic_diamonds debug roll <weights>` command that prints the outcome distribution over N simulated rolls is a candidate if manual verification of the weighted pick proves awkward, deferred to the first ticket | `synthetic_diamonds.debug` (if added), `just client` |

**What is genuinely hard here, stated plainly:** the mutual-exclusivity guarantee itself
(`domains/roll.md` `ROLL-REQ-001`) is fully unit-testable given a fixed random source — this is a
pure algorithm with no Minecraft dependency once the three weights and a roll value are inputs.
What is *not* pure-testable is whether the custom recipe class is actually found and run by a real
`MechanicalPressBlockEntity` — that depends on `RecipeManager`/`RecipeMap` behaving as the research's
disassembly predicts (`04-architecture.md` `ARCH-DEC-002`), which only a game test against a real
server world can confirm.

`TEST-REQ-001`: every `RECIPE-REQ`, `ROLL-REQ`, and `UI-REQ` names its test in the ticket that
implements it.
`TEST-REQ-002`: a deliberate-break proof for the pure-package check, once.
`TEST-REQ-003`: a game test proves this mod's recipe class coexists in the same `RecipeMap` bucket
as Create Fly's own vanilla `PressingRecipe` instances without disturbing their independent-roll
behaviour, given `04-architecture.md`'s stated reason for rejecting the shared-method mixin
alternative.
