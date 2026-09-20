---
title: "create_synthetic_diamonds spec — compliance, security and governance"
type: "spec"
category: "create_synthetic_diamonds"
---

# Compliance, security and governance (`COMP`, sheet §6)

A distributed product carries the same obligations as its siblings, landing in different places.

| Area | Position |
|---|---|
| GDPR: what leaves the user's machine | Nothing. No telemetry, no update check, no outbound network call of any kind (`COMP-REQ-001`). No player data of any kind is touched: this mod persists nothing (`contracts/data-contract.md`). |
| Hosted parts we run | None. Modrinth hosts the file and its page. |
| Impressumspflicht | Attaches to a public web presence; there is none beyond the platform pages. Revisit if a site exists. |
| Licence and notices | MIT (`decisions/DEC-003-licence.md`); `NOTICE` credits Create Fly (CC0), Create (MIT), Fabric (Apache-2.0). No Minecraft or Create Fly textures, models, or code are copied — the mod adds one recipe class and JSON data only. |
| Supply chain and release integrity | Builds from a tagged commit with pinned dependencies; the release checksum is in the release notes; no signing at 1.0. |
| Vulnerability disclosure | The public issue tracker only, on the GitHub mirror (`https://github.com/cubealgos/create_synthetic_diamonds/issues`); no private channel, no e-mail address published. Forgejo stays the source of truth for code. |
| Server trust boundary | Every press cycle runs entirely server-side; there is no menu, no packet, and no client input anywhere in this mod's mechanism — the simplest trust boundary of any sibling so far, since there is no state and no client-facing surface for a client to influence at all. |
| AI Act, GoBD, sector regulation | Not applicable: no AI component, no financial records, no regulated sector. |

`COMP-REQ-001`: the mod shall make no network call of its own; a source-scan test asserts it
(`SourceSurfaceTest`, as the three siblings use).

Open: release signing (minisign) before 1.0 or after.
