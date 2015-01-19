package org.andork.breakout.wallsimport;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.andork.i18n.I18n.Localizer;

public class WallsState
{
	Stack<WallsUnits>						stack			= new Stack<WallsUnits>( );
	WallsUnits								units			= new WallsUnits( );
	String									globalComments	= "";
	String									localComments	= "";

	final List<WallsImportStatusMessage>	statusMessages	= new ArrayList<WallsImportStatusMessage>( );
	Localizer								localizer;
	Path									currentFilePath;
}
