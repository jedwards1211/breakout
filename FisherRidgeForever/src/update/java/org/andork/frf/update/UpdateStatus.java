package org.andork.frf.update;

public class UpdateStatus
{
	private UpdateStatus( )
	{
		
	}
	
	public static final UpdateStatus	UNCHECKED			= new UpdateStatus( );
	public static final UpdateStatus	CHECKING			= new UpdateStatus( );
	public static final UpdateStatus	UP_TO_DATE			= new UpdateStatus( );
	public static final UpdateStatus	UPDATE_AVAILABLE	= new UpdateStatus( );
	public static final UpdateStatus	STARTING_DOWNLOAD	= new UpdateStatus( );
	
	public static abstract class Failure extends UpdateStatus
	{
		public final String	message;
		
		public Failure( String message )
		{
			this.message = message;
		}
	}
	
	public static final class CheckFailed extends Failure
	{
		public CheckFailed( String message )
		{
			super( message );
		}
	}
	
	public static final class Downloading extends UpdateStatus
	{
		public final long	numBytesDownloaded;
		public final long	totalNumBytes;
		
		public Downloading( long numBytesDownloaded , long totalNumBytes )
		{
			super( );
			this.numBytesDownloaded = numBytesDownloaded;
			this.totalNumBytes = totalNumBytes;
		}
	}
	
	public static final class DownloadFailed extends Failure
	{
		public DownloadFailed( String message )
		{
			super( message );
		}
	}
	
	public static final UpdateStatus	UPDATE_DOWNLOADED	= new UpdateStatus( );
	
	public static final class UpdateFailed extends Failure
	{
		public UpdateFailed( String message )
		{
			super( message );
		}
	}
}
