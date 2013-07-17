package org.andork.torquescape.launcher;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.andork.torquescape.app.TorquescapeEditorFrame;

public class TorquescapeEditorLauncher
{
	public static void main( String[ ] args )
	{
		final TorquescapeEditorFrame frame = new TorquescapeEditorFrame( );
		frame.addWindowListener( new WindowAdapter( )
		{
			public void windowClosing( WindowEvent windowevent )
			{
				frame.dispose( );
				System.exit( 0 );
			}
		} );
		
		frame.setSize( 640 , 480 );
		frame.setVisible( true );
		frame.startAnimation( );
	}
}
