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

import static org.andork.breakout.update.UpdateStatus.CHECKING;
import static org.andork.breakout.update.UpdateStatus.STARTING_DOWNLOAD;
import static org.andork.breakout.update.UpdateStatus.UPDATE_AVAILABLE;
import static org.andork.breakout.update.UpdateStatus.UPDATE_DOWNLOADED;
import static org.andork.breakout.update.UpdateStatus.UP_TO_DATE;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.andork.breakout.update.UpdateStatus.CheckFailed;
import org.andork.breakout.update.UpdateStatus.DownloadFailed;
import org.andork.breakout.update.UpdateStatus.Downloading;
import org.andork.io.Downloader;
import org.andork.io.Downloader.State;
import org.andork.swing.FromEDT;
import org.andork.swing.OnEDT;
import org.andork.util.Java7.Objects;

public class UpdateStatusPanelController
{
	private UpdateStatusPanel	panel;

	private String				currentVersion;
	private URL					latestVersionInfoUrl;
	private URL					latestVersionDownloadUrl;
	private File				downloadDir;
	private File				downloadPropsFile;

	private ExecutorService		executor;

	private Downloader			downloader;

	public UpdateStatusPanelController( UpdateStatusPanel panel , String currentVersion , URL latestVersionInfoUrl ,
		File downloadDir )
	{
		this( panel , createDefaultExecutor( ) , currentVersion , latestVersionInfoUrl ,
			downloadDir );
	}

	public UpdateStatusPanelController( UpdateStatusPanel panel , ExecutorService executor , String currentVersion ,
		URL latestVersionInfoUrl , File downloadDir )
	{
		this.panel = panel;
		this.currentVersion = currentVersion;
		this.latestVersionInfoUrl = latestVersionInfoUrl;
		this.downloadDir = downloadDir;
		this.downloadPropsFile = new File( downloadDir , "downloaded.properties" );
		this.executor = executor;

		panel.setDownloadAction( new DownloadAction( ) );
		panel.setCheckForUpdatesAction( new CheckForUpdatesAction( ) );
		panel.setCancelDownloadAction( new CancelDownloadAction( ) );
		panel.setInstallAction( new InstallAction( ) );
	}

	private static ExecutorService createDefaultExecutor( )
	{
		ThreadFactory threadFactory = new ThreadFactory( )
		{
			@Override
			public Thread newThread( Runnable r )
			{
				Thread thread = new Thread( r );
				thread.setDaemon( false );
				thread.setName( "UpdateStatusPanelController thread" );
				return thread;
			}
		};
		return new ThreadPoolExecutor( 0 , 1 , 1000 , TimeUnit.MILLISECONDS , new LinkedBlockingDeque<Runnable>( ) ,
			threadFactory );
	}

	@SuppressWarnings( "serial" )
	private class DownloadAction extends AbstractAction
	{
		@Override
		public void actionPerformed( ActionEvent e )
		{
			downloadUpdateIfAvailable( );
		}
	}

	private static String formatException( Exception ex )
	{
		return ex.getClass( ).getSimpleName( ) + ": " + ex.getLocalizedMessage( );
	}

	@SuppressWarnings( "serial" )
	private class CheckForUpdatesAction extends AbstractAction
	{
		@Override
		public void actionPerformed( ActionEvent e )
		{
			checkForUpdate( );
		}
	}

	@SuppressWarnings( "serial" )
	private class CancelDownloadAction extends AbstractAction
	{
		@Override
		public void actionPerformed( ActionEvent e )
		{
			if( downloader != null )
			{
				try
				{
					downloader.cancel( );
				}
				catch( Exception ex )
				{
					ex.printStackTrace( );
				}
			}
		}
	}

	@SuppressWarnings( "serial" )
	private class InstallAction extends AbstractAction
	{
		@Override
		public void actionPerformed( ActionEvent e )
		{
			panel.showInstallInstructionsDialog( );

			Desktop desktop = Desktop.getDesktop( );
			try
			{
				desktop.open( downloadDir );
			}
			catch( Exception ex )
			{
				ex.printStackTrace( );
			}
		}
	}

