package org.andork.frf.update;

import java.io.File;

public class Updater
{
	public static void main( String[ ] args )
	{
		if( args.length != 4 )
		{
			return;
		}
		
		File waitFile = new File( args[ 0 ] );
		File installDir = new File( args[ 1 ] );
		File updateFile = new File( args[ 2 ] );
		String postCommand = args[ 3 ];
		
		long startTime = System.currentTimeMillis( );
		
		while( waitFile.exists( ) )
		{
			try
			{
				Thread.sleep( 1000 );
			}
			catch( InterruptedException e )
			{
				e.printStackTrace( );
			}
			
			if( System.currentTimeMillis( ) - startTime > 10000 )
			{
				return;
			}
		}
	}
}
