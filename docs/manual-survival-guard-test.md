# Survival Guard Tower Test

Build a small tower wholly inside one colony and place one Survival Guard Tower Plaque inside it.
Have an unemployed citizen available. Patrol nodes may extend beyond the selected tower volume, but
must remain inside the same colony.

| Result | Check | Expected |
|---|---|---|
| ☐ | Select the tower volume in Building Area mode | A red guard flag and persistent volume outline replace the temporary selection. |
| ☐ | Look at the plaque without holding the marker | The HUD reports an active Guard Tower and zero optional patrol nodes. |
| ☐ | Open the plaque UI | Native guard hiring, guard type, equipment, hostile list, and patrol settings are present without a crash. |
| ☐ | Hire a Knight or Ranger | The citizen receives the native guard job; no construction warning appears. |
| ☐ | Cycle the marker to Patrol Route mode | The action bar reports Patrol Route mode. |
| ☐ | Use the marker on the committed Guard Tower plaque | The action bar confirms that this tower owns subsequent nodes. |
| ☐ | Use three ground blocks around the colony in order | Numbered red patrol flags appear and lines form a closed route in insertion order. |
| ☐ | Try to add a duplicate node | The route is unchanged and a duplicate warning appears. |
| ☐ | Try to add a node outside the colony | The route is unchanged and an ownership warning appears. |
| ☐ | Try to add a node beyond the tower's native patrol range | The route is unchanged and a range warning appears. |
| ☐ | Sneak-use the marker on the second node | That node disappears, remaining nodes renumber, and the route reconnects. |
| ☐ | Set the native patrol mode to manual/custom | The guard visits the synchronized patrol targets. |
| ☐ | Reload the world | The volume, ordered nodes, native patrol targets, flags, and HUD count persist. |
| ☐ | Sneak-use the plaque in Building Area mode | The committed mark and native patrol targets are cleared and the tower returns to level 0. |

This first Guard Tower slice validates native guard hiring and ordered manual routes. Automated route
validation, directional arrows, rally points, and per-segment path accessibility remain follow-up work.
