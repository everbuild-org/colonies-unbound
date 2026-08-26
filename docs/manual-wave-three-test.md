# Wave 3 furnace-workplace test sheet

Use a test colony with `MANAGE_HUTS` permission. Each workplace requires exactly one matching plaque,
one automatically scanned station in the marked volume, and one storage inventory marked afterward.

## Workstation matrix

| Workplace | Compatible scanned station | Native behavior to exercise |
| --- | --- | --- |
| Bakery | Furnace | Baker smelts or bakes a taught recipe |
| Kitchen | Furnace | Chef prepares a taught food recipe |
| Smeltery | Furnace | Smelter processes an ore recipe |
| Stone Smelter | Furnace | Stone Smelter processes a stone recipe |
| Glassblower | Furnace | Glassblower smelts a taught glass recipe |
| Dyer | Furnace | Dyer smelts a taught dye recipe |
| Alchemist | Brewing Stand | Alchemist brews a potion recipe |

Smokers and blast furnaces intentionally do not count: MineColonies' native furnace module only registers
vanilla Furnaces for these workers.

## Repeat for every workplace

- [ ] Plaque appears in Functional Blocks with the correct name and a non-missing model.
- [ ] A volume containing its compatible station commits successfully.
- [ ] A volume without a compatible station is rejected with the workplace name.
- [ ] A volume containing two building plaques is rejected.
- [ ] The HUD reports one scanned workstation and missing storage as DRAFT.
- [ ] Storage mode marks a chest/barrel without opening its inventory.
- [ ] Adding storage changes the HUD to ACTIVE and allows the native worker to be hired.
- [ ] The worker reaches the scanned station and executes an appropriate native recipe.
- [ ] Moving the station removes the old flag and registers the new station after reconciliation.
- [ ] Removing every station deactivates hiring without deleting storage or the committed volume.
- [ ] Restoring a station reactivates the building.
- [ ] Save/reload preserves the volume and re-discovers stations without hanging world load.
- [ ] Sneak-use on the plaque removes its committed mark and deactivates the native building.

Notes:

>

## Alchemist lifecycle

- [ ] A Brewing Stand is accepted; a Cauldron alone is rejected.
- [ ] Removing a registered Brewing Stand prevents the worker targeting its old position.
- [ ] Adding a new Brewing Stand makes it available without rebuilding the plaque.

Notes:

>

## Regression

- [ ] Dining Hall furnace scanning and eating still work.
- [ ] Wave 2 crafting plaques still scan their original workstation types.
- [ ] Wave 1 livestock and Apiary plaques still commit and rescan.
- [ ] No Colonies Unbound exceptions, missing models, or missing translations appear in `latest.log`.

Final result: [ ] Pass  [ ] Pass with issues  [ ] Fail

Tester/date:

Notes:

>
