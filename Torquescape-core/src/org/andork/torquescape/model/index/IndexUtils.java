package org.andork.torquescape.model.index;

public class IndexUtils
{
	public static char[ ] generateDefaultIndices( char npoints )
	{
		char[ ] result = new char[ npoints * 6 ];
		
		char k = 0;
		for( char i = 0 ; i < npoints ; i++ )
		{
			char next = ( char ) ( ( i + 1 ) % npoints );
			char beyond = ( char ) ( i + npoints );
			char greatBeyond = ( char ) ( next + npoints );
			result[ k++ ] = i;
			result[ k++ ] = next;
			result[ k++ ] = beyond;
			result[ k++ ] = greatBeyond;
			result[ k++ ] = beyond;
			result[ k++ ] = next;
		}
		
		return result;
	}
}
