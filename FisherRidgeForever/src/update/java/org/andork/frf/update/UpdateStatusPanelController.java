package org.andork.frf.update;

import static org.andork.frf.update.UpdateStatus.CHECKING;
import static org.andork.frf.update.UpdateStatus.STARTING_DOWNLOAD;
import static org.andork.frf.update.UpdateStatus.UPDATE_AVAILABLE;
import static org.andork.frf.update.UpdateStatus.UPDATE_DOWNLOADED;
import static org.andork.frf.update.UpdateStatus.UP_TO_DATE;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.AbstractAction;

import org.andork.frf.update.UpdateStatus.CheckFailed;
import org.andork.frf.update.UpdateStatus.DownloadFailed;
import org.andork.frf.update.UpdateStatus.Downloading;
import org.andork.io.Downloader;
import org.andork.io.Downloader.State;
import org.andork.swing.DoSwing;

public class UpdateStatusPanelController
{
	private UpdateStatusPanel	panel;
	
	private File				dateFile;
	private URL					updateUrl;
	private File				destFile;
	
	private ExecutorService		executor;
	
	private Downloader			downloader;
	
	public UpdateStatusPanelController( UpdateStatusPanel panel , File dateFile , URL updateUrl , File destFile )
	{
		this( panel , createDefaultExecutor( ) , dateFile , updateUrl , destFile );
	}
	
	public UpdateStatusPanelController( UpdateStatusPanel panel , ExecutorService executor , File dateFile , URL updateUrl , File destFile )
	{
		this.panel = panel;
		this.dateFile = dateFile;
		this.updateUrl = updateUrl;
		this.destFile = destFile;
		this.executor = executor;
		
		panel.setDownloadAction( new DownloadAction( ) );
		panel.setCheckForUpdatesAction( new CheckForUpdatesAction( ) );
		panel.setCancelDownloadAction( new CancelDownloadAction( ) );
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
		return new ThreadPoolExecutor( 0 , 1 , 1000 , TimeUnit.MILLISECONDS , new LinkedBlockingDeque<Runnable>( ) , threadFactory );
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
	
	public void checkForUpdate( )
	{
		panel.setStatus( CHECKING );
		
		executor.submit( new Runnable( )
		{
			@Override
			public void run( )
			{
				UpdateStatus newStatus = null;
				try
				{
					URLConnection connection = updateUrl.openConnection( );
					connection.getInputStream( ).close( );
					if( !dateFile.exists( ) || dateFile.lastModified( ) < connection.getLastModified( ) )
					{
						newStatus = UPDATE_AVAILABLE;
					}
					else
					{
						newStatus = UP_TO_DATE;
					}
				}
				catch( final Exception ex )
				{
					ex.printStackTrace( );
					newStatus = new CheckFailed( formatException( ex ) );
				}
				
				final UpdateStatus finalNewStatus = newStatus;
				
				new DoSwing( )
				{
					@Override
					public void run( )
					{
						panel.setStatus( finalNewStatus );
					}
				};
			}
		} );
	}
	
	public void downloadUpdateIfAvailable( )
	{
		checkForUpdate( );
		downloadUpdate( true );
	}

	private void downloadUpdate( final boolean checkStatus )
	{
		final UpdateStatus prevStatus = panel.getStatus( );
		
		downloader = new Downloader( ).url( updateUrl ).destFile( destFile ).blockSize( 1024 );
		downloader.addPropertyChangeListener( new PropertyChangeListener( )
		{
			@Override
			public void propertyChange( PropertyChangeEvent evt )
			{
				if( downloader.getState( ) == State.COMPLETE )
				{
					if( !dateFile.exists( ) )
					{
						try
						{
							long lastModified = updateUrl.openConnection( ).getLastModified( );
							dateFile.createNewFile( );
							dateFile.setLastModified( lastModified );
						}
						catch( IOException e )
						{
							e.printStackTrace( );
						}
					}
				}
				
				new DoSwing( )
				{
					@Override
					public void run( )
					{
						if( downloader.getState( ) == State.DOWNLOADING )
						{
							panel.setStatus( new Downloading( downloader.getNumBytesDownloaded( ) , downloader.getTotalSize( ) ) );
						}
						else if( downloader.getState( ) == State.COMPLETE )
						{
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
					}
				};
			}
		} );
		
		executor.submit( new Runnable( )
		{
			@Override
			public void run( )
			{
				class StatusGetter extends DoSwing
				{
					UpdateStatus	status;
					
					public void run( )
					{
						status = panel.getStatus( );
					}
				}

				if( checkStatus && new StatusGetter( ).status != UPDATE_AVAILABLE )
				{
					return;
				}
				
				new DoSwing( )
				{
					@Override
					public void run( )
					{
						panel.setStatus( STARTING_DOWNLOAD );
					}
				};
				
				if( !destFile.getParentFile( ).exists( ) )
				{
					destFile.getParentFile( ).mkdirs( );
				}
				
				downloader.download( );
			}
		} );
	}
}
