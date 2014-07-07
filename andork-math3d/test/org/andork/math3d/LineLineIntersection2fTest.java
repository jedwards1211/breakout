package org.andork.math3d;

import java.util.Arrays;

public class LineLineIntersection2fTest
{
	public static void main( String[ ] args )
	{
		System.out.println( Math.floor( Math.log10( 20.35 ) ) );
		System.out.println( Math.floor( Math.log10( 0.0053 ) ) );
		
		float[ ] m = { 1 , -1 , -5 , -.5f , 0 , 5 , 0 , 1 };
		//		float[ ] m = { -1 , 1 , -.5f , -5f , 5 , 0 , 1 , 0 };
		
		System.out.println( Vecmath.prettyPrint( m , 4 ) );
		
		int[ ] row_perms = new int[ 2 ];
		Vecmath.gauss( m , 2 , 4 , row_perms );
		
		System.out.println( Arrays.toString( row_perms ) );
		
		System.out.println( Vecmath.prettyPrint( m , 4 , row_perms ) );
		
		LineLineIntersection2f llx = new LineLineIntersection2f( );
		llx.setUp( new float[ ] { 0 , 5 } , new float[ ] { 1 , -1 } , new float[ ] { 0 , -1 } , new float[ ] { 5 , .5f } );
		llx.findIntersection( );
		
		System.out.println( llx.t0 );
		System.out.println( llx.t1 );
		
		System.out.println( Arrays.toString( llx.x ) );
	}
}
