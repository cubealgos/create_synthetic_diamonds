---
schema_version: 1
id: 01M2YH5SA0JVW93XCPTH7JR52T
key: SD-7
type: chore
title: Release 1.0.0+26.2
created_by: kevin
created_at: 2026-09-20T04:28:47Z
---

## Scope

The `1.0.0+26.2` release build and its distribution: `just release` from a clean checkout at tag
`v1.0.0+26.2` (`REL-REQ-001`), `dist/` holding the jar, its SHA-256, and generated release notes
naming the Minecraft, Fabric Loader and Create Fly versions tested (`REL-REQ-002`) and the default
weights in force for all three recipes (`REL-REQ-003`). Publish to Modrinth using the project
settings and body from SD-6 and the version settings SD-6's `body.md` already names. Tag on
`production` per `operations/release.md`'s branch/tag convention. `CHANGELOG.md`'s `[Unreleased]`
section becomes `[1.0.0+26.2]` with real entries for the three recipes, the exclusive roll, and the
debug command.

## Approach

Follow `operations/release.md` exactly as the three siblings' own release tickets did: merge every
prior ticket's branch into `development`, confirm SD-5's three-green-`just-check`-runs bar and
client checklist are both satisfied and recorded, tag `production` at the merge, run `just
release`, verify `dist/`'s jar and checksum, paste the generated notes into the Modrinth version
using SD-6's project settings. Confirm the repo's own CI (`.woodpecker.yml`) has run green on the
tagged commit before publishing, since the repo has been public and CI-enabled from the first push
(`operations/release.md`'s CI row).

## Acceptance criteria

- [ ] `just release` succeeds from a clean checkout at `v1.0.0+26.2`; `dist/` holds the jar, its
      SHA-256, and the release notes.
- [ ] Release notes name the tested Minecraft/Fabric Loader/Create Fly versions and the three
      recipes' default weights.
- [ ] `CHANGELOG.md` has a real `[1.0.0+26.2]` section, not `[Unreleased]`.
- [ ] Published to Modrinth using SD-6's project settings, body and icon.
- [ ] Woodpecker CI green on the tagged commit.

## Constraints and prior findings

`operations/release.md` (`REL-REQ-001`–`003`). Blocked by SD-5 (the three-run/checklist bar this
release gates on) and SD-6 (the Modrinth assets this release publishes). Not blocked by SD-8 (the
JEI category is explicitly a 1.1 scope item, `UI-DEC-001`, `decisions/DEC-009-jei-deferred.md`) —
1.0.0 ships without it by design.
