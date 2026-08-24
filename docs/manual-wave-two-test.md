# Wave 2 crafting-workplace test sheet

Use a test colony with `MANAGE_HUTS` permission. Every workplace requires exactly one matching plaque,
one compatible workstation in the marked volume, and one storage inventory marked afterward.

## Workstation matrix

| Workplace | Compatible scanned station |
| --- | --- |
| Blacksmith | Anvil or Smithing Table |
| Sawmill | Crafting Table or Stonecutter |
| Stonemason | Stonecutter |
| Fletcher | Fletching Table |
| Mechanic | Crafting Table |
| Concrete Mixer | Shallow flowing water (levels 1–5) |
| Crusher | Anvil |
| Sifter | Scaffolding |

## Repeat for every workplace

- [ ] Plaque appears in Functional Blocks with the correct name and a non-missing model.
- [ ] A volume containing its compatible station commits successfully.
- [ ] A volume without a compatible station is rejected with the workplace name.
- [ ] A volume containing two building plaques is rejected.
- [ ] The HUD reports one scanned workstation and missing storage as DRAFT.
- [ ] Storage mode marks a chest/barrel without opening its inventory.
- [ ] Adding storage changes the HUD to ACTIVE and allows the native worker to be hired.
- [ ] The worker reaches the building and accepts/executes an appropriate native recipe.
- [ ] Adding another compatible station creates another flag after the rescan delay.
- [ ] Removing every station deactivates the building without deleting storage or its volume.
- [ ] Restoring a station reactivates the building.
- [ ] Save/reload preserves the volume and re-discovers stations without hanging world load.
- [ ] Sneak-use on the plaque removes its committed mark and deactivates the native building.

Notes:

>

## Concrete Mixer native-water checks

- [ ] A source-water-only pool is rejected; a shallow flowing channel is accepted.
- [ ] Every discovered channel cell is shown as a workstation in the HUD/overlay.
- [ ] The Concrete Mixer places powder into registered water cells and later harvests concrete.
- [ ] Extending the channel adds native mixer capacity after reconciliation.
- [ ] Removing channel water deactivates the building when no valid cells remain.

Notes:

>

## Regression

- [ ] Wave 1 livestock and Apiary plaques still commit and rescan.
- [ ] Dining Hall furnace scanning still works.
- [ ] No Colonies Unbound exceptions, missing models, or missing translations appear in `latest.log`.

Final result: [ ] Pass  [ ] Pass with issues  [ ] Fail

Tester/date:

Notes:

>
