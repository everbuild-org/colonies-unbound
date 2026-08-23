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

## Development

```sh
./gradlew build
./gradlew runClient
```
