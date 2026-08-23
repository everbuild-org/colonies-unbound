# Colonies Unbound — Survival Residence Manual Test

Use this worksheet to test one build of the Residence prototype. Fill in every result with
`PASS`, `FAIL`, `BLOCKED`, or `NOT TESTED`, and attach screenshots or logs where useful.

## Test session

| Field | Value |
|---|---|
| Tester | |
| Date and time | |
| Colonies Unbound commit | |
| Colonies Unbound version | |
| Minecraft version | 1.21.1 |
| NeoForge version | 21.1.248 |
| MineColonies version | 1.1.1374-1.21.1-snapshot |
| Singleplayer or multiplayer | |
| New or existing world | |
| Survival or creative | |
| Other installed mods | |
| Log file | |

## Test house

| Field | Value |
|---|---|
| Dimension | |
| Colony name and ID | |
| Plaque position | |
| First selected corner | |
| Second selected corner | |
| Selected dimensions | |
| Beds inside selection | |
| Doors/entrances | |
| Relevant notes | |

## 1. Startup and registration

| # | Test | Expected result | Result | Notes/evidence |
|---:|---|---|---|---|
| 1.1 | Start the client with Colonies Unbound and its pinned dependencies. | The game reaches the title screen without a mod-loading error. | | |
| 1.2 | Open or create a world. | The world loads without a registry or datapack error. | | |
| 1.3 | Search the Tools & Utilities creative tab for `Worksite Marker`. | The item is present and has its translated name and tooltip. | | |
| 1.4 | Search the Functional Blocks creative tab for `Survival Residence Plaque`. | The plaque is present with the expected model and translated name. | | |
| 1.5 | Run `/give @s coloniesunbound:worksite_marker`. | The command resolves and gives the marker. | | |
| 1.6 | Run `/give @s coloniesunbound:survival_residence_plaque`. | The command resolves and gives the plaque. | | |

## 2. Plaque placement

| # | Test | Expected result | Result | Notes/evidence |
|---:|---|---|---|---|
| 2.1 | Place a plaque inside colony territory. | The block places with the expected orientation and no error. | | |
| 2.2 | Look at or interact with the plaque. | MineColonies recognizes it as a colony building anchor and does not crash. | | |
| 2.3 | Attempt plaque placement outside colony territory. | MineColonies rejects or safely handles placement; no orphan building or crash remains. | | |
| 2.4 | Break a newly placed, unconfigured plaque. | The plaque is removed cleanly and drops itself when appropriate. | | |
| 2.5 | Save and reload with an unconfigured plaque present. | The plaque and its block entity reload without errors. | | |

## 3. Marker interaction and overlay

| # | Test | Expected result | Result | Notes/evidence |
|---:|---|---|---|---|
| 3.1 | Use the marker on the first corner block. | An action-bar message shows the selected coordinates. | | |
| 3.2 | Keep holding the marker and aim at different blocks. | A cyan live area preview follows the targeted second corner. | | |
| 3.3 | Switch away from the marker. | All marker overlays disappear. | | |
| 3.4 | Hold the marker in the offhand. | The marker overlay remains visible and correct. | | |
| 3.5 | Use the marker on the opposite corner. | The completed volume receives a gold outline and endpoint pins. | | |
| 3.6 | Inspect the marker tooltip after completion. | It reports the selected inclusive dimensions. | | |
| 3.7 | Sneak-use the marker on a block. | The current selection clears and the overlay disappears. | | |
| 3.8 | Select corners in reverse coordinate order. | The same normalized volume is produced without negative dimensions. | | |
| 3.9 | Select across the maximum permitted axis length. | A 128-block side is accepted; a 129-block side is rejected. | | |
| 3.10 | Move the selected marker item between inventory slots or containers. | Selection data stays attached to that item stack. | | |

## 4. Residence inspection

| # | Test | Expected result | Result | Notes/evidence |
|---:|---|---|---|---|
| 4.1 | Select a loaded house containing one plaque and one bed. | The residence saves and reports one discovered bed. | | |
| 4.2 | Repeat with several beds. | Only bed heads are counted; capacity equals the number of physical beds. | | |
| 4.3 | Include bed feet and heads inside the selection. | Each bed is counted exactly once. | | |
| 4.4 | Put a bed partly outside the selected bounds. | It is counted only when its head is inside the bounds. | | |
| 4.5 | Select a volume with no bed. | Inspection fails with `no beds found`. | | |
| 4.6 | Select a volume with no plaque. | Inspection fails and requests a Survival Residence Plaque. | | |
| 4.7 | Select a volume containing two plaques. | Inspection fails because exactly one plaque is required. | | |
| 4.8 | Select a volume over 262,144 blocks. | Inspection refuses the volume without a long freeze or crash. | | |
| 4.9 | Select an area extending into unloaded chunks, if reproducible. | Inspection refuses it as not fully loaded. | | |
| 4.10 | Select an area crossing a colony border. | Registration fails and explains that the Residence must remain inside one colony. | | |

## 5. MineColonies building behavior

