package org.andork.math3d.curve;

public class BSplineUtils
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
	
}
