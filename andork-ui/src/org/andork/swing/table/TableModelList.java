package org.andork.swing.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.andork.q2.QObject;
import org.andork.q2.QSpec;

/**
 * A list of {@link QObject}s designed specifically to back a {@link QObjectListTableModel}.
 * 
 * @author James
 *
 * @param <E>
 *            the {@link QSpec} for the {@link QObject}s in the list.
 */
public class TableModelList<E> implements Iterable<E>
{
	private final List<E>			elements	= new ArrayList<E>( );

	private final List<Listener<E>>	listeners	= new ArrayList<>( );

	public int size( )
	{
		return elements.size( );
	}

	public boolean isEmpty( )
	{
		return elements.isEmpty( );
	}

	public E get( int index )
	{
		return elements.get( index );
	}

	public void set( int index , E element )
	{
		E previous = elements.set( index , element );
		if( !previous.equals( element ) )
		{
			fireElementsUpdated( index , index );
		}
	}

	public void add( E element )
	{
		add( elements.size( ) , element );
	}

	public void add( int index , E element )
	{
		elements.add( index , element );
		fireElementsInserted( index , index );
	}

	public void addAll( Collection<? extends E> elements )
	{
		addAll( elements.size( ) , elements );
	}

	public void addAll( int index , Collection<? extends E> elements )
	{
		this.elements.addAll( index , elements );
		fireElementsInserted( index , index + elements.size( ) - 1 );
	}

	public void remove( int index )
	{
		elements.remove( index );
		fireElementsDeleted( index , index );
	}

	public void remove( E element )
	{
		int index = elements.indexOf( element );
		if( index >= 0 )
		{
			remove( index );
		}
	}

	public void removeSublist( int fromIndex , int toIndex )
	{
		elements.subList( fromIndex , toIndex ).clear( );
		fireElementsDeleted( fromIndex , toIndex - 1 );
	}

	public void clear( )
	{
		int size = elements.size( );
		elements.clear( );
		fireElementsDeleted( 0 , size - 1 );
	}

	@Override
	public Iterator<E> iterator( )
	{
		return new Iter( );
	}

	private class Iter implements Iterator<E>
	{
		ListIterator<E>	wrapped;
		int				lastIndex;

		public Iter( )
		{
			wrapped = elements.listIterator( );
		}

		@Override
		public boolean hasNext( )
		{
			return wrapped.hasNext( );
		}

		@Override
		public E next( )
		{
			lastIndex = wrapped.nextIndex( );
			return wrapped.next( );
		}

		public void remove( )
		{
			wrapped.remove( );
			fireElementsDeleted( lastIndex , lastIndex );
		}
	}

	public void addListener( Listener<E> listener )
	{
		if( !listeners.contains( listener ) )
		{
			listeners.add( listener );
		}
	}

	public void removeListener( Listener<E> listener )
	{
		listeners.remove( listener );
	}

	public void fireElementsUpdated( int fromIndex , int toIndex )
	{
		for( Listener<E> listener : listeners )
		{
			listener.elementsUpdated( this , fromIndex , toIndex );
		}
	}

	public void fireDataChanged( )
	{
		for( Listener<E> listener : listeners )
		{
			listener.dataChanged( this );
		}
	}

	public void fireStructureChanged( )
	{
		for( Listener<E> listener : listeners )
		{
			listener.structureChanged( this );
		}
	}

	protected void fireElementsInserted( int fromIndex , int toIndex )
	{
		for( Listener<E> listener : listeners )
		{
			listener.elementsInserted( this , fromIndex , toIndex );
		}
	}

	protected void fireElementsDeleted( int fromIndex , int toIndex )
	{
		for( Listener<E> listener : listeners )
		{
			listener.elementsDeleted( this , fromIndex , toIndex );
		}
	}

	public static interface Listener<E>
	{
		public void elementsInserted( TableModelList<E> list , int fromIndex , int toIndex );

		public void elementsDeleted( TableModelList<E> list , int fromIndex , int toIndex );

		public void elementsUpdated( TableModelList<E> list , int fromIndex , int toIndex );

		public void dataChanged( TableModelList<E> list );

		public void structureChanged( TableModelList<E> list );
	}
}