~~~ FISHER RIDGE FOREVER pre-release version ~~~
      A cave survey viewer by Andy Edwards

To run, open FisherRidgeForever-<your platform>.jar.
Requires Java 1.6+.

GETTING STARTED

1. Drag and drop your survey file into the top-left cell of the survey table at the bottom (it is in an autohiding drawer, 
   move your mouse to the bottom of the window or click the "Survey Table" pin button to show it).  The survey file must be 
   in a tab-delimited format with column order, like this:
   
	From	To	Dist	Azm FS	Inc FS	Azm BS	Inc BS	Left	Right	Up	Down
	0	AE20	0	0	0	0	0	0	0	0	2
	AE20	AE19	9.425057029	58.38270779	-35.55110269	58.38270779	-35.55110269	11.99451958	1.992109435	0	20
	
2. Open the settings drawer on the right side of the window and click the "Update View"
   button.  This builds the 3D model from the data in the survey table.
   
COLORATION

The depth coloration, fade out, and mouseover highlight coloration can be adjusted by left and right clicking the coloration axes in
the settings drawer on the right side of the window.  Experiment to see how they behave.
   
NAVIGATION

Move mouse over a survey shot: highlights that survey shot and connected shots up to a certain distance away from it (this distance is
controlled by the yellow highlight axis in the settings drawer on the right side of the window).

Left Click + Drag:
	Up/Down: move forward and backward horizontally
	Left/Right: pan
	
Shift + Left Click + Drag:
	Up/Down: tilt
	Left/Right: pan

Left Click on a survey shot: selects that shot (deselects everything else)

Ctrl + Left Click on a survey shot: toggles whether that shot is selected, without deselecting anything else

Ctrl + Left Click + Drag: orbit around the selected survey shots

Middle Click + Drag: zoom in/out (move forward/back relative to the current view orientation)

Mouse Wheel: zoom in/out (duh ;)
	
Right Click + Drag: move up/down/left/right relative to the current view orientation

SEARCHING

To immediately select and fly to all surveys starting with the designation ALP, type "ALP" into the "Filter:" field in the survey table
or mini survey table (which is in the drawer on the left side of the window) and press enter.