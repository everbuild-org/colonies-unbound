# Manual building level test

Use a survival world with MineColonies and a colony member who has `MANAGE_HUTS` permission.

## Basic editing

- [ ] Commit a survival building and confirm its plaque HUD shows **Set level 1** and **Active level 1**.
- [ ] Cycle the Worksite Marker to **Building Level** mode.
- [ ] Use the marker on the plaque and confirm the action bar reports level 2.
- [ ] Look at the plaque and confirm both HUD levels are 2.
- [ ] Repeat up to level 5 and confirm another use reports the maximum instead of exceeding it.
- [ ] Sneak-use the plaque and confirm the level decreases.
- [ ] Continue down to level 1 and confirm it cannot go lower.
- [ ] Save and reload the world; confirm the selected level persists.

## Readiness separation

- [ ] Set a worker building to level 3 or higher.
- [ ] Remove one required POI or scanner-owned block and wait for reconciliation.
- [ ] Confirm the HUD retains **Set level 3** (or higher) but shows **Active level: Inactive**.
- [ ] Confirm the building cannot hire or operate while inactive.
- [ ] Restore the missing requirement and confirm **Active level** returns to the selected level.

## Permissions and special buildings

- [ ] Without `MANAGE_HUTS`, try changing a level and confirm it is rejected.
- [ ] Use Building Level mode on a Post Box, Stash, quarry, or Mystical Site and confirm it reports that the building is fixed at level 1.
- [ ] Confirm ordinary worker buildings, residences, guards, Town Hall, and other levelled facilities can reach level 5.

## Removal and compatibility

- [ ] Remove a committed mark in Building Area mode and confirm the native building becomes inactive.
- [ ] Load an older world made before manual levels existed and confirm existing plaques default to level 1.
