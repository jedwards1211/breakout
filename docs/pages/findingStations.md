# Finding Stations

It's easy to search for stations and find them in the 3D view. You
can search for:

- Stations
  - "AJ" (all stations beginning with "AJ")
  - "AJ51" (just station "AJ51")
  - "AJ50-60" (stations "AJ50", "AJ51", ..., "AJ60")
  - "AJ50+" (stations "AJ50" and higher)
  - "AJ, QC35-40" (all "AJ" stations and "QC35", "QC36", ..., "QC40")
- Trip Names
- Surveyors' Names
- Shot comments

For instance, "joe" would find all shots where anyone named Joe was on the survey
team, plus shots from trips with names like "Joe's Wet Lead", etc.

## The Search Field

The search field is at the top left of the search drawer (opened by moving the
mouse to the left side of the window) and the survey drawer (opened by moving
the mouse to the bottom of the window):

![Search Drawer](/static/search/search-drawer.png)
![Survey Drawer](/static/search/survey-drawer.png)

## Enter search terms

After typing in the search field, matching shots will be highlighted in the
table below. You can click the yellow rectangles next to the scroll bar to jump
to the highlighted results.

![Highlighted Results](/static/search/highlight-results.png)

### Highlight / Filter

When the **Highlight** option is selected, matching shots will be highlighted in
the table below. If you select the **Filter** option above the table, it will
show only matching shots:

|                                                              |                                                        |
| ------------------------------------------------------------ | ------------------------------------------------------ |
| ![Highlighted Results](/static/search/highlight-results.png) | ![Filtered Results](/static/search/filter-results.png) |

## Fly to Results

Hit the `Enter` key while the search field is focused to fly to the matching
shots. Breakout will select the matching shots (highlighting them blue) and
fly to them in the 3D view:

![Fly to Results](/static/search/fly-to-results.png)
