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
package org.andork.frf.update;

import static org.andork.frf.update.UpdateStatus.CHECKING;
import static org.andork.frf.update.UpdateStatus.STARTING_DOWNLOAD;
import static org.andork.frf.update.UpdateStatus.UNCHECKED;
import static org.andork.frf.update.UpdateStatus.UPDATE_AVAILABLE;
import static org.andork.frf.update.UpdateStatus.UPDATE_DOWNLOADED;
import static org.andork.frf.update.UpdateStatus.UP_TO_DATE;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.andork.awt.GridBagWizard;
import org.andork.awt.I18n;
import org.andork.awt.IconScaler;
import org.andork.awt.GridBagWizard.DefaultAutoInsets;
import org.andork.awt.I18n.Localizer;
import org.andork.frf.update.UpdateStatus.CheckFailed;
import org.andork.frf.update.UpdateStatus.DownloadFailed;
import org.andork.frf.update.UpdateStatus.Downloading;
import org.andork.frf.update.UpdateStatus.UpdateFailed;
import org.jdesktop.swingx.JXHyperlink;

@SuppressWarnings( "serial" )
public class UpdateStatusPanel extends JPanel
{
	private UpdateStatus	status;
	
	private JLabel			messageLabel;
	private JProgressBar	downloadProgressBar;
	private JXHyperlink		downloadHyperlink;
	private JXHyperlink		checkForUpdatesHyperlink;
	private JXHyperlink		detailsHyperlink;
	private JXHyperlink		cancelDownloadHyperlink;
	
	private Localizer		localizer;
	
	private Icon			infoIcon;
	private Icon			errorIcon;
	
	public UpdateStatusPanel( I18n i18n )
	{
		if( i18n == null )
		{
			i18n = new I18n( );
		}
		localizer = i18n.forClass( UpdateStatusPanel.class );
		init( );
		modelToView( );
	}
	
	private void init( )
	{
		messageLabel = new JLabel( );
		downloadProgressBar = new JProgressBar( );
		downloadProgressBar.setPreferredSize( new Dimension( 75 , 15 ) );
		downloadProgressBar.setMinimumSize( downloadProgressBar.getPreferredSize( ) );
		downloadHyperlink = new JXHyperlink( );
		checkForUpdatesHyperlink = new JXHyperlink( );
		localizer.setText( checkForUpdatesHyperlink , "Check for update..." );
		detailsHyperlink = new JXHyperlink( );
		detailsHyperlink.setAction( new DetailsAction( ) );
		localizer.setText( detailsHyperlink , "Details" );
		cancelDownloadHyperlink = new JXHyperlink( );
		localizer.setText( cancelDownloadHyperlink , "Cancel" );
		
		GridBagWizard g = GridBagWizard.create( this );
		g.defaults( ).autoinsets( new DefaultAutoInsets( 5 , 2 ) );
		g.put( messageLabel , downloadHyperlink , detailsHyperlink , checkForUpdatesHyperlink , cancelDownloadHyperlink , downloadProgressBar ).intoRow( );
		g.put( downloadProgressBar ).fillx( 1.0 );
		
		infoIcon = IconScaler.rescale( UIManager.getIcon( "OptionPane.informationIcon" ) , 1000 , 20 );
		errorIcon = IconScaler.rescale( UIManager.getIcon( "OptionPane.errorIcon" ) , 1000 , 20 );
	}
	
	public UpdateStatus getStatus( )
	{
		return status;
	}
	
	public void setStatus( UpdateStatus newStatus )
	{
		if( status != newStatus )
		{
			status = newStatus;
			modelToView( );
		}
	}
	
	public void setDownloadAction( Action action )
	{
		String text = downloadHyperlink.getText( );
		downloadHyperlink.setAction( action );
		downloadHyperlink.setText( text );
	}
	
	public void setCheckForUpdatesAction( Action action )
	{
		String text = checkForUpdatesHyperlink.getText( );
		checkForUpdatesHyperlink.setAction( action );
		checkForUpdatesHyperlink.setText( text );
	}
	
