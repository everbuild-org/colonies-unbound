# Survival plaque recipe test sheet

Every Colonies Unbound plaque has a shapeless conversion recipe. Combine the matching MineColonies
hut or controller with one vanilla Feather. The recipe consumes both ingredients and returns the
corresponding survival plaque.

## Representative recipes

| Input | Expected output |
| --- | --- |
| MineColonies Citizen Hut + Feather | Survival Residence Plaque |
| MineColonies Cook Hut + Feather | Survival Dining Hall Plaque |
| MineColonies Warehouse Hut + Feather | Survival Warehouse Plaque |
| MineColonies Builder Hut + Feather | Survival Builder Plaque |
| MineColonies Simple Quarry + Feather | Survival Simple Quarry Plaque |
| MineColonies Post Box + Feather | Survival Post Box Plaque |
| MineColonies Stash + Feather | Survival Stash Plaque |

## Recipe-book and crafting checks

- [ ] Each representative pair produces exactly one expected plaque in a crafting grid.
- [ ] Removing either the Feather or MineColonies input removes the result.
- [ ] Crafting consumes the native input and Feather exactly once.
- [ ] The recipe appears in the recipe book after its ingredients are discovered.
- [ ] JEI/EMI, if installed, displays the same two-input shapeless recipe.
- [ ] At least one plaque from each implementation wave (Residence and Waves 1–6) is craftable.
- [ ] Crafted plaques place, commit, drop themselves, and retain their normal HUD and lifecycle.

## Full-roster regression

- [ ] All 54 survival plaques have a recipe and no recipe produces a different facility type.
- [ ] The Worksite Marker recipe remains MineColonies Clipboard + Feather.
- [ ] Native MineColonies hut recipes remain available and unchanged.

Observed result / issues:

- [ ] Passed as written
- [ ] Needs follow-up (describe below)

________________________________________________________________________________

________________________________________________________________________________
