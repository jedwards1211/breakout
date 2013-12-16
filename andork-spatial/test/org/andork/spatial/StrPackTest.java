package org.andork.spatial;

import java.util.Random;

import org.junit.Test;

public class StrPackTest
{
	public static double[ ] randomRect( double[ ] bounds , Random random )
	{
		int d = bounds.length / 2;
		double[ ] result = new double[ bounds.length ];
		for( int i = 0 ; i < d ; i++ )
		{
			int j = i + d;
			result[ i ] = bounds[ i ] + random.nextDouble( ) * ( bounds[ j ] - bounds[ i ] );
			result[ j ] = bounds[ i ] + random.nextDouble( ) * ( bounds[ j ] - bounds[ i ] );
			if( result[ i ] > result[ j ] )
			{
				double swap = result[ i ];
				result[ i ] = result[ j ];
				result[ j ] = swap;
			}
		}
		return result;
	}
	
	public static double[ ] randomRect( double[ ] bounds , double[ ] minSize , double[ ] maxSize , Random random )
	{
		int d = bounds.length / 2;
		double[ ] result = new double[ bounds.length ];
		for( int i = 0 ; i < d ; i++ )
		{
			int j = i + d;
			double size = minSize[ i ] + random.nextDouble( ) * ( maxSize[ i ] - minSize[ i ] );
			double center = bounds[ i ] + size / 2.0 + random.nextDouble( ) * ( bounds[ j ] - bounds[ i ] - size );
			result[ i ] = center - size / 2.0;
			result[ j ] = center + size / 2.0;
		}
		return result;
	}
	
	@Test
	public void test001( )
	{
		Random random = new Random( );
		random.setSeed( 2L );
		
		double[ ] bounds = { 0 , 0 , 10 , 10 };
		
		double[ ] minSize = { 0.5 , 0.5 };
		double[ ] maxSize = { 1.5 , 1.5 };
		
		int nLeaves = 30;
		RdLeaf<Integer>[ ] leaves = new RdLeaf[ nLeaves ];
		for( int i = 0 ; i < nLeaves ; i++ )
		{
			leaves[ i ] = new DefaultRdLeaf<Integer>( i , randomRect( bounds , minSize , maxSize , random ) );
		}
		
		int n = 10;
		
		RdNode<Integer> root = StrPack.pack( n , leaves );
		
		System.out.println( RTrees.dump( root , "%5.2f" ) );
	}
}
