package org.andork.math.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Fitting
{
	/**
	 * @param points
	 *        a list of 2-element points
	 * @return a 2-element [slope, intercept] array
	 */
	public static float[ ] theilSen( List<float[ ]> points )
	{
		List<Float> slopes = new ArrayList<>( );
		
		for( int i = 0 ; i < points.size( ) ; i++ )
		{
			float[ ] p1 = points.get( i );
			for( int j = i + 1 ; j < points.size( ) ; j++ )
			{
				float[ ] p2 = points.get( j );
				slopes.add( ( p2[ 1 ] - p1[ 1 ] ) / ( p2[ 0 ] - p1[ 0 ] ) );
			}
		}
		
		Collections.sort( slopes );
		
		float[ ] result = new float[ 2 ];
		result[ 0 ] = slopes.get( slopes.size( ) / 2 );
		if( ( slopes.size( ) & 0x1 ) == 0 )
		{
			result[ 0 ] = 0.5f * ( result[ 0 ] + slopes.get( slopes.size( ) / 2 - 1 ) );
		}
		
		float[ ] intercepts = new float[ points.size( ) ];
		for( int i = 0 ; i < points.size( ) ; i++ )
		{
			float[ ] point = points.get( i );
			intercepts[ i ] = point[ 1 ] - result[ 0 ] * point[ 0 ];
		}
		
		Arrays.sort( intercepts );
		result[ 1 ] = intercepts[ points.size( ) / 2 ];
		if( ( points.size( ) & 0x1 ) == 0 )
		{
			result[ 1 ] = 0.5f * ( result[ 1 ] + intercepts[ points.size( ) / 2 - 1 ] );
		}
		
		return result;
	}
}
