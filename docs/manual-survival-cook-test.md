# Survival Dining Hall Test

Build a small dining hall inside one colony with one Survival Dining Hall Plaque, one chest or
barrel, one furnace/smoker/blast furnace, and one door/gate/trapdoor. An ordinary solid block may
be used as an Interaction point for this first adapter pass.

| Result | Check | Expected |
|---|---|---|
| ☐ | Select the hall volume in Building Area mode | A restaurant flag and persistent outline replace the temporary selection. |
| ☐ | Inspect the Dining Hall before adding points | It remains level 0 and does not hire a Cook yet. |
| ☐ | Mark the chest/barrel in Storage mode | Its inventory stays closed and it becomes a registered building container. |
| ☐ | Mark the furnace in Worksite mode | Its menu stays closed and it is registered in MineColonies' furnace module. |
| ☐ | Mark the door/gate/trapdoor in Entrance mode | The block stays closed and the native `entrance` tag is created. |
| ☐ | Add the third required point | The draft reports ready and the Dining Hall becomes built at level 1. |
| ☐ | Open the plaque UI | Native Cook hiring, fuel list, and restaurant menu modules are present without a crash. |
| ☐ | Assign or auto-hire a Cook | The citizen receives the native Cook job and walks to the marked furnace/work point. |
| ☐ | Add an Interaction point | MineColonies receives it as a native restaurant `sit` position. |
| ☐ | Remove one required point | The corresponding native furnace/container/tag is removed and the hall returns to level 0. |
| ☐ | Re-add the point and reload the world | The hall returns to level 1; bounds, points, modules, and flags persist. |
| ☐ | Sneak-use the marker on the Dining Hall plaque | The committed mark and native facility registrations are cleared. |
| ☐ | View flags near the edge of the camera | Visible flags remain rendered even when the plaque itself is off-screen. |

This slice validates native building activation and metadata plumbing. Full Cook AI behavior,
restaurant customer flow, automatic furnace discovery, and quality tiers remain follow-up work.
