# Import Compass Data

## Create Plot File in Compass

First you must create a Plot file with Compass, so that Breakout
can use the station positions calculated by Compass loop closure.

Compass seems this file automatically when you click **Process and View Cave**:

![Process and View Cave](/static/compass-import/process-and-view-cave.png)

If for whatever Compass doesn't save a `.plt` file with the same name as your
`.mak` project file, you can create a `.plt` file by going to
**File > Save Plot File...** in the viewer:

![Save Plot File](/static/compass-import/save-plot-file.png)

## Import data into Breakout

Now in Breakout, go to **File > Import > Import Compass data...**

![Import Compass menu](/static/compass-import/import-compass-menu.png)

Breakout will ask you to select the files to import. Select both your `.mak`
project file and the `.plt` file saved in the last step. (To select the second
file, hold Ctrl and click on Windows/Linux or ⌘ and click on Mac OS):

![Select Compass files](/static/compass-import/select-compass-files.png)

Click **OK**. Breakout will show a progress bar while reading the Walls files.
Once it is done, the a results dialog will appear, showing any errors and
warnings about the imported data.

![Import Compass results](/static/compass-import/import-warnings.png)

You can click the **Data** tab to see the shots Breakout imported:

![Import Walls data](/static/compass-import/import-data.png)

It's also good to verify that station positions were imported by selecting the
**NEV** option in the **Data** tab. _Any stations for which the **Northing**,
**Easting**, and **Elevation** columns are blank will have their position
computed by Breakout using very basic calculations without loop closure._

![Import Walls NEV](/static/compass-import/import-nev.png)

If you are satisfied with the imported data, click **Add to Current Project**.
You will see a progress bar at the top of the window that indicates it is
parsing the survey data and creating the 3D model. Once it is finished, if
everything goes well, the data should appear in the 3D view.

## Save Breakout File

If you save the imported data in Breakout's format, it will be easier to reopen
next time you launch the program. Go to **File > Save** and choose a file in the
Save dialog. This file will then appear in the **File > Recent Files** list for
quick access.
