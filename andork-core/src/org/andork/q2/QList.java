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
import java.util.List;
import java.util.ListIterator;

public abstract class QList<E, C extends List<E>> extends QCollection<E, C> implements List<E>
{
	public E get( int index )
	{
		return collection.get( index );
	}

	public E set( int index , E element )
	{
		E oldValue = collection.get( index );
		if( oldValue != element )
		{
			collection.set( index , element );
			handleChildRemoved( oldValue );
			handleChildAdded( element );
		}
		return oldValue;
	}

	public void add( int index , E element )
	{
		collection.add( index , element );
		handleChildAdded( element );
	}

	public E remove( int index )
	{
		E removed = collection.remove( index );
		handleChildRemoved( removed );
		return removed;
	}

	@Override
	public boolean addAll( int index , Collection<? extends E> c )
	{
		collection.addAll( index , c );
		handleChildrenAdded( c.toArray( ) );
		return true;
	}

	@Override
	public int indexOf( Object o )
	{
		return collection.indexOf( o );
	}

	@Override
	public int lastIndexOf( Object o )
	{
		return collection.lastIndexOf( o );
	}

	@Override
	public ListIterator<E> listIterator( )
	{
		return new ListIter( collection );
	}

	@Override
	public ListIterator<E> listIterator( int index )
	{
		return new ListIter( collection , index );
	}

	@Override
	public List<E> subList( int fromIndex , int toIndex )
	{
		return new SubList( collection , fromIndex , toIndex );
	}

	protected class ListIter implements ListIterator<E>
	{
		private ListIterator<E>	wrapped;
		private E				last;

		public ListIter( List<E> list )
		{
			wrapped = list.listIterator( );
		}

		public ListIter( List<E> list , int index )
		{
			wrapped = list.listIterator( index );
		}

		@Override
		public boolean hasNext( )
		{
			return wrapped.hasNext( );
		}

		@Override
		public boolean hasPrevious( )
		{
			return wrapped.hasPrevious( );
		}

		@Override
		public E next( )
		{
			return last = wrapped.next( );
		}

		@Override
		public E previous( )
		{
			return last = wrapped.previous( );
		}

		@Override
		public int nextIndex( )
		{
			return wrapped.nextIndex( );
		}

		@Override
		public int previousIndex( )
		{
			return wrapped.previousIndex( );
		}

		@Override
		public void remove( )
		{
			wrapped.remove( );
			handleChildRemoved( last );
		}

		@Override
		public void set( E e )
		{
			if( last != e )
			{
				wrapped.set( e );
				handleChildRemoved( last );
				handleChildAdded( e );
				last = e;
			}
		}

		@Override
		public void add( E e )
		{
			wrapped.add( e );
			handleChildAdded( e );
		}
	}

	protected class SubList implements List<E>
	{
		private List<E>	wrapped;

		public SubList( List<E> list , int fromIndex , int toIndex )
		{
			wrapped = list.subList( fromIndex , toIndex );
		}

		@Override
		public int size( )
		{
			return wrapped.size( );
		}

		@Override
		public boolean isEmpty( )
		{
			return wrapped.isEmpty( );
		}

		@Override
		public boolean contains( Object o )
		{
			return wrapped.contains( o );
		}

		@Override
		public java.util.Iterator<E> iterator( )
		{
			return new Iter( wrapped );
		}

		@Override
		public Object[ ] toArray( )
		{
			return wrapped.toArray( );
		}

		@Override
		public <T> T[ ] toArray( T[ ] a )
		{
			return wrapped.toArray( a );
		}

		@Override
		public boolean add( E e )
		{
			if( wrapped.add( e ) )
			{
				handleChildAdded( e );
				return true;
			}
			return false;
		}

		@Override
		public boolean remove( Object o )
		{
			if( collection.remove( o ) )
			{
				handleChildRemoved( o );
				return true;
			}
			return false;
		}

		@Override
		public boolean containsAll( Collection<?> c )
		{
			return wrapped.containsAll( c );
		}

		@Override
		public boolean addAll( Collection<? extends E> c )
		{
			List<E> added = new ArrayList<E>( );
			for( E e : c )
			{
				if( wrapped.add( e ) )
				{
					added.add( e );
				}
			}
			if( !added.isEmpty( ) )
			{
				handleChildrenAdded( added.toArray( ) );
			}
			return !added.isEmpty( );
		}

		@Override
		public boolean addAll( int index , Collection<? extends E> c )
		{
			wrapped.addAll( index , c );
			handleChildrenAdded( c.toArray( ) );
			return true;
		}

		@Override
		public boolean removeAll( Collection<?> c )
		{
			List<Object> removed = new ArrayList<Object>( );
			for( Object e : c )
			{
				if( wrapped.remove( e ) )
				{
					removed.add( e );
				}
			}
			if( !removed.isEmpty( ) )
			{
				handleChildrenRemoved( removed );
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
				handleChildrenRemoved( removed.toArray( ) );
			}
			return !removed.isEmpty( );
		}

		@Override
		public void clear( )
		{
			if( !isEmpty( ) )
			{
				Object[ ] removed = toArray( );
				wrapped.clear( );
				handleChildrenRemoved( removed );
			}
		}

		@Override
		public E get( int index )
		{
			return wrapped.get( index );
		}

		@Override
		public E set( int index , E element )
		{
			E oldValue = wrapped.get( index );
			if( oldValue != element )
			{
				wrapped.set( index , element );
				handleChildRemoved( oldValue );
				handleChildAdded( element );
			}
			return oldValue;
		}

		@Override
		public void add( int index , E element )
		{
			wrapped.add( index , element );
		}

		@Override
		public E remove( int index )
		{
			E removed = wrapped.remove( index );
			handleChildRemoved( removed );
			return removed;
		}

		@Override
		public int indexOf( Object o )
		{
			return wrapped.indexOf( o );
		}

		@Override
		public int lastIndexOf( Object o )
		{
			return wrapped.lastIndexOf( o );
		}

		@Override
		public ListIterator<E> listIterator( )
		{
			return new ListIter( this );
		}

		@Override
		public ListIterator<E> listIterator( int index )
		{
			return new ListIter( this , index );
		}

		@Override
		public List<E> subList( int fromIndex , int toIndex )
		{
			return new SubList( this , fromIndex , toIndex );
		}
	}
}
