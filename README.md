# Create: Synthetic Diamonds

A Create mechanical press turns charcoal, coal, or a block of coal into a small, mutually
exclusive chance of a vanilla diamond — otherwise the press destroys the input into flint or
gunpowder. Every press cycle yields exactly one of the three outcomes, never zero and never two at
once: 0.5% diamond, 95% flint, 4.5% gunpowder for a single item; a block press scales only the
by-product count (×9), not the diamond odds. Deliberately slow — about 200 charcoal per diamond —
so this is a bit harder to automate than mining for diamonds early game.

Requires Minecraft 26.2, Fabric Loader, Fabric API and Create Fly. MIT (LICENSE); credits in NOTICE.
Releases carry the jar and its SHA-256 in the notes; see CHANGELOG.md for what each version holds.

Support and security reports go through the issue tracker only (SUPPORT.md): https://github.com/cubealgos/create_synthetic_diamonds/issues.

Source: https://git.cubealgos.de/cubealgos/create_synthetic_diamonds (Forgejo, the home of this repository). Mirror: https://github.com/cubealgos/create_synthetic_diamonds, read-only code, and the issue tracker.
Releases: https://modrinth.com/mod/synthetic-diamonds.

Development: `just --list`. The specification is `docs/spec/`.
