package org.andork.breakout;

import java.util.Comparator;

public class PickResult<T> implements Comparable<PickResult<T>>
{
	public final float[ ]					location			= new float[ 3 ];
	/**
	 * The distance of the pick location projected onto the pick ray (rather than the distance
	 * from the origin of the pick ray to the intersection point).
	 */
	public float							distance;
	/**
	 * The distance from the pick location to its projection on the pick ray.
	 */
	public float							lateralDistance;
	public T								picked;
	
	public static final DistanceComparator	DISTANCE_COMPARATOR	= new DistanceComparator( );
	
	public static class DistanceComparator implements Comparator<PickResult<?>>
	{
		@Override
		public int compare( PickResult<?> o1 , PickResult<?> o2 )
		{
			return Float.compare( o1.distance , o2.distance );
		}
	}
	
	@Override
	public int compareTo( PickResult<T> o )
	{
		return DISTANCE_COMPARATOR.compare( this , o );
	}
}