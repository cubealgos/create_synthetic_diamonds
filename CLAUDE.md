# create_synthetic_diamonds

A Create Fly add-on for Minecraft 26.2 on Fabric: a mechanical press turns charcoal, coal or a
coal block into a small, mutually exclusive chance of a vanilla diamond, otherwise destroying the
input into flint or gunpowder.

**This file routes. It does not hold content.** The specification is `docs/spec/`.

## Read this before you do that

| about to… | read first |
|---|---|
| anything at all | `docs/spec/README.md`, then the one domain file you need |
| find where something lives | `docs/map.md`; generated, never edited |
| touch `synthetic_diamonds.model` | it has no Minecraft imports; the build's `verifyPurePackage` enforces it |
| touch the recipe type or serializer | `docs/spec/domains/recipe.md`, `docs/spec/04-architecture.md` `ARCH-DEC-001`, `ARCH-DEC-002` |
| touch the weighted exclusive roll | `docs/spec/domains/roll.md`, `docs/spec/decisions/DEC-008-exclusive-roll-mechanism.md` |
| touch the debug command or any player-facing surface | `docs/spec/domains/ui.md` |
| add a dependency | `docs/spec/decisions/DEC-003-licence.md` (MIT) and heimathafen's dependency policy |
| commit | scope `synthetic_diamonds`, the ticket key (`SD-N`) in the subject |

## Working here

```
kontor claim SD-N
kontor branch new SD-N <slug>
just check
```

`just --list` shows the task surface; `just spec-sync` refreshes `docs/spec/` from the vault; `just map` regenerates the map.

## Standing rules

- The spec is authoritative; `docs/spec/` is a copy of heimathafen's vault.
- A design question the spec does not answer is asked, never decided inline.
- Nothing leaves the player's machine: no telemetry, no network calls (`docs/spec/operations/compliance.md`).
- Always keep a playable build: `just client` boots with Create Fly at every merge.
