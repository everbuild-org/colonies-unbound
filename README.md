# Colonies Unbound

A NeoForge 1.21.1 add-on for registering survival-built homes and workplaces with MineColonies.

The project replaces schematic-only building metadata with survival-friendly building inspection and explicit in-world semantic markers. The first vertical slice is a residence that can discover beds, validate a player-built volume, and participate in MineColonies' native housing system.

## Compatibility baseline

- Minecraft 1.21.1
- NeoForge 21.1.248
- MineColonies 1.1.1374-1.21.1-snapshot
- Java 21

MineColonies integration will intentionally be version-pinned because some required extension points are internal implementation classes rather than stable public APIs.

## Planned milestones

1. Marker domain model and server-side persistence
2. Point, area, and path editing
3. Client overlays for colored pins, bounds, and routes
4. Survival Residence integration
5. Reusable inspection rules and workplace adapters

## Current prototype

The Worksite Marker provides the first area-selection vertical slice:

- Obtain it from the Tools & Utilities creative tab (or `/give @s coloniesunbound:worksite_marker`).
- Place one Survival Residence Plaque inside the candidate house from the Functional Blocks tab.
- Use it on two blocks to select the inclusive corners of a volume.
- Hold it to see a live cyan preview and the finalized gold outline in-world.
- Completing an area inspects loaded blocks and reports the number of bed heads found.
- Valid residences configure a dedicated MineColonies building whose capacity is its registered bed count.
- Residence bounds, beds, ownership, and inspection time are also stored per dimension.
- Saving requires MineColonies' `MANAGE_HUTS` permission and an area wholly inside one colony.
- Sneak-use it on a block to clear the current selection.

Selection data is mutated by the server and synchronized as item data. The current item-local
representation is intentionally small; world-level marker collections and MineColonies ownership
links will be introduced with the Residence integration.

## Development

```sh
./gradlew build
./gradlew runClient
```

`build` also runs the focused bounds and Residence inspection unit tests.
