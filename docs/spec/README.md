---
title: "create_synthetic_diamonds spec — index"
type: "spec"
category: "create_synthetic_diamonds"
repo: "create_synthetic_diamonds"
---

# Create: Synthetic Diamonds — specification

A small Create Fly add-on for Minecraft 26.2 on Fabric: pressing charcoal, coal, or a block of coal
in a Create mechanical press rolls a small, mutually exclusive chance of yielding a vanilla
diamond, and otherwise destroys the input into flint or gunpowder — Kevin's idea, verbatim:
"hitting charcoal with a press should either destroy the charcoal or create a diamond; the chance
really low so this is a bit harder to automate than mining for diamonds early game." The fourth of
the small add-ons built on the way to `create_civilization`; it is nonetheless a distributed
product with real users and is specified as one (`decisions/DEC-001-classification.md`).

This spec is the distributed-product spec sheet in the chunked format. The sheet's sections map to
files as follows; a section marked *out of scope* says why in the file that would have held it.

| Sheet section | File |
|---|---|
| §1 Document control | this file: identifiers, state, decisions |
| §2 Executive summary and business context | `00-context.md` |
| §3 Product architecture and runtime topology | `04-architecture.md` |
| §4 Domain-driven functional specifications | `01-actors.md`, `02-journeys.md`, `03-glossary.md`, `domains/recipe.md`, `domains/roll.md`, `domains/ui.md` |
| §5 Interface contracts and integration | `contracts/platform-matrix.md`, `contracts/public-surface.md`, `contracts/data-contract.md` |
| §6 Compliance, security and governance | `operations/compliance.md` |
| §7 Release engineering, distribution and support | `operations/release.md`, `operations/testing.md` |
| §8 Migration, compatibility and out of scope | `00-context.md` §What it will not do, `contracts/data-contract.md` |
| Appendix: technical blueprints | `04-architecture.md` §Shape |

## Files and state

| File | Domain prefix | State |
|---|---|---|
| `00-context.md` | — | written |
| `01-actors.md` | `ACTORS` | written |
| `02-journeys.md` | `UC` | written |
| `03-glossary.md` | — | written |
| `04-architecture.md` | `ARCH` | written |
| `domains/recipe.md` | `RECIPE` | written |
| `domains/roll.md` | `ROLL` | written |
| `domains/ui.md` | `UI` | written |
| `contracts/platform-matrix.md` | `PLATFORM` | written |
| `contracts/public-surface.md` | `SURFACE` | written |
| `contracts/data-contract.md` | `DATA` | written |
| `operations/compliance.md` | `COMP` | written |
| `operations/release.md` | `REL` | written |
| `operations/testing.md` | `TEST` | written |

## Identifiers

`<DOMAIN>-<KIND>-<NNN>`: `RECIPE-REQ-004`, `ROLL-UC-001` (use cases are flat, see below), `UI-FAIL-001`,
`ARCH-DEC-002`. Use cases themselves are `UC-NNN`, flat across the project. Permanent; a withdrawn
item keeps its number.

## Verifications

Every row is a claim in this sheet traced to
`vault/technical/minecraft/create-fly-pressing-recipes-26-2.md` (2026-09-20, read via `javap`
against the Create Fly jar and the merged Minecraft jar — no sources jar existed for either). A
claim not in this table and not in that note is marked "to verify at the first ticket" where it
appears.

