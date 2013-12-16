package org.andork.torquescape.model;

public class CanonicalPointOrder
{
	public static int compare( float x1 , float y1 , float z1 , float x2 , float y2 , float z2 )
	{
		int result = Float.compare( x1 , x2 );
		if( result != 0 )
		{
			return result;
		}
		result = Float.compare( y1 , y2 );
		if( result != 0 )
		{
			return result;
		}
		return Float.compare( z1 , z2 );
	}
}
