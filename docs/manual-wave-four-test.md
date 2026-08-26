# Wave 4 natural-workplace test sheet

Use a test colony with `MANAGE_HUTS` permission. Each marked volume must contain exactly one matching
plaque, at least one resource target from the matrix below, and one storage inventory marked afterward.

Wave 4 intentionally uses MineColonies specialist field and processing blocks where its native AI requires
them. These are normal craftable MineColonies items; vanilla replacements would require replacement worker AI.

## Resource matrix

| Workplace | Scanned resource | Native binding |
| --- | --- | --- |
| Farm | MineColonies Scarecrow | Farm-field extension owned by this farm |
| Plantation | MineColonies Plantation Field | Plantation-field extension owned by this plantation |
| Fisherman's Hut | Vanilla source water | Native pond search anchored at the hut |
| Forester's Hut | Any vanilla/modded log in the logs tag | Marked volume becomes the native restricted woodland |
| Florist | MineColonies Composted Dirt | Native florist planting-ground list |
| Composter | MineColonies Compost Barrel | Native composter barrel list |

For the Farm, configure a seed on the Scarecrow before expecting the Farmer to claim it. At survival level 1,
MineColonies permits one owned farm field. Plantation research and level restrictions still apply normally.

## Repeat for every workplace

- [ ] Plaque appears in Functional Blocks with the correct name and a non-missing model.
- [ ] Looking at an unconfigured plaque shows its correct workplace name in the HUD.
- [ ] A volume containing its matching resource commits successfully.
- [ ] A volume without the resource is rejected with the workplace name.
- [ ] A volume containing two building plaques is rejected.
- [ ] The HUD reports the resource and missing storage as DRAFT.
- [ ] Storage mode marks a chest/barrel without opening its inventory.
- [ ] Adding storage changes the HUD to ACTIVE and permits hiring the native worker.
- [ ] Moving/removing the resource updates its flag after reconciliation.
- [ ] Removing every resource deactivates hiring without deleting storage or the committed volume.
- [ ] Restoring the resource reactivates the building.
- [ ] Save/reload preserves the volume and native resource ownership without hanging world load.
- [ ] Sneak-use on the plaque removes the committed mark and releases native targets/extensions.

Notes:

>

## Native behavior checks

- [ ] Farmer claims the configured Scarecrow field and tends its selected crop.
- [ ] Plantation claims a valid Plantation Field and tends its configured crop type.
- [ ] Fisherman locates a usable pond in/near the volume, walks to shore, and fishes.
- [ ] Forester does not select a tree whose trunk begins outside the committed volume.
- [ ] Resizing/recommitting the Forester volume updates its native restricted corners.
- [ ] Florist plants and gathers flowers only from registered Composted Dirt.
- [ ] Composter fills, advances, and harvests registered MineColonies Compost Barrels.
- [ ] Removing a Compost Barrel does not leave it in the survival barrel registry after reconciliation.

Notes:

>

## Regression

- [ ] Wave 3 furnace and brewing targets still unregister/re-register correctly.
- [ ] Wave 2 crafting plaques still scan their original workstation types.
- [ ] Wave 1 livestock and Apiary plaques still commit and rescan.
- [ ] Dining Hall eating and furnace scanning still work.
- [ ] No Colonies Unbound exceptions, missing models, or missing translations appear in `latest.log`.

Final result: [ ] Pass  [ ] Pass with issues  [ ] Fail

Tester/date:

Notes:

>