	public void checkForUpdate( )
	{
		OnEDT.onEDT( ( ) -> panel.setStatus( CHECKING ) );

		executor.submit( ( ) ->
		{
			UpdateStatus newStatus = null;
			String latestVersion = null;
			URLConnection connection = null;
			try
			{
				Properties props = new Properties( );

				connection = latestVersionInfoUrl.openConnection( );
				props.load( connection.getInputStream( ) );
				connection.getInputStream( ).close( );

				latestVersion = props.getProperty( "version" );
				latestVersionDownloadUrl = new URL( props.getProperty( "url" ) );

				if( VersionUtil.compareVersions( currentVersion , latestVersion ) < 0 )
				{
					newStatus = UPDATE_AVAILABLE;

					if( downloadPropsFile.exists( ) )
					{
						props = new Properties( );
						try( InputStream in = new FileInputStream( downloadPropsFile ) )
						{
							props.load( in );
							in.close( );

							String downloadedVersion = props.getProperty( "version" );
							if( Objects.equals( downloadedVersion , latestVersion ) )
							{
								newStatus = UPDATE_DOWNLOADED;
							}
						}
						catch( Exception ex )
						{
						}
					}
				}
				else if( latestVersion == null )
				{
					throw new Exception(
						"Breakout couldn't recognize the format of the latest version info file from the server." );
				}
				else
				{
					newStatus = UP_TO_DATE;
				}
			}
			catch( final Exception ex )
			{
				ex.printStackTrace( );
				if( connection != null )
				{
					try
					{
						connection.getInputStream( ).close( );
					}
					catch( final Exception ex2 )
					{
						ex2.printStackTrace( );
					}
				}
				newStatus = new CheckFailed( formatException( ex ) );
			}

			final String finalLatestVersion = latestVersion;
			final UpdateStatus finalNewStatus = newStatus;

			OnEDT.onEDT( ( ) ->
			{
				panel.setLatestVersion( finalLatestVersion );
				panel.setStatus( finalNewStatus );
			} );
		} );
	}

	public void downloadUpdateIfAvailable( )
	{
		checkForUpdate( );
		downloadUpdate( true );
	}

	private void downloadUpdate( final boolean checkStatus )
	{
		try
		{
			for( File file : downloadDir.listFiles( ) )
			{
				file.delete( );
			}
		}
		catch( Exception ex )
		{
			ex.printStackTrace( );
		}

		final UpdateStatus prevStatus = panel.getStatus( );

		String file = latestVersionDownloadUrl.getFile( );

		int i = file.lastIndexOf( '/' ) + 1;
		if( i > file.length( ) )
		{
			i = 0;
		}
		File destFile = new File( downloadDir , file.substring( i ) );

		downloader = new Downloader( ).url( latestVersionDownloadUrl ).destFile( destFile ).blockSize( 1024 );
		downloader.addPropertyChangeListener( new PropertyChangeListener( )
		{
			@Override
			public void propertyChange( PropertyChangeEvent evt )
			{
				OnEDT.onEDT( ( ) ->
				{
					if( downloader.getState( ) == State.DOWNLOADING )
					{
						panel.setStatus( new Downloading( downloader.getNumBytesDownloaded( ) , downloader
							.getTotalSize( ) ) );
					}
					else if( downloader.getState( ) == State.COMPLETE )
					{
						try( PrintStream ps = new PrintStream( new FileOutputStream( downloadPropsFile ) ) )
						{
							ps.print( "version=" + panel.getLatestVersion( ) );
							ps.close( );
						}
						catch( Exception ex )
						{
							ex.printStackTrace( );
						}
						panel.setStatus( UPDATE_DOWNLOADED );
					}
					else if( downloader.getState( ) == State.FAILED )
					{
						panel.setStatus( new DownloadFailed( formatException( downloader.getException( ) ) ) );
					}
					else if( downloader.getState( ) == State.CANCELED )
					{
						panel.setStatus( prevStatus );
					}
				} );
			}
		} );

		executor.submit( ( ) ->
		{
			if( checkStatus )
			{
				UpdateStatus status = FromEDT.fromEDT( ( ) -> panel.getStatus( ) );

				if( status != UPDATE_AVAILABLE )
				{
					return;
				}
			}

			OnEDT.onEDT( ( ) -> panel.setStatus( STARTING_DOWNLOAD ) );

			if( !downloadDir.exists( ) )
			{
				downloadDir.mkdirs( );
			}

			downloader.download( );
		} );
	}
}
