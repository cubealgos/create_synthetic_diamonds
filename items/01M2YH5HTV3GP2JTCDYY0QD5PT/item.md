---
schema_version: 1
id: 01M2YH5HTV3GP2JTCDYY0QD5PT
key: SD-6
type: docs
title: "Modrinth assets: navy badge icon from the vanilla diamond sprite, body, gallery shot list"
created_by: kevin
created_at: 2026-09-20T04:28:39Z
---

## Scope

`tools/icon.py`, rendering `docs/modrinth/icon.png`: this mod adds no block or item of its own
(`UI-REQ-001`), so the icon's subject is the vanilla diamond item sprite
(`assets/minecraft/textures/item/diamond.png`), read straight out of the merged Minecraft client
jar in the Gradle cache — never vendored into this repo — on the cubealgos navy badge the three
siblings already share. `docs/modrinth/body.md`'s `## Body` section, replacing SD-1's placeholder,
covering what the mod does (the three recipes, the exclusive roll, the deliberately slow odds),
made for Create, privacy (nothing leaves the player's machine), requirements, and support — the
same sections the three siblings' own bodies carry. A gallery shot list (`docs/modrinth/gallery.md`):
what screenshots to take and of what, mirroring `create_villager_customers`' `VC-7` list, adapted
to this mod having no screen of its own to show — press placement, a diamond popping out of the
press, the recipe JSON as a readable-odds shot.

## Approach

Adapt `create_villager_customers`'s `tools/icon.py` (`VC-7`): swap the sprite entry to
`assets/minecraft/textures/item/diamond.png`, keep the navy-badge rendering (rim, band, ring,
blueprint grid, glow, outline, shadow) byte-for-byte, since the badge itself is the cubealgos
Create-add-on identity mark, not per-mod. Write `body.md`'s prose from `00-context.md` and the
`recipe`/`roll` domain files, matching the siblings' own body structure and tone (see
`create_villager_customers/docs/modrinth/body.md` for the shape: What it does, Made for Create,
Privacy, Requirements, Support). Gallery shot list: since this mod has no screen, favor shots that
make the mechanism legible — a press mid-cycle, the moment a diamond outputs, a chest of flint/
gunpowder next to a single diamond to sell the rarity, the recipe JSON open in an editor.

## Acceptance criteria

- [ ] `just icon` renders `docs/modrinth/icon.png` from the vanilla diamond sprite on the navy
      badge, matching the siblings' visual style.
- [ ] `docs/modrinth/body.md`'s `## Body` section is filled with real prose (no longer "to be
      written at SD-6"); the project-settings table from SD-1 is carried forward unchanged unless
      a real detail changed.
- [ ] `docs/modrinth/gallery.md` lists concrete shots to take, each with what it should show and
      why.
- [ ] `just check` still green (icon rendering is not part of `check`, but nothing it touches
      breaks the build).

## Constraints and prior findings

Blocked by SD-1 only (the placeholder `body.md` and project-settings table it created). Not
blocked by SD-2/SD-3/SD-4: the icon and gallery list don't depend on the recipes actually shipping,
though the body's prose is more accurate once they have (`RECIPE-REQ-001`–`003` describe the exact
odds this section states). `docs/spec/00-context.md`, `domains/recipe.md`, `domains/roll.md`.
Modeled on `create_villager_customers`'s `VC-7`.