	public void setCancelDownloadAction( Action action )
	{
		String text = cancelDownloadHyperlink.getText( );
		cancelDownloadHyperlink.setAction( action );
		cancelDownloadHyperlink.setText( text );
	}
	
	private void modelToView( )
	{
		downloadProgressBar.setVisible( status == CHECKING || status == STARTING_DOWNLOAD || status instanceof Downloading );
		downloadProgressBar.setIndeterminate( status == CHECKING || status == STARTING_DOWNLOAD );
		detailsHyperlink.setVisible( status instanceof UpdateFailed || status instanceof CheckFailed || status instanceof DownloadFailed );
		downloadHyperlink.setVisible( status == UPDATE_AVAILABLE || status instanceof DownloadFailed );
		checkForUpdatesHyperlink.setVisible( status == null || status == UNCHECKED || status instanceof CheckFailed || status == UP_TO_DATE || status instanceof UpdateFailed );
		cancelDownloadHyperlink.setVisible( status instanceof Downloading || status == STARTING_DOWNLOAD );
		
		if( status == null || status == UNCHECKED )
		{
			messageLabel.setText( null );
			messageLabel.setIcon( null );
		}
		else if( status == CHECKING )
		{
			localizer.setText( messageLabel , "Checking for update..." );
			messageLabel.setIcon( null );
		}
		else if( status == UPDATE_AVAILABLE )
		{
			localizer.setText( messageLabel , "An update is available." );
			localizer.setText( downloadHyperlink , "Download Now" );
			messageLabel.setIcon( infoIcon );
		}
		else if( status == UP_TO_DATE )
		{
			localizer.setText( messageLabel , "Software up-to-date." );
			messageLabel.setIcon( null );
		}
		else if( status == UPDATE_DOWNLOADED )
		{
			localizer.setText( messageLabel , "Update will be installed when you restart." );
			messageLabel.setIcon( infoIcon );
		}
		else if( status instanceof Downloading )
		{
			localizer.setText( messageLabel , "Downloading update..." );
			messageLabel.setIcon( null );
			downloadProgressBar.setMaximum( ( int ) ( ( Downloading ) status ).totalNumBytes );
			downloadProgressBar.setValue( ( int ) ( ( Downloading ) status ).numBytesDownloaded );
		}
		else if( status instanceof CheckFailed )
		{
			localizer.setText( messageLabel , "Update check failed." );
			messageLabel.setIcon( errorIcon );
		}
		else if( status instanceof DownloadFailed )
		{
			localizer.setText( messageLabel , "Download failed." );
			messageLabel.setIcon( errorIcon );
			localizer.setText( downloadHyperlink , "Retry" );
		}
		else if( status instanceof UpdateFailed )
		{
			localizer.setText( messageLabel , "Update failed." );
			messageLabel.setIcon( errorIcon );
		}
	}
	
	private class DetailsAction extends AbstractAction
	{
		public DetailsAction( )
		{
			super( "Details..." );
		}
		
		@Override
		public void actionPerformed( ActionEvent e )
		{
			if( status instanceof DownloadFailed )
			{
				JOptionPane.showMessageDialog( SwingUtilities.getWindowAncestor( UpdateStatusPanel.this ) ,
						( ( DownloadFailed ) status ).message , localizer.getString( "Download failed" ) , JOptionPane.ERROR_MESSAGE );
			}
			else if( status instanceof UpdateFailed )
			{
				JOptionPane.showMessageDialog( SwingUtilities.getWindowAncestor( UpdateStatusPanel.this ) ,
						( ( UpdateFailed ) status ).message , localizer.getString( "Update failed" ) , JOptionPane.ERROR_MESSAGE );
			}
			else if( status instanceof CheckFailed )
			{
				JOptionPane.showMessageDialog( SwingUtilities.getWindowAncestor( UpdateStatusPanel.this ) ,
						( ( CheckFailed ) status ).message , localizer.getString( "Update check failed" ) , JOptionPane.ERROR_MESSAGE );
			}
		}
	}
}
