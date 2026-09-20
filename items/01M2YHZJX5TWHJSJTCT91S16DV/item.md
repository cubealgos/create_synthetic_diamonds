---
schema_version: 1
id: 01M2YHZJX5TWHJSJTCT91S16DV
key: SD-9
type: chore
title: "CI: the copied install script lost its executable bit"
created_by: kevin
created_at: 2026-09-20T04:42:52Z
---

## Scope

Woodpecker's first runs on this repo fail at `./.ci/install-tools.sh: Permission denied`: the bootstrap copy lost the script's executable bit (the siblings track it as mode 100755). Restore the mode so `just check` runs on the runner.

## Approach

`git update-index --chmod=+x .ci/install-tools.sh`, one commit, PR, and the next pipeline on `development` must go green.

## Acceptance criteria

- [x] `.ci/install-tools.sh` is tracked with mode 100755.
- [x] The Woodpecker pipeline for the merge commit on `development` succeeds.

## Constraints and prior findings

SD-1's Findings on the file-copy script; the compass's BC-17 fixed the same installer before.
