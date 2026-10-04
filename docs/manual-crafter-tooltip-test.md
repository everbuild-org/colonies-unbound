# Shared crafter tooltip regression (MineColonies 1.1.1374)

The client-only Mixin excludes Colonies Unbound modules from the tooltip lookup
when the same producer belongs to a native MineColonies building. Native buildings
remain the tooltip representatives. Unique addon modules are retained. Actual
building modules and recipe/persistence keys are unchanged.

1. Launch the client with the pinned dependencies and confirm the required Mixin applies.
2. Open Food Stock and hover items with custom kitchen recipes: no duplicate-key
   exception, and the tooltip names the native Kitchen with its required level.
3. Repeat for Blacksmith and Sawmill custom recipes. Check a native-only recipe too.
4. In native and survival kitchens, verify the same datapack custom recipe remains
   available and workers execute it when ingredients and requirements are satisfied.
5. Teach a recipe in a survival workplace, save, restart, and verify its learned
   recipes and worker assignment persist. Repeat in an existing colony save.
6. Launch a dedicated server: the client Mixin must not load or resolve its target.

This patch does not resolve duplicate recipe keys introduced by unrelated addons.