| # | Claim | Section |
|---|---|---|
| 1 | `ProcessingOutput.rollOutput` rolls every result in a recipe's `results` list independently; no exclusive/weighted-pick mode exists anywhere in the pressing (or any processing) recipe machinery | §B, §C |
| 2 | A naive independent 0.5%/95%/4.5% implementation yields nothing ≈4.75% of the time and two-or-more results at once ≈4.71% of the time — unacceptable for "exactly one, always" | §C |
| 3 | `RecipeManager`/`RecipeMap` bucket recipes by the live `Recipe.getType()` object, not by the JSON `"type"` (serializer) field; `MechanicalPressBlockEntity.getRecipe()` looks up `AllRecipeTypes.PRESSING` by that same literal static field | §C |
| 4 | A custom `Recipe<SingleRecipeInput>` class, registered under this mod's own `RecipeSerializer` id, whose `getType()` returns `AllRecipeTypes.PRESSING`, is found and run by the vanilla mechanical press (belt mode and world/depot mode) with zero mixin | §C |
| 5 | Belt mode and world/depot mode call the identical `MechanicalPressBlockEntity.getRecipe()` / roll path; basin mode is an unrelated feature (vanilla shaped/shapeless crafting-recipe compression, not `create:pressing` at all) and is irrelevant here | §B |
| 6 | Create Fly's own JEI category (`PressingCategory`) hard-casts every recipe it renders to the concrete `PressingRecipe` record and throws `ClassCastException` on a differently-classed recipe; no EMI integration exists anywhere in Create Fly to piggyback on instead | §C, §E |
| 7 | `minecraft:coals` (vanilla tag) = `{minecraft:coal, minecraft:charcoal}` exactly; `c:storage_blocks/coal` (Fabric convention tag, shipped by Create Fly) = `{minecraft:coal_block}` exactly | §A, §D |
| 8 | No `charcoal_block` exists anywhere in either the Create Fly jar or the merged 26.2 Minecraft jar (assets, blockstates, items, recipes, or classes) | §D |
| 9 | `bulkPressing` (`AllConfigs.server().recipes.bulkPressing`) defaults to `false`: one press consumes exactly one input item and rolls exactly once per 240-tick (12-second) cycle, regardless of how large the queued stack is | §B |
| 10 | Pure data is not sufficient for the mutual-exclusivity requirement: that guarantee has no JSON expression anywhere in `ProcessingOutput`/`CreateSingleStackRollableRecipe`'s shipped roll logic | §E |
| 11 | Balance estimate (explicitly labeled assumptions, not measurements): a single press yields ≈1.5 diamonds/hour at 0.5%, against an assumed ≈3–10 diamonds/hour early-game hand-mining ballpark; press throughput has no shared cap across multiple presses, so horizontal scaling (many presses) can exceed hand-mining rates — accepted by Kevin as designed (`decisions/DEC-010-scaling-accepted.md`), not a finding this sheet treats as a defect | §F |

## Divergences from heimathafen standards

| Standard | Divergence | Recorded in |
|---|---|---|
| `standards/legal/default-license-apache-2-cla.md` | MIT, no CLA | `decisions/DEC-003-licence.md` |
| "no remote unless justified later" | Public on Forgejo under `cubealgos` from the bootstrap, mirrored to GitHub with the issue tracker there, as all three siblings ended up | `decisions/DEC-003-licence.md` |
| The three siblings' own "Create Fly: `<Name>`" Modrinth display-name pattern | This mod's listing is titled **"Create: Synthetic Diamonds"** instead — Kevin's own choice for this one, not an error to correct and not a naming-theme deviation needing further defense; see `decisions/DEC-002-name.md` | `decisions/DEC-002-name.md` |

## Decisions

| ID | Decision | State |
|---|---|---|
| `DEC-001` | Distributed product, full spec sheet | written |
| `DEC-002` | `create_synthetic_diamonds`, mod id `synthetic_diamonds`, display "Create: Synthetic Diamonds" | written |
| `DEC-003` | MIT, no CLA; public under the cubealgos organisation from the first commit | written |
| `DEC-004` | Toolchain as the siblings; one Gradle project; **zero mixin targets** | written |
| `DEC-005` | Kevin's 0.5% diamond / 95% flint / 4.5% gunpowder, mutually exclusive | written |
| `DEC-006` | Success product is the plain vanilla diamond; no item of this mod's own | written |
| `DEC-007` | Charcoal, coal, and coal block all get pressing recipes; no charcoal block exists; the block's failure by-product scales ×9, its diamond odds do not (Kevin, 2026-09-20) | written |
| `DEC-008` | The exclusive roll is a custom `Recipe`/`RecipeSerializer` pair under `AllRecipeTypes.PRESSING`, zero mixin; proposed, to confirm at the first ticket | written |
| `DEC-009` | No JEI category at 1.0.0; a self-contained one is a 1.1 ticket (Kevin, 2026-09-20) | written |
| `DEC-010` | Horizontal press scaling is accepted, no cap and no brake (Kevin, 2026-09-20) | written |

## Open questions gathered

All three open questions the first draft carried forward were ruled on by Kevin the same day,
2026-09-20, after reading that draft (`rulings-2026-09-20.md` "Rulings on the sheet's open
questions"):

- **The coal-block roll shape** — resolved: one roll per block press at the plain single-item 0.5%
  diamond chance, with the failure by-product (flint or gunpowder) scaled ×9 instead
  (`decisions/DEC-007-inputs-and-block-variants.md`, `domains/recipe.md` `RECIPE-REQ-003`).
- **JEI/no-JEI at 1.0** — resolved: no JEI category at 1.0.0, a 1.1 ticket
  (`decisions/DEC-009-jei-deferred.md`, `domains/ui.md` `UI-DEC-001`).
- **Horizontal press-scaling balance** — resolved: accepted as designed, no shared cap and no
  brake across multiple presses (`decisions/DEC-010-scaling-accepted.md`, `04-architecture.md`
  `ARCH-FAIL-005`).

What remains open is technical and belongs to the first ticket: the exact JSON field names and
codec shape for the weight and count fields, and whether the zero-mixin custom-recipe-class
approach (`DEC-008`) actually works against a running server as the research's bytecode
disassembly predicts.
