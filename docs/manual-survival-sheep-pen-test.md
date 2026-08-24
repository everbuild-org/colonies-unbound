# Survival Sheep Pen Test

Build a fenced sheep pen wholly inside one colony. Include grass or dirt floor, at least one vanilla
fence gate, one chest or barrel, and one Survival Sheep Pen Plaque. Put at least two sheep inside. The
selected volume must include the floor, fence gate, plaque, sheep, and storage.

| Done | Action | Expected result |
|---|---|---|
| ☐ | Run `/give @s coloniesunbound:survival_sheep_pen_plaque` and place it inside the colony | MineColonies registers an unconfigured Survival Sheep Pen. |
| ☐ | Look at the plaque | The HUD says `Sheep Pen` and reports Storage, Fence gates, and Grass or dirt floor requirements. |
| ☐ | Select the complete pen with the Worksite Marker | The volume is saved; the building remains a draft until storage is marked. Shared green pen, gate, and pasture flags appear while holding the marker. |
| ☐ | Switch to Storage mode and use the marker on the chest or barrel | The HUD becomes `ACTIVE`, the native building reaches level 1, and a Shepherd can be hired. |
| ☐ | Hire/assign a Shepherd and provide wheat, an axe, and shears | The Shepherd enters the marked bounds, breeds sheep, shears wool, and uses native MineColonies settings. |
| ☐ | Open the building settings | Native breeding, shearing, and dyeing controls are present and can be changed without a UI crash. |
| ☐ | Add, replace, then remove fence gates | Scanner-owned gate flags reconcile automatically. With no gate, the building deactivates but keeps its mark and storage. |
| ☐ | Replace all included grass/dirt with stone, then restore one dirt block | Pasture loss deactivates the building; restoring pasture reactivates it without remarking storage. |
| ☐ | Save and reload the world | Volume, requirements, storage, worker assignment, and Shepherd settings persist. Joining does not stall during spawn preparation. |
| ☐ | Sneak-use the marker on the plaque | The committed mark, registered storage, and active building level are cleared. |

The Sheep Pen and Cow Pen share one no-load reconciliation path. This test therefore also checks that
adding another livestock profession did not reintroduce synchronous chunk loading during world join.
