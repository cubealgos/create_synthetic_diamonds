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

## Findings

**Repo.** `https://git.cubealgos.de/cubealgos/create_synthetic_diamonds`, id `18`, public, wiki/
projects/packages/actions off, issues on. Default branch `development` (auto-set by Forgejo on the
first push, no separate PATCH needed for that field). Branch protection (`enable_push: false`) on
both `development` and `production`. Merge message templates copied in at bootstrap, so unlike the
three siblings' own first merge this repo's very first PR (#1) already had `.gitea/default_merge_message/*`
on `development` before it merged — no need for the explicit `merge_title_field`/`merge_message_field`
workaround the vault note describes for a repo's first-ever merge; used them anyway for full
control over the Conventional Commits subject. PR #1 merged clean on the first attempt (`merged:
true`, `origin/development`'s tip confirmed by both the API and `git ls-remote` after a few
seconds), unlike the "merge recorded but never lands" failure the vault note documents happening on
two of five merges elsewhere the same week — worth noting as a data point that it is not universal,
not that the failure mode is fixed.

**gitkontor project id** `01M2YGMQZT28QFB5PQ3V837NXT`; milestone ids: M0
`01M2YGMQZVDDZJ54K9FGDFTCB4`, M1 `01M2YH2GBP4YGPATW8GE3XZBZE`, M2 `01M2YH2GDEF2NM4TW7DKT0FQFK`, M3
`01M2YH2GF66S4RF7AQWYFMK1R0`.

**Differences from the `VC-1` bootstrap**, beyond the domain-content rewrite the Approach section
already describes: (1) `docs/modrinth/body.md` ships now, as a placeholder, per this ticket's own
brief — `VC-1` shipped no `docs/modrinth/` at all, adding it only at `VC-7`. (2)
`synthetic_diamonds.mixins.json` is expected to stay empty forever (`DEC-004`'s zero-mixin-targets
ruling), where every sibling's own empty mixin config was a placeholder for mixins its next couple
of tickets would add. (3) `kontor claim`/`kontor finish` here are the bare CLI subcommands, not a
`just claim`/`just unblock-sweep` recipe wrapper — this repo's `justfile` (copied from `VC-1`, which
itself has none) defines no such recipes, so the claim-ritual and finish-verification steps
`docs/spec` cites as `just claim`/`just finish` were run as raw `kontor claim SD-1` / `kontor ticket
status SD-1 review` / `kontor finish SD-1` instead, with an extra manual `git add -A && git commit`
in `.gitkontor/` after `claim` since the bare subcommand edits `state.toml` but does not commit it
itself (unlike the wrapper recipe the workflow doc describes). `kontor finish` additionally
required a live local branch matching the ticket to verify containment against — recreated
`chore/sd-1-bootstrap` locally (pointing at the pre-merge commit) after having deleted it
post-push, then deleted it again once `finish` had read it.

**Ticket-authoring divergence from the base-layer workflow doc's §4.10 "epics as parents
everywhere" rule**: no `epic`-typed items were created for M0–M3, matching the three siblings'
actual practice (`create_villager_customers` has none either) rather than the documented rule — a
known policy-vs-practice gap this project inherits rather than re-litigates.
