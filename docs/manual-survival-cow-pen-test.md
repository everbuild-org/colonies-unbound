# Survival Cow Pen Test

Build a fenced cattle or goat pen wholly inside one colony. Include grass or dirt floor, at least one
vanilla fence gate, one chest or barrel, and one Survival Cow Pen Plaque. Put at least two cows or goats
inside. The selected volume must include the floor blocks, fence gate, plaque, animals, and storage.

| Done | Action | Expected result |
|---|---|---|
| ☐ | Run `/give @s coloniesunbound:survival_cow_pen_plaque` and place it inside the colony | MineColonies registers an unconfigured Survival Cow Pen. |
| ☐ | Look at the plaque | The HUD reports `NOT SET` with Storage, Fence gates, and Grass or dirt floor requirements. |
| ☐ | Select two corners around a pen that has no fence gate | The marker rejects it and reports that a fence gate is required. |
| ☐ | Add a fence gate but select a volume that excludes all grass and dirt | The marker rejects it and reports that pasture floor is required. |
| ☐ | Select the complete pen volume | The mark is saved; gate and pasture flags appear while holding the marker. The building remains a draft because Storage is missing. |
| ☐ | Switch to Storage mode and use the marker on the chest or barrel | The HUD becomes `ACTIVE`, the native building reaches level 1, and a Cowhand can be hired. The inventory opens only when used without the marker. |
| ☐ | Hire/assign a Cowhand and supply an axe plus wheat | The Cowhand enters the marked bounds and tends cattle or goats using MineColonies' native herder AI. |
| ☐ | Add or replace a fence gate inside the committed volume | Within about one second, the gate flags update automatically and the marked storage remains assigned. |
| ☐ | Remove every fence gate | The HUD reports the missing gate and the Cow Pen deactivates instead of deleting the mark. |
| ☐ | Restore a fence gate | The building returns to `ACTIVE` without remarking storage. |
| ☐ | Remove the grass/dirt floor and replace it with stone | The pasture requirement disappears and the building deactivates. Restoring one grass/dirt block reactivates it. |
| ☐ | Save and reload the world | The marked volume, scanned requirements, storage, building state, and worker assignment persist. Joining does not stall at spawn preparation. |
| ☐ | Sneak-use the marker on the plaque | The committed mark, registered storage, and active building level are cleared. |

This slice intentionally targets MineColonies' Cowboy job, which handles cows and goats. Sheep,
chickens, pigs, and rabbits need their own native building registrations and will follow as separate
adapters rather than changing an occupied pen's profession dynamically.
