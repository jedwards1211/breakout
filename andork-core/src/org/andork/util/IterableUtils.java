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