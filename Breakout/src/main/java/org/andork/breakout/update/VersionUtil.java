package org.andork.breakout.update;

public class VersionUtil
{
	public static int compareVersions( String a , String b )
	{
		String[ ] aParts = a.split( "\\." );
		String[ ] bParts = b.split( "\\." );

		for( int i = 0 ; i < Math.min( aParts.length , bParts.length ) ; i++ )
		{
			int c = aParts[ i ].compareTo( bParts[ i ] );
			if( c != 0 )
			{
				return c;
			}
		}

		return aParts.length - bParts.length;
	}
}
