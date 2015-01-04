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
package org.andork.q2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public abstract class QCollection<E, C extends Collection<E>> extends QElement implements Collection<E>
{
	protected final C	collection	= createCollection( );

	protected abstract C createCollection( );

	public boolean add( E element )
	{
		if( collection.add( element ) )
		{
			handleChildAdded( element );
			return true;
		}
		return false;
	}

	@Override
	public boolean addAll( Collection<? extends E> c )
	{
		List<E> added = new ArrayList<E>( );
		for( E e : c )
		{
			if( collection.add( e ) )
			{
				added.add( e );
			}
		}
		if( !added.isEmpty( ) )
		{
			handleChildrenAdded( this , added.toArray( ) );
		}
		return !added.isEmpty( );
	}

	public void clear( )
	{
		if( !isEmpty( ) )
		{
			Object[ ] oldChildren = toArray( );
			collection.clear( );
			handleChildrenRemoved( oldChildren );
		}
	}

	@Override
	public boolean contains( Object o )
	{
		return collection.contains( o );
	}

	@Override
	public boolean containsAll( Collection<?> c )
	{
		return collection.containsAll( c );
	}

	public boolean isEmpty( )
	{
		return collection.isEmpty( );
	}

	public Iterator<E> iterator( )
	{
		return new Iter( collection );
	}

	public boolean remove( Object element )
	{
		if( collection.remove( element ) )
		{
			handleChildRemoved( element );
			return true;
		}
		return false;
	}

	@Override
	public boolean removeAll( Collection<?> c )
	{
		List<Object> removed = new ArrayList<Object>( );
		for( Object e : c )
		{
			if( collection.remove( e ) )
			{
				removed.add( e );
			}
		}
		if( !removed.isEmpty( ) )
		{
			handleChildrenRemoved( this , removed.toArray( ) );
		}
		return !removed.isEmpty( );
	}

	@Override
	public boolean retainAll( Collection<?> c )
	{
		List<Object> removed = new ArrayList<Object>( );
		Iter iter = ( Iter ) iterator( );
		while( iter.hasNext( ) )
		{
			E e = iter.next( );
			if( !c.contains( e ) )
			{
				iter.removeWithoutSideEffects( );
				removed.add( e );
			}
		}
		if( !removed.isEmpty( ) )
		{
			handleChildrenRemoved( this , removed.toArray( ) );
		}
		return !removed.isEmpty( );
	}

	public int size( )
	{
		return collection.size( );
	}

	@Override
	public Object[ ] toArray( )
	{
		return collection.toArray( );
	}

	@Override
	public <T> T[ ] toArray( T[ ] a )
	{
		return collection.toArray( a );
	}

	protected class Iter implements Iterator<E>
	{
		Iterator<E>	wrapped;
		E			last;

		public Iter( Collection<E> collection )
		{
			wrapped = collection.iterator( );
		}

		@Override
		public boolean hasNext( )
		{
			return wrapped.hasNext( );
		}

		@Override
		public E next( )
		{
			return last = wrapped.next( );
		}

		@Override
		public void remove( )
		{
			wrapped.remove( );

			handleChildRemoved( last );
		}

		void removeWithoutSideEffects( )
		{
			wrapped.remove( );
		}
	}
}