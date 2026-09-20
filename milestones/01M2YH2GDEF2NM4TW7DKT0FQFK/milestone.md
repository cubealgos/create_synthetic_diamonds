---
schema_version: 1
id: 01M2YH2GDEF2NM4TW7DKT0FQFK
key: M2
title: Tests and listing
status: todo
created_at: 2026-09-20T04:26:59Z
---

## Goal

Prove the mechanism is solid and make it a real Modrinth listing: every requirement mapped to a
test, three clean `just check` runs, a client checklist done by Kevin, and the assets and release
that turn a working mod into a published one.

## Scope

The requirement-to-test table, the three-run bar and the client checklist (`SD-5`); the Modrinth
icon, body and gallery shot list (`SD-6`); the `1.0.0+26.2` release build and its publication
(`SD-7`). Not the JEI category (`M3`).

## Exit criteria

- Every `RECIPE-REQ`, `ROLL-REQ` and `UI-REQ` id is mapped to a test or a recorded ruling.
- `just check` green three consecutive times on a clean checkout.
- Kevin's client checklist done.
- Published on Modrinth with real assets, and a tagged `1.0.0+26.2` release with its notes and
  checksum.

## Tickets

SD-5, SD-6, SD-7.

## Depends on

M1 (the recipes, the roll, and the debug tool this milestone tests and documents).
