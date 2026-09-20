---
schema_version: 1
id: 01M2YXVN163G5EE6N2V3NCQYG3
key: SD-11
type: bug
title: An out-of-range recipe weight is not clamped as RECIPE-FAIL-003 requires
created_by: kevin
created_at: 2026-09-20T08:10:26Z
---

## Scope

`RECIPE-FAIL-003` says an out-of-range chance in a recipe file (below 0 or above 1) is clamped to `[0, 1]` and logged once; the serializer and `WeightedPick.of` only normalise the sum and never clamp an individual weight (found by SD-5's sweep). Implement the clamp in the pure model (`WeightedPick.of`: clamp each weight to `[0, 1]` before normalising, one message per clamped weight through the existing consumer) and prove it.

## Approach

Pure change in `synthetic_diamonds.model.WeightedPick` plus unit tests (negative weight → 0 and logged; weight 1.5 → 1 and logged; then the normal normalisation); a game test with a fixture recipe carrying a weight of 2.0 that loads and rolls sanely.

## Acceptance criteria

- [ ] Unit tests for the clamp on each weight, including the log-once behaviour.
- [ ] Game test: a fixture recipe with an out-of-range weight loads and every roll yields exactly one outcome.
- [ ] Merged through a Forgejo pull request into `development`.

## Constraints and prior findings

SD-2's `WeightedPick`; SD-5's requirement table row for `RECIPE-FAIL-003`.
