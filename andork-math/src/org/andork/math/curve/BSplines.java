package org.andork.math.curve;

public class BSplines
{
	/**
	 * A modified binary search that's easier to use for B-Splines.
	 * 
	 * @return the index {@code i} in {@code [fromIndex, toIndex)} such that {@code key} is in {@code [a[i], a[i+1])}. If no such {@code i} exists, returns
	 *         {@code -1}.
	 */
	public static int binarySearch( float[ ] a , int fromIndex , int toIndex , float key )
	{
		int low = fromIndex;
		int high = toIndex - 1;
		
		while( low + 1 < high )
		{
			int mid = ( low + high ) >>> 1;
			float midVal = a[ mid ];
			
			if( midVal <= key )
			{
				low = mid;
			}
			else
			{
				high = mid;
			}
		}
		return key >= a[ low ] && key < a[ high ] ? low : -1;
	}
	
	public static float[ ] createUniformKnots( int degree , int numControlPoints )
	{
		float[ ] knots = new float[ degree + numControlPoints + 1 ];
		
		for( int i = 0 ; i < degree ; i++ )
		{
			knots[ i ] = 0;
			knots[ knots.length - i - 1 ] = 1;
		}
		
		for( int i = degree ; i <= numControlPoints ; i++ )
		{
			knots[ i ] = ( float ) ( i - degree ) / ( numControlPoints - degree );
		}
		
		return knots;
	}
}
