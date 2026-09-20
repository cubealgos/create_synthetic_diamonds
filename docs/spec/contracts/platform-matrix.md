---
title: "create_synthetic_diamonds spec — platform matrix"
type: "spec"
category: "create_synthetic_diamonds"
---

# Platform matrix (`PLATFORM`)

| Row | Value | How it is checked |
|---|---|---|
| Minecraft | 26.2 (`~26.2` in `fabric.mod.json`) | `just doctor`, game tests on a dedicated server |
| Fabric Loader | ≥ 0.19.5 | `fabric.mod.json` |
| Fabric API | ≥ 0.160.0 (development tooling only — no Fabric event carries this mod's mechanism; the recipe lookup uses vanilla registry mechanics, `04-architecture.md` `ARCH-DEC-001`) | `fabric.mod.json` |
| Create Fly | `26.2-rc-2-6.0.9-1`, mod id `create`, declared version `6.0.9-1`, `implementation` coordinate `maven.modrinth:create-fly`, pinned | `fabric.mod.json` depends `create`; `NOTICE` |
| Java | 25 | `just doctor` |
| Gradle / Loom | 9.5.1 wrapper / 1.17 | wrapper properties |
| Operating systems | macOS, Linux, Windows: the JVM's | not tested separately; nothing native |
| Client and server | Recipes apply server-side, exactly as for every vanilla or Create Fly processing recipe; the recipe JSON is also loaded client-side for the same data-driven bookkeeping vanilla recipes always get (recipe existence, ingredient display where applicable) — no client-side behaviour of this mod's own exists | game tests (server), `just client` (client boot only) |
| Mixin targets | **None.** The custom recipe class is found through ordinary `Recipe.getType()`/`RecipeType` registry mechanics, not a hook into any private vanilla or Create Fly method (research §C). A genuine, notable divergence from every sibling so far — `create_villager_customers` needed two, `create_brass_compass` and `create_metered_motor` needed none for their own reasons; this is the first mod in the family to add net-new server-side recipe behaviour with zero mixin. | mixin config (absent), `just check` |

`PLATFORM-REQ-001`: **If** any row moves, **then** `just doctor` fails naming the row.
`PLATFORM-REQ-002`: **If** a Create Fly release renames or removes `AllRecipeTypes.PRESSING` or
changes `MechanicalPressBlockEntity.getRecipe()`'s lookup mechanism, **then** the build fails at
compile time (the `AllRecipeTypes.PRESSING` reference no longer resolves) rather than silently
producing recipes the press never finds.
