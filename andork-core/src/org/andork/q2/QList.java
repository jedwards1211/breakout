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
import java.util.Objects;

public abstract class QList<E, C extends List<E>> extends QCollection<E, C> implements List<E>
{
	public E get( int index )
	{
		return collection.get( index );
	}

	@Override
	public boolean add( E element )
	{
		int index = collection.size( );
		boolean result = collection.add( element );
		fireElemAdded( element );
		fireElemAdded( index , element );
		return result;
	}

	@Override
	public boolean addAll( Collection<? extends E> c )
	{
		int index = collection.size( );
		boolean result = collection.addAll( c );
		List<E> newValues = new ArrayList<>( c );
		fireElemsAdded( newValues );
		fireListChanged( QChange.ADDED , range( index , index + c.size( ) ) , null , newValues );
		return result;
	}

	@Override
	public void clear( )
	{
		if( !isEmpty( ) )
		{
			List<Integer> indices = range( 0 , collection.size( ) );
			List<E> oldValues = new ArrayList<>( collection );
			fireElemsRemoved( oldValues );
			fireListChanged( QChange.REMOVED , indices , oldValues , null );
		}
	}

	@Override
	public boolean remove( Object element )
	{
		int index = collection.indexOf( element );
		if( index >= 0 )
		{
			E removed = collection.remove( index );
			fireElemRemoved( removed );
			fireElemRemoved( index , removed );
		}
		return index >= 0;
	}

	@Override
	public boolean removeAll( Collection<?> c )
	{
		return batchRemove( Objects.requireNonNull( c ) , false );
	}

	@Override
	public boolean retainAll( Collection<?> c )
	{
		return batchRemove( Objects.requireNonNull( c ) , true );
	}

	private boolean batchRemove( Collection<?> c , boolean complement )
	{
		ListIterator<E> iter = collection.listIterator( );

		List<Integer> indices = new ArrayList<Integer>( );
		List<E> oldValues = new ArrayList<>( );

		while( iter.hasNext( ) )
		{
			int index = iter.nextIndex( );
			E elem = iter.next( );
			if( c.contains( elem ) != complement )
			{
				indices.add( index );
				oldValues.add( elem );
				iter.remove( );
			}
		}

		fireElemsRemoved( oldValues );
		fireListChanged( QChange.REMOVED , indices , oldValues , null );

		return !indices.isEmpty( );
	}

	public E set( int index , E newValue )
	{
		E oldValue = collection.set( index , newValue );
		if( oldValue != newValue )
		{
			fireElemRemoved( oldValue );
			fireElemAdded( newValue );
			fireElemReplaced( index , oldValue , newValue );
		}
		return oldValue;
	}

	public void add( int index , E element )
	{
		collection.add( index , element );
		fireElemAdded( element );
		fireElemAdded( index , element );
	}

	public E remove( int index )
	{
		E removed = collection.remove( index );
		fireElemRemoved( removed );
		fireElemRemoved( index , removed );
		return removed;
	}

	@Override
	public boolean addAll( int index , Collection<? extends E> c )
	{
		List<Integer> indices = range( index , index + c.size( ) );
		List<E> newValues = new ArrayList<>( c );
		boolean result = collection.addAll( index , c );
		fireElemsAdded( newValues );
		fireListChanged( QChange.ADDED , indices , null , newValues );
		return result;
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
		return new ListIter( collection , 0 );
	}

	@Override
	public ListIterator<E> listIterator( int index )
	{
		return new ListIter( collection , index , 0 );
	}

	@Override
	public List<E> subList( int fromIndex , int toIndex )
	{
		return new SubList( collection , fromIndex , toIndex );
	}

	public void addListener( QListListener<? super E> listener )
	{
		super.addListener( listener );
	}

	public void removeListener( QListListener<? super E> listener )
	{
		super.removeListener( listener );
	}

	@SuppressWarnings( "unchecked" )
	protected void fireElemAdded( int index , E newValue )
	{
		forEachListener( QListListener.class ,
			l -> l.listChanged( this , QChange.ADDED , index , null , newValue ) );
	}

	@SuppressWarnings( "unchecked" )
	protected void fireElemRemoved( int index , E oldValue )
	{
		forEachListener( QListListener.class ,
			l -> l.listChanged( this , QChange.REMOVED , index , oldValue , null ) );
	}

	@SuppressWarnings( "unchecked" )
	protected void fireElemReplaced( int index , E oldValue , E newValue )
	{
		forEachListener( QListListener.class ,
			l -> l.listChanged( this , QChange.REPLACED , index , oldValue , newValue ) );
	}

	@SuppressWarnings( "unchecked" )
	protected void fireListChanged( QChange change , List<Integer> indices , List<E> oldValues ,
		List<E> newValues )
	{
		forEachListener( QListListener.class ,
			l -> l.listChanged( this , change , indices , oldValues , newValues ) );
	}

