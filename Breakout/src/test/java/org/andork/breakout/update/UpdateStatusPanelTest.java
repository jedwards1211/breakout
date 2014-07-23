/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
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
