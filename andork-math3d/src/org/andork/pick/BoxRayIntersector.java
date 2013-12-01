package org.andork.pick;

import org.andork.spatial.Rectmath;


public class BoxRayIntersector
{
	final int[ ]	side	= new int[ 3 ];
	final float[ ]	v1		= new float[ 3 ];
	final float[ ]	v2		= new float[ 3 ];
	
	public static void main( String[ ] args )
	{
		float[ ] r = { 0 , 0 , 0 , 1 , 1 , 1 };
		
		float[ ] rayOrigin = { 2 , 0.5f , -1 };
		float[ ] rayDirection = { -1 , 0.26f , 1f };
		
		System.out.println( Rectmath.rayIntersects( rayOrigin , rayDirection , r ) );
	}
}
