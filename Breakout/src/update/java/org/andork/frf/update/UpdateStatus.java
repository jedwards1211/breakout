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
