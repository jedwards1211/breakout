package org.andork.jogl;

import java.nio.FloatBuffer;

public class Dumps
{
	public static void dumpBuffer( FloatBuffer buffer , String elemFormat , int elemsPerLine )
	{
		buffer.position( 0 );
		
		int k = 0;
		while( k < buffer.capacity( ) )
		{
			for( int i = 0 ; i < elemsPerLine && k < buffer.capacity( ) ; i++ , k++ )
			{
				System.out.format( elemFormat , buffer.get( k ) );
			}
			System.out.println( );
		}
	}
}
