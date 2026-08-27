# Wave 6 network-and-structural test sheet

Wave 6 completes the pinned MineColonies facility roster. Test against a disposable colony/world first:
Builder, Miner, and quarry jobs create native work orders that intentionally alter blocks.

## Physical contracts

| Plaque | Required scanned resource | Additional behavior |
| --- | --- | --- |
| Archery | Vanilla Target | Glowstone blocks in the volume become firing stands |
| Combat Academy | Carved pumpkin directly above a hay block | Hay block becomes the fighting position |
| Warehouse | MineColonies Rack | Racks become warehouse containers; no manual storage point required |
| Post Box | None | Plaque is an always-level-1 request-system component |
| Courier Hut | Marked storage | Courier is assigned through a native Warehouse |
| Barracks | Marked storage | Active Barracks Towers link to their nearest active Barracks |
| Barracks Tower | Bed heads and marked storage | Beds enter the native guard-bed module |
| Gate House | Up to four vanilla Target blocks and marked storage | Targets become guard posts; beds are scanned additionally |
| Builder's Hut | Crafting table and marked storage | Builder can claim native colony work orders |
| Mine | Ladder and marked storage | First ladder defines shaft start and attached support direction |
| Simple Quarry | None | Committed volume is the controller footprint |
| Medium Quarry | None | Committed volume is the controller footprint |
| Town Hall | None | Registers native Town Hall settings on the survival building |
| Stash | None | Registers a native request-system stash component |
| Mystical Site | None | Registers a native colony mystical site |

## Registration and HUD

- [ ] All 15 plaques appear in Functional Blocks with translated names and distinct models.
- [ ] Each plaque commits with the requirements shown in the table.
- [ ] Warehouse becomes Active with a rack and no authored Storage point.
- [ ] Courier Hut and Barracks become Active with Storage alone.
- [ ] Post Box, quarries, Town Hall, Stash, and Mystical Site become Active after volume commit.
- [ ] The other six workplaces require both their scanned resource and authored Storage.
- [ ] Breaking the last required resource deactivates the building without deleting its volume/manual points.
- [ ] Replacing that resource reactivates it after the debounced rescan.

## Logistics network

- [ ] A committed Warehouse marks every scanned MineColonies Rack as “in warehouse.”
- [ ] Items placed in scanned racks are found by native warehouse requests.
- [ ] A Courier hired at the Courier Hut is assigned by the Warehouse courier module.
- [ ] Courier can deposit its inventory into a scanned rack without a null warehouse-tile error.
- [ ] Removing a rack from the volume clears its warehouse flag and container registration.
- [ ] Post Box requests enter the same colony request network.
- [ ] Stash contents are visible to its native request-system component.

## Defense and training

- [ ] Each active Barracks Tower chooses the nearest active Barracks as parent.
- [ ] Deactivating that Barracks moves the Tower to the next nearest active Barracks, if present.
- [ ] Barracks Tower guard modules can hire and use scanner-owned beds.
- [ ] Gate House registers with the colony connection manager at Active and unregisters when removed.
- [ ] Gate House guards stand at scanned Target blocks rather than the plaque.
- [ ] Archers alternate between scanned glowstone stands and shoot scanned Target blocks.
- [ ] Combat Academy trainees walk to the hay block below each valid pumpkin dummy.

## Structural jobs

- [ ] Builder hires and can claim a normal build/repair work order.
- [ ] Miner hires without a missing ladder/cobble schematic-tag error.
- [ ] Miner shaft rotation follows the wall supporting the selected ladder.
- [ ] Quarrier hired at the Mine discovers each unfinished quarry controller.
- [ ] Simple Quarry loads `simplequarryshaft1.blueprint` and starts below its plaque.
- [ ] Medium Quarry loads `mediumquarryshaft1.blueprint` and starts below its plaque.
- [ ] Quarry footprint/corners follow the committed volume and do not pull unrelated chunks into it.

## Persistence and regressions

- [ ] Save/reload preserves targets, firing stands, training dummies, guard posts, mine shaft start, and links.
- [ ] Removing a committed mark clears owned racks, beds, posts, and active native level.
- [ ] Restarting the client keeps all Wave 6 flags and HUD types visible.
- [ ] Waves 1–5 still commit, rescan, deactivate, and reactivate normally.

## Known architectural note

Every MineColonies colony begins with a founding Town Hall. A Survival Town Hall added later exposes the
native Town Hall building/settings contract but does not silently replace the colony's founding anchor.
Replacing or migrating the founding anchor is deliberately outside this non-destructive adapter slice.

Observed result / issues:

- [ ] Passed as written
- [ ] Needs follow-up (describe below)

________________________________________________________________________________

________________________________________________________________________________
