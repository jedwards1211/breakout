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
package org.andork.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class IterableUtils
{
	private IterableUtils( )
	{
		
	}
	
	public static <E> void addAll( Iterable<? extends E> iterable , Collection<E> collection )
	{
		for( E elem : iterable )
		{
			collection.add( elem );
		}
	}
	
	public static <E> ArrayList<E> toArrayList( Iterable<E> iterable )
	{
		ArrayList<E> result = new ArrayList<E>( );
		addAll( iterable , result );
		return result;
	}
	
	public static Iterable<Float> range( final float start , final float end , final boolean includeEnd , final float step )
	{
		return new Iterable<Float>( )
		{
			@Override
			public Iterator<Float> iterator( )
			{
				return new Iterator<Float>( )
				{
					float	next	= start;
					
					@Override
					public void remove( )
					{
						throw new UnsupportedOperationException( );
					}
					
					@Override
					public Float next( )
					{
						float result = next;
						
						if( includeEnd && next < end )
						{
							next = Math.min( next + step , end );
						}
						else
						{
							next += step;
						}
						
						return result;
					}
					
					@Override
					public boolean hasNext( )
					{
						return next < end || ( includeEnd && next == end );
					}
				};
			}
		};
	}
	
	public static <E> Iterable<E> iterable( final Iterator<E> iterator )
	{
		return new Iterable<E>( )
		{
			@Override
			public Iterator<E> iterator( )
			{
				return iterator;
			}
		};
	}
}