package org.andork.breakout.update;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.SwingUtilities;

import org.andork.awt.I18n;
import org.andork.frf.update.UpdateStatus;
import org.andork.frf.update.UpdateStatusPanel;
import org.andork.frf.update.UpdateStatusPanelController;
import org.andork.swing.QuickTestFrame;

public class UpdateStatusPanelTest
{
	public static void main( String[ ] args )
	{
		SwingUtilities.invokeLater( new Runnable( )
		{
			@Override
			public void run( )
			{
				final UpdateStatusPanel panel = new UpdateStatusPanel( new I18n( ) );
				panel.setStatus( UpdateStatus.UNCHECKED );
				try
				{
					UpdateStatusPanelController controller = new UpdateStatusPanelController( panel , new File( "version.txt" ) , new URL( "http://andork.com/index.html" ) , new File( "downloaded.txt" ) );
				}
				catch( MalformedURLException e )
				{
					e.printStackTrace();
				}
				QuickTestFrame.frame( panel ).setVisible( true );
			}
		} );
	}
}
