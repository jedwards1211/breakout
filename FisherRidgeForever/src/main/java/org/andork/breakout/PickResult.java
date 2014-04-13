package org.andork.breakout;

import java.util.Comparator;

public class PickResult<T> implements Comparable<PickResult<T>>
{
	public final float[ ]					location			= new float[ 3 ];
	public float							distance;
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