# Typed Point and Workplace Draft Test

Use an existing committed Survival Residence containing a chest or barrel, a furnace-class block,
and a door, fence gate, or trapdoor. Hold the Worksite Marker throughout the test.

| Result | Check | Expected |
|---|---|---|
| ☐ | Right-click in air | Mode cycles and appears in the action bar and tooltip. |
| ☐ | In Storage mode, use the marker on a chest or barrel | The inventory stays closed, a Storage point is added, and a gold flag appears. |
| ☐ | In Storage mode, use the marker on a furnace | The inventory stays closed and the target is rejected as invalid storage. |
| ☐ | In Worksite mode, use the marker on a furnace, smoker, or blast furnace | Its menu stays closed, a Worksite point is added, and an orange flag appears. |
| ☐ | In Entrance mode, use the marker on a door, fence gate, or trapdoor | The block does not toggle, an Entrance point is added, and a teal flag appears. |
| ☐ | Add all three required point types | The action bar reports that the workplace draft is ready. |
| ☐ | Sneak-use a marked block in its matching mode | The point is removed without opening or toggling the block. |
| ☐ | Sneak-use an unmarked block | No point is removed and a clear error is shown. |
| ☐ | Add or remove a bed | Bed POIs reconcile while all manual points remain. |
| ☐ | Leave and reload the world | Mode, points, colors, and workplace readiness remain consistent. |
| ☐ | Try editing without `MANAGE_HUTS` | The edit is rejected and the block does not open. |

Known separate papercut: billboard culling at some viewing angles is not part of this acceptance pass.