	private List<Integer> range( int start , int end )
	{
		List<Integer> result = new ArrayList<Integer>( );
		for( int i = start ; i < end ; i++ )
		{
			result.add( i );
		}
		return result;
	}

	protected class ListIter implements ListIterator<E>
	{
		private int offset;
		private ListIterator<E> wrapped;
		private E last;
		private int lastIndex;

		public ListIter( List<E> list , int offset )
		{
			wrapped = list.listIterator( );
			this.offset = offset;
		}

		public ListIter( List<E> list , int index , int offset )
		{
			wrapped = list.listIterator( index );
			this.offset = offset;
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
			lastIndex = wrapped.nextIndex( );
			return last = wrapped.next( );
		}

		@Override
		public E previous( )
		{
			lastIndex = wrapped.previousIndex( );
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
			fireElemRemoved( last );
			fireElemRemoved( lastIndex + offset , last );
		}

		@Override
		public void set( E e )
		{
			if( last != e )
			{
				wrapped.set( e );
				fireElemRemoved( last );
				fireElemAdded( e );
				fireElemReplaced( lastIndex + offset , last , e );
				last = e;
			}
		}

		@Override
		public void add( E e )
		{
			int index = wrapped.nextIndex( );
			wrapped.add( e );
			fireElemAdded( e );
			fireElemAdded( index + offset , e );
		}
	}

	protected class SubList implements List<E>
	{
		private int offset;
		private List<E> wrapped;

		public SubList( List<E> list , int fromIndex , int toIndex )
		{
			wrapped = list.subList( fromIndex , toIndex );
			offset = fromIndex;
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
			int index = wrapped.size( );
			boolean result = wrapped.add( e );
			fireElemAdded( e );
			fireElemAdded( index + offset , e );
			return result;
		}

		@Override
		public boolean remove( Object o )
		{
			int index = wrapped.indexOf( o );
			if( index >= 0 )
			{
				E removed = wrapped.remove( index );
				fireElemRemoved( removed );
				fireElemRemoved( index + offset , removed );
			}
			return index >= 0;
		}

		@Override
		public boolean containsAll( Collection<?> c )
		{
			return wrapped.containsAll( c );
		}

		@Override
		public boolean addAll( Collection<? extends E> c )
		{
			int index = wrapped.size( );
			boolean result = wrapped.addAll( c );
			List<E> newValues = new ArrayList<>( c );
			fireElemsAdded( newValues );
			fireListChanged( QChange.ADDED , range( index + offset , index + offset + c.size( ) ) , null ,
				newValues );
			return result;
		}

		@Override
		public boolean addAll( int index , Collection<? extends E> c )
		{
			List<Integer> indices = range( index + offset , index + offset + c.size( ) );
			List<E> newValues = new ArrayList<>( c );
			boolean result = wrapped.addAll( index , c );
			fireElemsAdded( newValues );
			fireListChanged( QChange.ADDED , indices , null , newValues );
			return result;
		}

		@Override
		public boolean removeAll( Collection<?> c )
		{
			return batchRemove( c , false );
		}

		@Override
		public boolean retainAll( Collection<?> c )
		{
			return batchRemove( c , true );
		}

		private boolean batchRemove( Collection<?> c , boolean complement )
		{
			ListIterator<E> iter = wrapped.listIterator( );

			List<Integer> indices = new ArrayList<Integer>( );
			List<E> oldValues = new ArrayList<>( );

			while( iter.hasNext( ) )
			{
				int index = iter.nextIndex( );
				E elem = iter.next( );
				if( c.contains( elem ) != complement )
				{
					indices.add( index + offset );
					oldValues.add( elem );
					iter.remove( );
				}
			}

			fireElemsRemoved( oldValues );
			fireListChanged( QChange.REMOVED , indices , oldValues , null );

			return !indices.isEmpty( );
		}

		@Override
		public void clear( )
		{
			if( !isEmpty( ) )
			{
				List<Integer> indices = range( offset , offset + wrapped.size( ) );
				List<E> oldValues = new ArrayList<>( wrapped );
				fireElemsRemoved( oldValues );
				fireListChanged( QChange.REMOVED , indices , oldValues , null );
			}
		}

		@Override
		public E get( int index )
		{
			return wrapped.get( index );
		}

		@Override
		public E set( int index , E newValue )
		{
			E oldValue = wrapped.set( index , newValue );
			if( oldValue != newValue )
			{
				fireElemRemoved( oldValue );
				fireElemAdded( newValue );
				fireElemReplaced( index + offset , oldValue , newValue );
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
			fireElemRemoved( removed );
			fireElemRemoved( index + offset , removed );
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
			return new ListIter( this , offset );
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
