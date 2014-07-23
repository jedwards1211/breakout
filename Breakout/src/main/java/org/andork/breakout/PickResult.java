/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
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