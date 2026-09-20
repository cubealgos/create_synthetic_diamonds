---
schema_version: 1
id: 01M2YX348T4JWDR1G8702VD8WJ
key: SD-10
type: chore
title: doctor.py reads the villager repo's VC_VAULT_SPEC variable instead of SD_VAULT_SPEC
created_by: kevin
created_at: 2026-09-20T07:57:03Z
---

## Scope

`tools/doctor.py` still reads the environment variable `VC_VAULT_SPEC` (a leftover from the `create_villager_customers` copy) while `justfile` defines `SD_VAULT_SPEC`; the doctor's spec-copy check therefore ignores the repo's own override. Found by the `create_firearms` bootstrap (FA-1 Findings). Rename to `SD_VAULT_SPEC` in the script and its message.

## Approach

One-line edit in `tools/doctor.py` (two occurrences), `just doctor` still green.

## Acceptance criteria

- [ ] `tools/doctor.py` reads `SD_VAULT_SPEC`; no `VC_` reference remains in the repo (`grep -r VC_ tools justfile` empty).
- [ ] Merged through a Forgejo pull request into `development`.

## Constraints and prior findings

SD-1's bootstrap copy script; FA-1's Findings in `create_firearms`.
