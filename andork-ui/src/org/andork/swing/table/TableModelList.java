package org.andork.swing.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.andork.q2.QObject;
import org.andork.q2.QObjectListener;
import org.andork.q2.QSpec;
import org.andork.q2.QSpec.Property;

/**
 * A list of {@link QObject}s designed specifically to back a {@link QObjectListTableModel}.
 * 
 * @author James
 *
 * @param <E>
 *            the {@link QSpec} for the {@link QObject}s in the list.
 */
public class TableModelList<E>
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
	}
}
