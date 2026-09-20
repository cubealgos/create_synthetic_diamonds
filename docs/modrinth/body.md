# Modrinth listing (paste-ready)

## Project settings

| Field | Value |
|---|---|
| Name | Create: Synthetic Diamonds |
| Slug | `synthetic-diamonds` |
| Summary | A Create mechanical press turns charcoal, coal or a coal block into a small, mutually exclusive chance of a diamond, or otherwise flint or gunpowder. |
| Categories | Equipment, Technology, Utility (secondary: Adventure, Game-Mechanics, Management, Optimization) |
| Licence | MIT |
| Client side | Unsupported |
| Server side | Required |
| Loaders | Fabric |
| Game versions | 26.2 |
| Dependencies | Create Fly (required), Fabric API (required) |
| Icon | `icon.png` in this folder: the vanilla diamond item sprite on the cubealgos navy badge (`just icon` regenerates it, SD-6) |
| Links | Source `https://github.com/cubealgos/create_synthetic_diamonds` · Issues `https://github.com/cubealgos/create_synthetic_diamonds/issues` · Origin `https://git.cubealgos.de/cubealgos/create_synthetic_diamonds` |

## Version settings

| Field | Value |
|---|---|
| Version number | `1.0.0+26.2` |
| Version title | Synthetic Diamonds 1.0.0 for Minecraft 26.2 |
| Channel | Release |
| File | `dist/create_synthetic_diamonds-1.0.0+26.2.jar` |
| Changelog | paste `dist/release-notes-1.0.0+26.2.md` |

## Body

A Create mechanical press already sits on every serious charcoal or coal line. Synthetic Diamonds
gives it a low, honest chance at a vanilla diamond: feed it charcoal, coal, or a coal block, and
each press cycle rolls exactly one outcome — a diamond, or otherwise flint or gunpowder — never
zero, never two at once. No new item, block, or screen: the only product is the plain vanilla
`minecraft:diamond`, indistinguishable from one you mined.

### What it does

- **Press charcoal or coal.** Each item pressed rolls once: 0.5% diamond, 95% flint, 4.5%
  gunpowder. Exactly one of the three comes out, every cycle, roughly every 12 seconds
  (240 ticks) per item.
- **Press a coal block.** Same 0.5% diamond chance as a single item — a block is not a better bet
  per charcoal, it's a batch. If the roll misses, the by-product scales with the block: 9 flint or
  9 gunpowder instead of 1. One roll, one outcome, the failure count ×9.
- **Nothing else changes.** No other Create Fly recipe type is touched — crushing, milling,
  cutting, mixing all keep their own vanilla behaviour. No new use for the diamond itself.

### Setting it up

Run the press exactly as you would for any other pressing recipe: charcoal, coal, or coal blocks
feed in over a depot or on a belt, the press cycles them, and diamonds, flint, and gunpowder come
out the other side mixed together. Sort the output with a filtered belt or a brass funnel into
separate chests so the rare diamonds don't get lost in the flint pile — a splitter with a filter on
`minecraft:diamond` does the job.

### What it's for

A slow, automatable alternative to hand-mining diamonds, deliberately worse odds than digging so
it stays behind mining rather than replacing it early game. Build one press and check back
occasionally, or build ten and let horizontal scale do the work — each press rolls independently,
with no shared cap and no diminishing returns.

### Retuning via datapack

The odds and ingredients are plain JSON, not hardcoded: `data/synthetic_diamonds/recipe/
weighted_pressing/` ships three files (`charcoal.json`, `coal.json`, `coal_block.json`), each with
its own diamond/flint/gunpowder chances and result counts. A datapack can override any of them,
add more, or change the odds entirely — the recipe files are also the only place in-game to read
the exact numbers, since this mod adds no menu, tooltip, or recipe-viewer entry to show them (no
JEI category at 1.0).

### Made for Create

No new screen, no new block, and no new item of its own: the mechanical press is the entire
interface, exactly as before this mod is installed.

### Privacy

Nothing leaves your machine. No telemetry, no update checks, no network calls of its own — the
whole mechanism is a server-side recipe roll.

### Requirements

Minecraft 26.2, Fabric, Fabric API, and Create Fly 6.0.9-1 (the build this version was tested
with; the mod declares exactly that version).

### Support

Through the issue tracker only (https://github.com/cubealgos/create_synthetic_diamonds/issues),
as time allows. Source on GitHub, mirrored from the cubealgos Forgejo. Include your Minecraft,
Fabric and Create Fly versions, the mod version from the jar name, and the steps that show the
problem. MIT licensed.
