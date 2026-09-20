---
schema_version: 1
id: 01M2YH5E8J14DJMP6TFC43SWPG
key: SD-5
type: test
title: Requirement-to-test table, three check runs, client checklist
created_by: kevin
created_at: 2026-09-20T04:28:35Z
---

## Scope

The requirement-to-test table `operations/testing.md` `TEST-REQ-001` requires: every `RECIPE-REQ`,
`ROLL-REQ` and `UI-REQ` mapped to the test that proves it (or, where a requirement genuinely cannot
be proven headless, a ruling recording why and pointing at the client checklist instead), plus the
two once-only proofs `TEST-REQ-002` (the pure-package deliberate-break proof, satisfied at SD-1) and
`TEST-REQ-003` (the recipe-bucket coexistence game test, satisfied at SD-3), both restated here with
their ticket references rather than re-proven. Three green `just check` runs in a row on a clean
checkout, recorded with their game-test counts. Kevin's client checklist
(`operations/testing.md`, "Manual / release checklist" row): watching a real press cycle run
against charcoal, coal and a coal block over enough real time (12-second cycles) to see all three
outcomes appear for each, confirming the observed ratio is in the right ballpark for the configured
weights, and confirming no recipe-viewer entry appears anywhere for these recipes at 1.0
(`UI-FAIL-001`) — timing and player-visible ratio are not meaningfully provable headless in the
time this project spends on any one ticket, so the checklist is load-bearing here, not decorative.

## Approach

One table in this ticket, filled from the tests written under SD-2 through SD-4; where a
requirement cannot be proven headless, the table names the reason and points at the client
checklist instead, exactly as `operations/testing.md` scopes it. `just check` run three times in a
row with no flaky failure, on a clean checkout, each run's game-test count recorded here. The debug
command from SD-4 (`/synthetic_diamonds debug press <count>`) is the practical tool for eyeballing
the odds quickly before committing to the slower real-time checklist item.

## Acceptance criteria

- [ ] A requirement-to-test table in this ticket covers every `RECIPE-REQ`, `ROLL-REQ` and
      `UI-REQ` id.
- [ ] `just check` green three consecutive runs, recorded with their game-test counts, on a clean
      checkout.
- [ ] `TEST-REQ-002` (satisfied at SD-1) and `TEST-REQ-003` (satisfied at SD-3) restated in the
      table with their ticket references.
- [ ] Kevin's client checklist done and recorded: real press cycles against charcoal, coal and a
      coal block, all three outcomes observed for each, ratio sanity-checked, no recipe-viewer
      entry found anywhere.

## Constraints and prior findings

`operations/testing.md` (`TEST-REQ-001`–`003`), `domains/ui.md` `UI-FAIL-001`/`UI-FAIL-002`.
Blocked by SD-3 (the recipe data and their game tests must exist to table them) and SD-4 (the debug
command is part of the checklist's practical tooling). Blocks SD-7 (release requires this ticket's
green three-run bar and checklist). Modeled directly on `create_villager_customers`'s `VC-6`
(`requirement-to-test table, three green just check runs, client checklist`), adapted to this mod's
three domains (`RECIPE`, `ROLL`, `UI`) in place of the sibling's (`CUSTOMER`, `TRANSACTION`,
`SHOP`).
