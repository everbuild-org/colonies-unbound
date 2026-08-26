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

- Craft it from a MineColonies Clipboard and a Feather, obtain it from the Tools & Utilities creative tab, or use `/give @s coloniesunbound:worksite_marker`.
- Place one Survival Residence Plaque inside the candidate house from the Functional Blocks tab.
- Use it on two blocks to select the inclusive corners of a volume.
- Hold it to see a live cyan preview, committed residence outlines, and yaw-facing typed flags.
- Completing an area inspects loaded blocks and reports the number of bed heads found.
- Valid residences configure a dedicated MineColonies building whose capacity is its registered bed count.
- Placing or removing beds inside committed, loaded volumes automatically reconciles MineColonies POIs and capacity.
- Discovered beds are synchronized to clients and shown with their own smaller bed flags.
- Residence bounds, beds, ownership, and inspection time are also stored per dimension.
- Saving requires MineColonies' `MANAGE_HUTS` permission and an area wholly inside one colony.
- Sneak-use it on a block to clear the current selection, or on a residence plaque to remove its committed mark.
- Use it in air to cycle Residence Area, Storage, Worksite, Entrance, and Interaction editing modes.
- In a point mode, use inside a committed volume to add a point and sneak-use the same block to remove it.
- Point-mode clicks take priority over block menus; storage, furnace worksite, and entrance targets are validated.
- A minimal workplace draft becomes ready once it has at least one Storage, Worksite, and Entrance point.
- A Survival Dining Hall Plaque turns that draft into a native MineColonies Cook building: readiness
  activates level 1, registers marked furnaces and containers, and exposes work/entrance/sit tags.
- Dining Hall furnaces, smokers, and blast furnaces are scanner-owned and reconcile automatically
  after block changes and chunk loads without disturbing manual storage, entrance, or seating points.
- A Survival Guard Tower Plaque activates MineColonies' native Guard Tower modules. Patrol Route mode
  selects a committed tower, then authors up to 32 ordered, colony-contained native patrol targets.
- A Survival Cow Pen Plaque activates MineColonies' native Cowhand for cattle and goats. The pen
  scanner discovers fence gates and a grass/dirt floor, while the player marks one storage inventory.
- Cow Pen gates and pasture reconcile after block changes and chunk loads. Losing any required part
  deactivates hiring until the pen becomes valid again, without deleting its committed volume.
- A Survival Sheep Pen Plaque uses the same scanned pen requirements but activates MineColonies'
  native Shepherd, including breeding, shearing, dyeing settings, and sheep-specific production.
- Chicken Pen, Pig Pen, and Rabbit Hutch plaques compose the same gate/pasture/storage workflow with
  MineColonies' native Chicken Herder, Swine Herder, and Rabbit Herder modules.
- A Survival Stable adds an authored Stall point to the livestock requirements and exposes it as the
  native `stall` positioned tag used by the Stable Master and cavalry systems.
- A Survival Apiary scans vanilla beehives and bee nests, while storage remains player-authored. Hive
  changes reconcile into MineColonies' native Beekeeper building after block changes and chunk loads.
- Wave 2 crafting plaques cover Blacksmith, Sawmill, Stonemason, Fletcher, Mechanic, Concrete Mixer,
  Crusher, and Sifter. Compatible workstation blocks are scanner-owned; storage is player-authored.
- Crafting station placement/removal reconciles automatically. Concrete Mixer additionally registers
  shallow flowing-water cells into MineColonies' native mixer topology for real placement/harvesting.
- Wave 3 adds Bakery, Kitchen, Smeltery, Stone Smelter, Glassblower, Dyer, and Alchemist plaques.
  Furnaces and brewing stands are scanner-owned and synchronized with their native worker topology.
- Wave 4 adds Farmer, Plantation, Fisherman's Hut, Forester's Hut, Florist, and Composter plaques.
  Natural resource volumes bind native fields, soil, barrels, ponds, and restricted woodland areas.
- Wave 5 adds Hospital, School, Library, University, Tavern, Graveyard, Enchanter, and Nether Worker
  plaques. Their beds, classroom seats, bookshelves, graves, enchanting tables, and live portals are
  scanner-owned; the Enchanter also links all operational worker buildings as native draining targets.

Temporary selection data is synchronized as item data. Committed volumes are owned and synchronized
by their residence plaque, so removing that building anchor also removes the mark.
Repeated reconciliation also removes orphaned marks after their MineColonies building disappears,
while tolerating transient loading gaps and non-destructive validation failures.

The POI reconciliation path is intentionally event-driven and debounced, ready for other accurately
assignable MineColonies POIs as survival workplace adapters are added. Scanner-owned bed points are
refreshed independently, so manually assigned semantic points survive rescans and reloads.

Billboard marker art is layered from `textures/marker/flag_base.png` and a typed icon such as
`textures/marker/icons/residence.png`; transparent 32x32 textures are recommended. The white
`flag_base.png` acts as an alpha mask and is tinted by semantic category at runtime. Both layers
use true alpha blending.

## Development

```sh
./gradlew build
./gradlew runClient
```

`build` also runs the focused bounds and Residence inspection unit tests.