| # | Test | Expected result | Result | Notes/evidence |
|---:|---|---|---|---|
| 5.1 | Complete a valid inspection and open the plaque/building UI. | A dedicated survival Residence is shown without a UI crash. | | |
| 5.2 | Inspect its building level. | It is treated as built at level 1. | | |
| 5.3 | Inspect housing capacity with several beds. | Capacity matches registered bed count, even when greater than building level. | | |
| 5.4 | Allow automatic citizen housing or assign residents manually. | Citizens can be assigned up to bed capacity. | | |
| 5.5 | Attempt to assign one citizen beyond capacity. | MineColonies refuses the extra assignment. | | |
| 5.6 | Check build, upgrade, repair, and deconstruct actions. | Schematic-oriented actions are unavailable or harmless. | | |
| 5.7 | Check colony population capacity before and after registration. | Maximum citizens increases by the Residence bed capacity. | | |
| 5.8 | Inspect colony/building warnings. | No false `unbuilt schematic` warning blocks normal Residence use. | | |

## 6. Citizen navigation and sleep

Record at least one full evening-to-morning cycle.

| # | Test | Expected result | Result | Notes/evidence |
|---:|---|---|---|---|
| 6.1 | Assign one citizen and wait for sleep time. | The citizen walks into the selected building bounds. | | |
| 6.2 | Observe the assigned citizen at the bed. | The citizen chooses a registered bed and sleeps successfully. | | |
| 6.3 | Assign several citizens to distinct beds. | Citizens use valid beds without persistent contention. | | |
| 6.4 | Place the plaque away from the beds but inside the selected bounds. | Citizens navigate to beds rather than stopping at the plaque. | | |
| 6.5 | Temporarily obstruct the route to a bed. | Failure is safe and recovery occurs after the route is restored. | | |
| 6.6 | Remove one registered bed after assignment. | No crash occurs; note whether capacity or assignments become stale. | | |
| 6.7 | Replace the bed and reinspect the same marker. | Registered beds and capacity refresh to the latest scan. | | |

## 7. Persistence and reinspection

| # | Test | Expected result | Result | Notes/evidence |
|---:|---|---|---|---|
| 7.1 | Save, exit, and reload after valid registration. | Bounds, plaque building, beds, capacity, and residents persist. | | |
| 7.2 | Restart the entire client/server and reload. | The Residence still loads without registry or deserialization errors. | | |
| 7.3 | Add or remove beds, then repeat the selection with the same marker. | The existing marker updates rather than creating a duplicate. | | |
| 7.4 | Inspect colony data after reinspection. | Old registered beds are removed and only current bed heads remain. | | |
| 7.5 | Use a fresh marker on the same plaque. | Record whether a duplicate marker/building is created or safely rejected. | | |
| 7.6 | Travel to another dimension and return. | Dimension-local Residence data remains associated with the correct world. | | |
| 7.7 | Delete the colony or remove its town hall. | The Residence fails or cleans up safely; record any orphan data. | | |

## 8. Permissions and multiplayer

If multiplayer is unavailable, mark this section `NOT TESTED`.

| # | Test | Expected result | Result | Notes/evidence |
|---:|---|---|---|---|
| 8.1 | Register as colony owner. | Registration succeeds. | | |
| 8.2 | Register as a player with `MANAGE_HUTS`. | Registration succeeds. | | |
| 8.3 | Register as a player without `MANAGE_HUTS`. | Registration is rejected with a permission message. | | |
| 8.4 | Have two players view the marker selection. | Only the holder sees item-local overlays; no stale or cross-player overlay appears. | | |
| 8.5 | Reinspect while another player has the building UI open. | No desynchronization or crash occurs. | | |
| 8.6 | Disconnect and reconnect after registration. | Building and marker state synchronize correctly. | | |

## 9. Destructive and recovery cases

Back up the world before this section.

| # | Test | Expected result | Result | Notes/evidence |
|---:|---|---|---|---|
| 9.1 | Break a configured plaque. | MineColonies removes or invalidates the building cleanly. | | |
| 9.2 | Replace the plaque at the same position. | It can be registered again without duplicate or corrupt state. | | |
| 9.3 | Destroy every registered bed. | No crash occurs; record population capacity and resident behavior. | | |
| 9.4 | Reinspect after rebuilding the house with different bounds. | The logical bounds and bed registrations update correctly. | | |
| 9.5 | Unload/reload the plaque chunk while citizens are assigned. | Building and citizen references remain valid. | | |

## Observations

### What worked well

- 
- 
- 

### Confusing behavior or usability problems

- 
- 
- 

### Performance observations

| Scenario | Approximate delay/FPS impact | Notes |
|---|---|---|
| Holding marker with live preview | | |
| Completing a small inspection | | |
| Completing a large inspection | | |
| Citizen sleep/navigation | | |

## Issues found

Create one row per distinct issue. Preserve the world and logs for crashes or data corruption.

| ID | Severity | Summary | Reproduction steps | Expected | Actual | Evidence |
|---|---|---|---|---|---|---|
| CU-TEST-001 | | | | | | |
| CU-TEST-002 | | | | | | |
| CU-TEST-003 | | | | | | |

Severity suggestion:

- `BLOCKER`: crash, world corruption, or cannot register any Residence
- `HIGH`: broken persistence, citizen assignment, capacity, or sleep
- `MEDIUM`: incorrect validation, overlay, permissions, or recoverable stale state
- `LOW`: text, model, visual polish, or minor usability issue

## Final assessment

| Field | Value |
|---|---|
| Overall result | |
| Safe for continued development? | |
| Safe for test-world use? | |
| Highest-severity open issue | |
| Recommended next action | |

### Tester summary

_Write a short overall assessment here._

