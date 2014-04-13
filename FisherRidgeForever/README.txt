~~~ BREAKOUT pre-release version ~~~
      A cave survey viewer by Andy Edwards

To run, open breakout-<your platform>.jar.
Requires Java 1.6+.

GETTING STARTED

1. Drag and drop your survey file into the top-left cell of the survey table at the bottom (it is in an autohiding drawer, 
   move your mouse to the bottom of the window or click the "Survey Table" pin button to show it).  The survey file must be 
   in a tab-delimited format with column order, like this:
   
	From	To	Dist	Azm FS	Inc FS	Azm BS	Inc BS	Left	Right	Up	Down
	0	AE20	0	0	0	0	0	0	0	0	2
	AE20	AE19	9.425057029	58.38270779	-35.55110269	58.38270779	-35.55110269	11.99451958	1.992109435	0	20
	
2. The 3D model should be built automatically, and the table will be automatically saved.  The next time you run the program, it will
   reload the surveys saved from the table.
   
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

When the "Alphabetic Designation" filter type is selected in the dropdown in the settings drawer, you can enter detailed compound
searches like the following:
  MNE6+, AA30-50, AJ58
  
Assuming your survey names are in <letters/symbols><digits> format, it should be obvious which stations that will match.  
If your surveys are different let me know and I'll try to design a filter type for your survey naming conventions.
  
Any shot for which the filter matches the names of the from and to stations will be included in the filter results.

The "Regular Expression" filter type is provided primarily for programmers who are familiar with regular expressions.  Is uses Java
regular expressions on the survey names.  If you have questions about it, please don't ask me until you've read Java's documentation
here: <http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html>.  I don't want to spend time supporting advanced features. 