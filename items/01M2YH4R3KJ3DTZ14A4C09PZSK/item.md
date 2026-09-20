---
schema_version: 1
id: 01M2YH4R3KJ3DTZ14A4C09PZSK
key: SD-1
type: chore
title: "Bootstrap: Gradle with Loom and Create Fly, entrypoints, licence, notice, routing, tools, spec copy, smoke game test"
created_by: kevin
created_at: 2026-09-20T04:28:13Z
---

## Scope

The repository as `docs/spec/04-architecture.md` `ARCH-DEC-001`/`ARCH-DEC-002` and
`docs/spec/decisions/DEC-004-toolchain.md` describe it: one Gradle project on Loom 1.17.21 with
Create Fly `26.2-rc-2-6.0.9-1` pinned and Fabric API `0.160.0+26.2`, Java 25, Gradle 9.5.1 wrapper,
Kotlin DSL and version catalog copied from `create_villager_customers`'s verified `VC-1` scaffold:
`build.gradle.kts`, `settings.gradle.kts`, `gradle/`, `gradlew`, `gradle.properties`, `justfile`,
`tools/` (`doctor.py`, `map.py`, `release_notes.py` and their tests; no `icon.py` yet, SD-6's
territory), `.ci/install-tools.sh`, `.woodpecker.yml`, `.gitea/default_merge_message/*`, `CLAUDE.md`
routing adapted to this repo's three domains (recipe, roll, ui), `.gitignore`. MIT `LICENSE`
(`decisions/DEC-003-licence.md`), `NOTICE` crediting Create Fly (CC0), Create (MIT) and Fabric
(Apache-2.0), `README.md`, `SUPPORT.md`, `CHANGELOG.md` skeleton, `docs/spec/` as a byte-identical
copy of the vault spec via `just spec-sync`, and `docs/modrinth/body.md` as a placeholder (project
settings table filled, the body itself left for SD-6). Main and client entrypoints for mod id
`synthetic_diamonds` (package `synthetic_diamonds`, classes `SyntheticDiamonds` and
`client.SyntheticDiamondsClient`); an empty `synthetic_diamonds.mixins.json` — `DEC-004` calls for
**zero mixin targets**, so unlike every sibling this file never gains an entry; `verifyPurePackage`
gating the `synthetic_diamonds.model` package (the pure roll lands there at SD-2); a smoke game test
proving the mod loads beside Create Fly (`SmokeGameTest`); `SourceSurfaceTest` asserting no
networking type is referenced (`COMP-REQ-001`). `fabric.mod.json`'s `contact` block: source
`https://git.cubealgos.de/cubealgos/create_synthetic_diamonds`, issues
`https://github.com/cubealgos/create_synthetic_diamonds/issues`, Modrinth slug
`synthetic-diamonds`, display name "Create: Synthetic Diamonds" (`decisions/DEC-002-name.md`).

## Approach

Copy the verified toolchain and repository shape from `create_villager_customers`'s `VC-1` bootstrap
commit (its exact file list, not its current HEAD, since later villager tickets added
mod-specific content not applicable here) into one Gradle project for `create_synthetic_diamonds`;
rename every `villager_customers`/`VillagerCustomers`/`Villager Customers`/`VC` identifier to
`synthetic_diamonds`/`SyntheticDiamonds`/`Synthetic Diamonds`/`SD`, then rewrite the prose in
`README.md`, `CLAUDE.md`, `NOTICE`, `SUPPORT.md`, `fabric.mod.json`, the `model` package-info and
the entrypoint's log line for this mod's own subject matter (villager shops vs. a mechanical press),
since a blind identifier rename leaves the wrong domain content behind. Point `just spec-sync`'s
vault path at `../heimathafen/vault/projects/create_synthetic_diamonds/spec`. Diverge from `VC-1` in
two ways this ticket's own brief calls for: a `docs/modrinth/body.md` placeholder ships now instead
of at the icon ticket (SD-6 fills the body itself), and the mixin config stays permanently empty per
`DEC-004` rather than gaining mixins in later tickets.

## Acceptance criteria

- [x] `just check` is green: lint (`verifyPurePackage` + `validateAccessWidener`), `map-check`,
      `test-java`, `test-tools` (4 tests), `gametest` (2/2 game tests, including `SmokeGameTest`
      asserting both `create` and `synthetic_diamonds` are loaded).
- [x] `just doctor-toolchain` is clean: Java 25, Gradle wrapper 9.5.1, `just` present, Python 3,
      `kontor` present, map current, `docs/spec/` identical to the vault.
- [x] `fabric.mod.json`'s `contact` block links the Forgejo repo, the GitHub issues tracker, and the
      Modrinth slug `synthetic-diamonds`; `name` is "Create: Synthetic Diamonds".
- [x] `synthetic_diamonds.mixins.json` exists with an empty `mixins` array and no `client` entries.
- [x] `docs/modrinth/body.md` exists with the project-settings table filled and the body itself
      marked as SD-6's territory.
- [x] Forgejo repo `cubealgos/create_synthetic_diamonds` created (public), `development` pushed and
      set as default branch, branch protection on `development`/`production`, merge message
      templates present, `gitkontor/data` pushed.

## Constraints and prior findings

`docs/spec/contracts/platform-matrix.md`, `docs/spec/decisions/DEC-004-toolchain.md`,
`docs/spec/decisions/DEC-003-licence.md`, `docs/spec/operations/compliance.md`. Create Fly
coordinate, mod id `create`, declared version `6.0.9-1`, CC0, and the 26.2 toolchain floors are
inherited unverified-again from the three siblings' own bootstraps (`create_metered_motor`'s `MM-1`,
`create_brass_compass`'s `BC-1`, `create_villager_customers`'s `VC-1`), which is the direct model for
this ticket's shape and its `just doctor`/`just check` acceptance bar.

Findings from doing this bootstrap: `kontor init` parks the checkout on `chore/bootstrap`, not
`development` (`vault/technical/gitkontor/kontor-init-parks-the-checkout-on-chore-bootstrap.md`);
`git checkout development` immediately after `init` avoided it here. `kontor ticket new` lands a
ticket in `backlog` regardless of its milestone's own status
(`vault/technical/gitkontor/ticket-new-lands-in-backlog-claim-needs-todo.md`); every ticket here but
SD-8 was moved to `todo` explicitly before claiming. Copying files with a shell loop over
`git show --name-only <sha>` must strip the commit-message lines first — the raw output pollutes
the file list with the message body as bogus paths, and `cmd > "$badpath"` creates an empty file
at that path even when `cmd` itself fails, silently littering the target tree; caught and cleaned up
during this ticket, worth a vault note. `kontor init`'s two root commits (`chore/bootstrap` and
`gitkontor/data`) used the environment's global git identity (`kevinscheeren@icloud.com`) rather
than this repo's own (`scheeren@cubealgos.de`); both were un-pushed single-commit histories, so
amended with `--reset-author` after setting local `git config user.name`/`user.email`, rather than
left wrong or worked around with `-c user.email=` on every later commit.
