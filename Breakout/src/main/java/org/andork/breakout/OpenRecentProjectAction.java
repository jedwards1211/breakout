package org.andork.breakout;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;

@SuppressWarnings( "serial" )
public class OpenRecentProjectAction extends AbstractAction
{
	BreakoutMainView	mainView;
	File				recentProjectFile;
	
	public OpenRecentProjectAction( final BreakoutMainView mainView , File recentProjectFile )
	{
		super( );
		this.mainView = mainView;
		this.recentProjectFile = recentProjectFile;
		
		putValue( NAME , recentProjectFile.toString( ) );
	}
	
	@Override
	public void actionPerformed( ActionEvent e )
	{
		mainView.openProject( recentProjectFile );
	}
}
