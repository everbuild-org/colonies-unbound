# Wave 5 service-workplace test sheet

Use one committed volume and one storage inventory per plaque. A plaque should remain Draft until both
storage and at least one compatible service resource are present, then become Active at level 1.

## Scanner matrix

| Plaque | Automatically scanned resource | Native behavior to verify |
| --- | --- | --- |
| Hospital | Bed heads | Healer can hire and assigns patients to scanned beds |
| School | Wool carpet blocks | Teacher and pupils hire; pupils use carpet seats |
| Library | Any block in the bookshelves tag | Student hires and walks to scanned shelves |
| University | Any block in the bookshelves tag | Researcher hires and colony research progresses |
| Tavern | Bed heads | Visitors can use scanned guest beds |
| Graveyard | MineColonies Grave or Named Grave | Undertaker hires and actual graves appear in the colony grave manager |
| Enchanter | Enchanting table | Enchanter hires; operational worker huts are linked as draining targets |
| Nether Worker | Live purple Nether portal block | Nether Worker hires and can start an expedition through that portal |

## Commit and HUD

- [ ] Each of the eight plaques appears in the Functional Blocks creative tab with the correct name/model.
- [ ] A volume with no compatible resource reports the specific missing resource and refuses to commit.
- [ ] A valid volume commits and the plaque HUD reports storage plus the correct scanned resource.
- [ ] Adding one marked Storage point changes the building from Draft to Active.
- [ ] Breaking the last scanned resource returns the plaque to Draft without deleting storage or the volume.
- [ ] Replacing the resource returns it to Active without recommitting.

## Native mechanics

- [ ] Hospital registers only bed heads, not both halves, and stops offering a bed removed from the volume.
- [ ] School pupils choose one of the scanned wool carpets as their classroom position.
- [ ] Library and University accept vanilla and modded blocks in the common bookshelves tag.
- [ ] Tavern registers only bed heads in its native bed module.
- [ ] Graveyard registers real Grave block entities; removing one releases it from the grave manager.
- [ ] Named Graves are also detected as visual grave locations.
- [ ] Enchanter has no “no draining buildings set” blocker when another active worker hut exists.
- [ ] Deactivating/removing the Enchanter mark removes its automatically created draining links.
- [ ] Nether Worker points at an actual portal block rather than an offset plaque/schematic tag.
- [ ] Breaking and relighting the portal deactivates and then reactivates the Nether Worker.

## Persistence and regressions

- [ ] Save/reload preserves the Hospital bed list, School seats, Library/University shelves, and Nether portal target.
- [ ] Restarting the client keeps all eight committed marks and their flags visible.
- [ ] Shrinking a volume unregisters resources that remain in the world but are now outside it.
- [ ] Removing a committed mark deactivates the native building and clears its owned service resources.
- [ ] Waves 1–4 still commit, rescan, deactivate, and reactivate normally.

## Notes

- Enchanter draining targets are colony-wide relationships, so the enchanting table is the local physical
  requirement while active worker buildings are linked automatically.
- The Nether Worker scanner intentionally requires an already lit portal. An obsidian frame by itself is not
  enough to activate the workplace.

Observed result / issues:

- [ ] Passed as written
- [ ] Needs follow-up (describe below)

________________________________________________________________________________

________________________________________________________________________________
