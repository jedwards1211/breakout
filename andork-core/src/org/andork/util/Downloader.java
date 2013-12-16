package org.andork.util;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

public class Downloader
{
	public static final String			STATE					= "state";
	public static final String			TOTAL_SIZE				= "totalSize";
	public static final String			NUM_BYTES_DOWNLOADED	= "numBytesDownloaded";
	
	private URL							url;
	private File						destFile;
	private long						blockSize				= 1024;
	
	private State						state					= State.NOT_DOWNLOADING;
	private long						totalSize;
	private long						numBytesDownloaded;
	
	private IOException					exception;
	
	private final PropertyChangeSupport	propertyChangeSupport	= new PropertyChangeSupport( this );
	
	private final Object				lock					= new Object( );
	
	public static enum State
	{
		NOT_DOWNLOADING , DOWNLOADING , CANCELED , FAILED , COMPLETE;
	}
	
	public Downloader url( URL url )
	{
		this.url = url;
		return this;
	}
	
	public Downloader destFile( File destFile )
	{
		this.destFile = destFile;
		return this;
	}
	
	public Downloader blockSize( long blockSize )
	{
		if( blockSize <= 0 )
		{
			throw new IllegalArgumentException( "blockSize must be > 0" );
		}
		this.blockSize = blockSize;
		return this;
	}
	
	public Downloader addPropertyChangeListener( PropertyChangeListener listener )
	{
		propertyChangeSupport.addPropertyChangeListener( listener );
		return this;
	}
	
	public Downloader removePropertyChangeListener( PropertyChangeListener listener )
	{
		propertyChangeSupport.removePropertyChangeListener( listener );
		return this;
	}
	
	public void download( )
	{
		if( url == null )
		{
			throw new IllegalStateException( "missing url" );
		}
		if( destFile == null )
		{
			throw new IllegalStateException( "missing destFile" );
		}
		
		InputStream in = null;
		FileOutputStream out = null;
		
		setState( State.DOWNLOADING );
		
		try
		{
			URLConnection conn = url.openConnection( );
			long totalSize = conn.getContentLength( );
			in = conn.getInputStream( );
			ReadableByteChannel rbc = Channels.newChannel( in );
			out = new FileOutputStream( destFile );
			FileChannel fc = out.getChannel( );
			
			setTotalSize( totalSize );
			
			long position = 0;
			long numBytesTransferred;
			do
			{
				numBytesTransferred = fc.transferFrom( rbc , position , blockSize );
				position += numBytesTransferred;
				setNumBytesDownloaded( numBytesDownloaded + numBytesTransferred );
			} while( getState( ) != State.CANCELED && numBytesTransferred > 0 );
			
			if( getState( ) != State.CANCELED )
			{
				setState( State.COMPLETE );
			}
		}
		catch( IOException ex )
		{
			if( getState( ) != State.CANCELED )
			{
				downloadFailed( ex );
			}
		}
		finally
		{
			if( out != null )
			{
				try
				{
					out.close( );
				}
				catch( Exception ex )
				{
					ex.printStackTrace( );
				}
			}
			if( in != null )
			{
				try
				{
					in.close( );
				}
				catch( Exception ex )
				{
					ex.printStackTrace( );
				}
			}
		}
	}
	
	public void cancel( )
	{
		setState( State.CANCELED );
	}
	
	public State getState( )
	{
		synchronized( lock )
		{
			return state;
		}
	}
	
	public long getTotalSize( )
	{
		synchronized( lock )
		{
			return totalSize;
		}
	}
	
	public long getNumBytesDownloaded( )
	{
		synchronized( lock )
		{
			return numBytesDownloaded;
		}
	}
	
	public IOException getException( )
	{
		synchronized( lock )
		{
			return exception;
		}
	}
	
	private void setState( State newValue )
	{
		State oldValue;
		synchronized( lock )
		{
			oldValue = state;
			if( newValue == State.DOWNLOADING && oldValue != State.NOT_DOWNLOADING )
			{
				throw new IllegalArgumentException( "Already downloading" );
			}
			if( newValue == State.CANCELED && oldValue == State.NOT_DOWNLOADING )
			{
				throw new IllegalArgumentException( "Can't cancel before download has started" );
			}
			if( oldValue != newValue )
			{
				state = newValue;
				lock.notifyAll( );
			}
		}
		if( oldValue != newValue )
		{
			propertyChangeSupport.firePropertyChange( STATE , oldValue , newValue );
		}
	}
	
	private void setTotalSize( long newValue )
	{
		long oldValue;
		synchronized( lock )
		{
			oldValue = totalSize;
			if( oldValue != newValue )
			{
				totalSize = newValue;
			}
		}
		if( oldValue != newValue )
		{
			propertyChangeSupport.firePropertyChange( TOTAL_SIZE , oldValue , newValue );
		}
	}
	
	private void setNumBytesDownloaded( long newValue )
	{
		long oldValue;
		synchronized( lock )
		{
			oldValue = numBytesDownloaded;
			if( oldValue != newValue )
			{
				numBytesDownloaded = newValue;
			}
		}
		if( oldValue != newValue )
		{
			propertyChangeSupport.firePropertyChange( NUM_BYTES_DOWNLOADED , oldValue , newValue );
		}
	}
	
	private void downloadFailed( IOException ex )
	{
		synchronized( lock )
		{
			if( exception == null )
			{
				exception = ex;
			}
		}
		setState( State.FAILED );
	}
}
