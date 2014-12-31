package org.andork.util;

public class VersionUtil
{
	public static int compareVersions( String a , String b )
	{
		String[ ] aParts = a.split( "\\." );
		String[ ] bParts = b.split( "\\." );

		for( int i = 0 ; i < Math.min( aParts.length , bParts.length ) ; i++ )
		{
			int aNum = Integer.parseInt( aParts[ i ] );
			int bNum = Integer.parseInt( bParts[ i ] );
			if( aNum != bNum )
			{
				return aNum - bNum;
			}
		}

		return aParts.length - bParts.length;
	}
}
