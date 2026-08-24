# Survival Dining Hall Test

Build a small dining hall inside one colony with one Survival Dining Hall Plaque, one chest or
barrel, one furnace/smoker/blast furnace, and one door/gate/trapdoor. An ordinary solid block may
be used as an Interaction point for this first adapter pass.

| Result | Check | Expected |
|---|---|---|
| ☐ | Select the hall volume in Building Area mode | A restaurant flag and persistent outline replace the temporary selection; every furnace-family block receives a Worksite flag. |
| ☐ | Inspect the Dining Hall before adding points | It remains level 0 and does not hire a Cook yet. |
| ☐ | Mark the chest/barrel in Storage mode | Its inventory stays closed and it becomes a registered building container. |
| ☐ | Try to mark a furnace in Worksite mode | The menu stays closed and the marker reports that this point is scanner-owned. |
| ☐ | Mark the door/gate/trapdoor in Entrance mode | The block stays closed and the native `entrance` tag is created. |
| ☐ | Finish marking Storage and Entrance | With the scanned furnace present, the Dining Hall reports ready and becomes built at level 1. |
| ☐ | Open the plaque UI | Native Cook hiring, fuel list, and restaurant menu modules are present without a crash. |
| ☐ | Assign or auto-hire a Cook | The citizen receives the native Cook job and walks to the marked furnace/work point. |
| ☐ | Add an Interaction point | MineColonies receives it as a native restaurant `sit` position. |
| ☐ | Remove the Storage or Entrance point | The corresponding native container/tag is removed and the hall returns to level 0. |
| ☐ | Re-add the point and reload the world | The hall returns to level 1; bounds, scanned/manual points, modules, and flags persist. |
| ☐ | Sneak-use the marker on the Dining Hall plaque | The committed mark and native facility registrations are cleared. |
| ☐ | View flags near the edge of the camera | Visible flags remain rendered even when the plaque itself is off-screen. |
| ☐ | Place another furnace inside the committed volume | Within about half a second it receives a Worksite flag and is registered without using the marker. |
| ☐ | Break one registered furnace | Its Worksite flag and native furnace registration disappear; the hall remains active if another furnace exists. |
| ☐ | Break the final furnace | The plaque HUD reports the missing furnace and the hall returns to level 0. |
| ☐ | Replace the furnace and reload its chunk | The scanner restores the point, native registration, and level 1 state. |

This slice validates native building activation, customer flow, metadata plumbing, and automatic
furnace discovery. Broader automatic POI rules and quality tiers remain follow-up work.
