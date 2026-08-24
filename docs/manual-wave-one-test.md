# Wave 1 manual test sheet

Use a fresh test colony with `MANAGE_HUTS` permission. Run each section once after a clean client
launch and record failures, screenshots, or log timestamps in the notes fields.

## Shared livestock flow

Repeat for Chicken Pen, Pig Pen, Rabbit Hutch, and Stable.

- [ ] The plaque is present in Functional Blocks and places with the correct name/texture.
- [ ] Marking a volume containing exactly one plaque, a fence gate, and grass/dirt succeeds.
- [ ] A volume with no gate is rejected.
- [ ] A volume with no grass/dirt floor is rejected.
- [ ] The HUD reports storage, fence gate, and pasture status correctly.
- [ ] Storage mode consumes the marker click instead of opening the container UI.
- [ ] Marking one chest/barrel activates the level-1 building and permits hiring the native worker.
- [ ] Adding/removing a gate or pasture block updates the committed mark without losing storage.
- [ ] Breaking a required scanned POI deactivates hiring; restoring it reactivates hiring.
- [ ] Sneak-use on the plaque removes the committed volume.
- [ ] Save/reload preserves the committed volume, flags, POIs, and active state.

Notes:

>

## Stable additions

- [ ] A Stable remains a draft until a Stall point is authored.
- [ ] Stall mode accepts a non-air block inside the stable volume.
- [ ] The stall flag appears and the HUD changes to active after storage and stall are present.
- [ ] The Stable Master can use the authored stall through MineColonies' native stable behavior.

Notes:

>

## Apiary

- [ ] The Survival Apiary Plaque is present in Functional Blocks and places correctly.
- [ ] A volume with exactly one plaque and at least one vanilla beehive or bee nest commits.
- [ ] A volume without a hive is rejected with the apiary-specific message.
- [ ] The HUD shows scanned hives and authored storage independently.
- [ ] Storage mode marks a chest/barrel without opening its UI and activates the apiary.
- [ ] A Beekeeper can be hired and recognizes all hives inside the volume.
- [ ] Placing another hive adds it automatically after the rescan delay.
- [ ] Breaking a hive removes it automatically; removing the last hive deactivates hiring.
- [ ] Hives outside the committed volume are ignored.
- [ ] Save/reload and crossing a chunk boundary do not hang world loading.
- [ ] Sneak-use on the plaque removes the committed mark and native hive registrations.

Notes:

>

## Cross-building regression

- [ ] A volume containing plaques for two different building types is rejected.
- [ ] Residence, Dining Hall, Guard Tower, Cow Pen, and Sheep Pen still commit and operate.
- [ ] Marker modes cycle through Building Area, Storage, Worksite, Entrance, Interaction, Stall, and Patrol Route.
- [ ] No missing-model purple/black plaque textures or missing flag icons appear.
- [ ] `latest.log` has no Colonies Unbound exceptions during commit, rescan, reload, or removal.

Final result: [ ] Pass  [ ] Pass with issues  [ ] Fail

Tester/date:

Notes:

>
