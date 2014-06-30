package org.andork.math3d;

public class RayIntersectsBoxTester
{

	public static boolean rayIntersects( float[ ] rayOrigin , float[ ] rayDirection , float[ ] rect )
	{
		for( int d = 0 ; d < 3 ; d++ )
		{
			if( ( rayOrigin[ d ] <= rect[ d ] && rayDirection[ d ] < 0 ) || ( rayOrigin[ d ] >= rect[ d + 3 ] && rayDirection[ d ] > 0 ) )
			{
				return false;
			}
		}
		
		for( int d0 = 0 ; d0 < 3 ; d0++ )
		{
			if( rayDirection[ d0 ] == 0 )
			{
				if( rayOrigin[ d0 ] < rect[ d0 ] || rayOrigin[ d0 ] > rect[ d0 + 3 ] )
				{
					return false;
				}
				continue;
			}
			
			float l0;
			
			if( rayOrigin[ d0 ] <= rect[ d0 ] )
			{
				l0 = rect[ d0 ] - rayOrigin[ d0 ];
			}
			else if( rayOrigin[ d0 ] >= rect[ d0 + 3 ] )
			{
				l0 = rect[ d0 + 3 ] - rayOrigin[ d0 ];
			}
			else
			{
				continue;
			}
			
			for( int i = 1 ; i < 3 ; i++ )
			{
				int d1 = ( d0 + i ) % 3;
				float l1 = rayDirection[ d1 ] * l0 / rayDirection[ d0 ];
				if( ( rayOrigin[ d1 ] <= rect[ d1 + 3 ] && rayOrigin[ d1 ] + l1 > rect[ d1 + 3 ] ) ||
						( rayOrigin[ d1 ] >= rect[ d1 ] && rayOrigin[ d1 ] + l1 < rect[ d1 ] ) )
				{
					return false;
				}
			}
		}
		
		return true;
	}
	
}
